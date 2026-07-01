package com.busanbank.loan.domain.chat.service;

import com.busanbank.loan.domain.chat.config.ChatProperties;
import com.busanbank.loan.domain.chat.dto.request.ChatRequest;
import com.busanbank.loan.domain.chat.dto.response.ChatResponse;
import com.busanbank.loan.domain.chat.entity.ChatMessage;
import com.busanbank.loan.domain.chat.repository.ChatMessageRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 챗봇 오케스트레이션: 이력 로딩 → 질문 임베딩 → 벡터 검색 → 프롬프트 조립 → 답변 생성 → 저장.
 * (CHATBOT_SPEC.md 3장 흐름)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            당신은 부산은행 대출 상품 상담원입니다.

            [역할]
            - 고객의 대출 상품 상담에 친절하고 정중한 존댓말로 답변합니다.

            [답변 규칙]
            - 특정 상품의 금리·한도·조건·자격요건 등 '구체적 수치와 사실'은 반드시 아래 '상품 정보'에
              있는 내용만 근거로 답변하세요. 상품 정보에 없는 수치는 지어내지 말고, 모른다고 안내하세요.
            - 다만 고객이 상담 도중 금융 용어·개념(예: 고정/변동금리, DSR, 중도상환수수료, 신용점수 등)을
              물어보면, 일반적인 금융 지식으로 이해하기 쉽게 설명해 드리세요.
              (이때 특정 상품의 수치로 단정하지 말고, 일반적인 설명임을 밝히세요.)
            - 대출·금융과 무관한 주제(예: 날씨, 요리, 여행, 잡담 등)에는 답변하지 말고,
              정중히 안내한 뒤 대출·금융 상담으로 자연스럽게 유도하세요.
            - 대출 승인 여부나 적용 금리를 확정적으로 약속하지 마세요.
              (예: "반드시 승인됩니다", "금리는 정확히 O%입니다" 등의 단정 표현 금지)
            - 실제 한도·금리·승인은 심사 결과에 따라 달라질 수 있음을 안내하세요.
            - 개인정보(주민번호, 계좌번호 등)는 요구하지 마세요.

            [상품 정보]
            __PRODUCT_CONTEXT__
            """;

    /**
     * 사내 상품 데이터에서 일치하는 상품을 찾지 못했을 때 사용하는 '금융 일반 상담' 프롬프트.
     * 특정 상품 수치를 지어내지 않고 금융 일반 지식으로 답하되, 금융 외 주제는 정중히 거절한다.
     */
    private static final String GENERAL_PROMPT = """
            당신은 부산은행 대출 상품 상담원입니다.

            [역할]
            - 고객의 대출·금융 관련 질문에 친절하고 정중한 존댓말로 답변합니다.
            - 지금은 사내 상품 데이터에서 일치하는 상품을 찾지 못한 상황입니다.
              특정 상품의 금리·한도·조건을 지어내지 말고, 금융 일반 지식으로 설명하세요.

            [답변 범위]
            - 대출·예금·금리·신용·상환·금융 용어·경제 상식 등 '금융 전반'의 일반적인 질문에 답변합니다.
            - 금융과 무관한 주제(예: 날씨, 요리, 여행, 코딩, 잡담 등)에는 답변하지 마세요.
              대신 "대출·금융 관련 상담만 도와드릴 수 있다"고 정중히 안내하고, 대출 상담으로 자연스럽게 유도하세요.

            [답변 규칙]
            - 특정 부산은행 상품의 정확한 금리·한도·승인 여부를 단정하지 마세요.
              실제 조건은 상품 안내와 심사 결과에 따라 달라질 수 있음을 안내하세요.
            - 확실하지 않은 내용은 단정하지 말고, 일반적인 정보임을 밝히세요.
            - 구체적인 상품 가입·조건 확인은 부산은행 고객센터(1588-6200) 또는 상담사 연결을 권유하세요.
            - 개인정보(주민번호, 계좌번호 등)는 요구하지 마세요.
            """;

    private final ChatMessageRepository chatMessageRepository;
    private final GeminiClient geminiClient;
    private final VectorSearchService vectorSearchService;
    private final ChatProperties chatProperties;

    @Transactional
    public ChatResponse chat(ChatRequest request, Long customerId) {
        if (!geminiClient.isEnabled()) {
            throw new BusinessException(ErrorCode.CHAT_DISABLED);
        }

        String sessionId = (request.sessionId() == null || request.sessionId().isBlank())
                ? UUID.randomUUID().toString()
                : request.sessionId();

        List<ChatMessage> history = loadHistory(sessionId);

        // ② 질문 임베딩 → ③ 벡터 검색
        float[] queryVector = embedQuery(request.message());
        List<RetrievedProduct> retrieved = searchProducts(queryVector);

        // 유사도 임계값 미달 → 매칭 상품 없음. 상담사 연결로 끊지 않고, 금융 일반 상담으로 이어간다.
        // (금융 외 주제는 GENERAL_PROMPT 의 [답변 범위] 가드로 LLM 이 답변 시점에 정중히 거절)
        boolean noMatch = retrieved.isEmpty()
                || retrieved.get(0).score() < chatProperties.getSimilarityThreshold();

        // ④ 프롬프트 조립 → ⑤ 답변 생성
        String systemPrompt;
        List<String> referencedProducts;
        if (noMatch) {
            systemPrompt = GENERAL_PROMPT;
            referencedProducts = Collections.emptyList();
        } else {
            systemPrompt = buildSystemPrompt(retrieved);
            referencedProducts = retrieved.stream()
                    .map(RetrievedProduct::productCode)
                    .filter(code -> code != null && !code.isBlank())
                    .toList();
        }

        String answer = generateAnswer(systemPrompt, history, request.message());

        // ⑥ 저장 (일반 상담이면 참조 상품 없음 → null)
        String referenced = referencedProducts.isEmpty() ? null : String.join(",", referencedProducts);
        persist(sessionId, customerId, request.message(), answer, referenced);

        // fallback 플래그: 상품 근거 없이 일반 지식으로 답한 경우 true
        return new ChatResponse(sessionId, answer, referencedProducts, noMatch);
    }

    private float[] embedQuery(String message) {
        try {
            return geminiClient.embed(message);
        } catch (Exception e) {
            log.error("질문 임베딩 실패", e);
            throw new BusinessException(ErrorCode.CHAT_LLM_UNAVAILABLE);
        }
    }

    private List<RetrievedProduct> searchProducts(float[] queryVector) {
        try {
            return vectorSearchService.search(queryVector, chatProperties.getTopK());
        } catch (Exception e) {
            log.error("벡터 검색 실패", e);
            throw new BusinessException(ErrorCode.CHAT_SEARCH_UNAVAILABLE);
        }
    }

    private String generateAnswer(String systemPrompt, List<ChatMessage> history, String message) {
        try {
            return geminiClient.generate(systemPrompt, history, message);
        } catch (Exception e) {
            log.error("답변 생성 실패", e);
            throw new BusinessException(ErrorCode.CHAT_LLM_UNAVAILABLE);
        }
    }

    /** 세션의 최근 대화를 시간순(오래된→최신)으로 반환. */
    private List<ChatMessage> loadHistory(String sessionId) {
        List<ChatMessage> recentDesc = chatMessageRepository.findBySessionIdOrderByCreatedAtDesc(
                sessionId, PageRequest.of(0, chatProperties.getHistoryLimit() * 2));
        List<ChatMessage> ordered = new ArrayList<>(recentDesc);
        Collections.reverse(ordered);
        return ordered;
    }

    private String buildSystemPrompt(List<RetrievedProduct> retrieved) {
        StringBuilder context = new StringBuilder();
        for (RetrievedProduct p : retrieved) {
            context.append("─ ").append(p.productName()).append('\n')
                   .append(p.text()).append("\n\n");
        }
        // 상품 텍스트에 금리 '%' 등이 포함되므로 String.format 대신 replace 사용(포맷 지정자 오인 방지)
        return SYSTEM_PROMPT_TEMPLATE.replace("__PRODUCT_CONTEXT__", context.toString().strip());
    }

    private void persist(String sessionId, Long customerId, String userMessage,
                         String assistantMessage, String referencedProducts) {
        chatMessageRepository.save(ChatMessage.builder()
                .sessionId(sessionId)
                .customerId(customerId)
                .role(ChatMessage.ROLE_USER)
                .content(userMessage)
                .build());

        chatMessageRepository.save(ChatMessage.builder()
                .sessionId(sessionId)
                .customerId(customerId)
                .role(ChatMessage.ROLE_ASSISTANT)
                .content(assistantMessage)
                .referencedProducts(referencedProducts)
                .build());
    }
}
