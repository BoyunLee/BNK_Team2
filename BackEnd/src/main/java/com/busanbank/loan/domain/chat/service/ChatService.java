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
            - 고객의 대출 상품 관련 질문에 친절하고 정중한 존댓말로 답변합니다.

            [답변 규칙]
            - 반드시 아래 '상품 정보'에 포함된 내용만 근거로 답변하세요.
            - '상품 정보'에 없는 내용은 모른다고 안내하고, 추측하지 마세요.
            - 대출 승인 여부나 적용 금리를 확정적으로 약속하지 마세요.
              (예: "반드시 승인됩니다", "금리는 정확히 O%입니다" 등의 단정 표현 금지)
            - 실제 한도·금리·승인은 심사 결과에 따라 달라질 수 있음을 안내하세요.
            - 개인정보(주민번호, 계좌번호 등)는 요구하지 마세요.

            [상품 정보]
            __PRODUCT_CONTEXT__
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

        // 유사도 임계값 미달 → 관련 상품 없음(fallback)
        boolean noMatch = retrieved.isEmpty()
                || retrieved.get(0).score() < chatProperties.getSimilarityThreshold();
        if (noMatch) {
            String fallback = chatProperties.getFallbackMessage();
            persist(sessionId, customerId, request.message(), fallback, null);
            return new ChatResponse(sessionId, fallback, Collections.emptyList(), true);
        }

        // ④ 프롬프트 조립 → ⑤ 답변 생성
        String systemPrompt = buildSystemPrompt(retrieved);
        String answer = generateAnswer(systemPrompt, history, request.message());

        List<String> referencedProducts = retrieved.stream()
                .map(RetrievedProduct::productCode)
                .filter(code -> code != null && !code.isBlank())
                .toList();

        // ⑥ 저장
        persist(sessionId, customerId, request.message(), answer, String.join(",", referencedProducts));

        return new ChatResponse(sessionId, answer, referencedProducts, false);
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
