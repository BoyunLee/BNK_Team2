package com.busanbank.loan.domain.product.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.busanbank.loan.domain.product.entity.ProductDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AiSummaryService {

    private static final Map<String, String> KEY_LABELS = Map.of(
            "LOAN_TYPE",  "대출 종류",
            "ELIGIBLE",   "대출 대상",
            "LOAN_LIMIT", "대출 한도",
            "RATE_INFO",  "금리 정보",
            "REPAYMENT",  "상환 방법",
            "FEE",        "수수료",
            "CAUTION",    "유의사항"
    );

    @Autowired(required = false)
    private AnthropicClient anthropicClient;

    /**
     * Claude API를 호출하여 상품 설명 요약을 생성합니다.
     * API 키가 설정되지 않은 경우 null을 반환합니다.
     */
    public Optional<String> generateSummary(String productName, List<ProductDescription> descriptions) {
        if (anthropicClient == null) {
            log.warn("ANTHROPIC_API_KEY 미설정 — AI 요약 건너뜀: {}", productName);
            return Optional.empty();
        }

        if (descriptions.isEmpty()) {
            return Optional.empty();
        }

        String descText = descriptions.stream()
                .map(d -> KEY_LABELS.getOrDefault(d.getAttrKey(), d.getAttrKey()) + ": " + d.getAttrValue())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "\n" + b);

        String prompt = String.format("""
                당신은 부산은행 금융상품 전문 작성자입니다.

                다음은 대출 상품 '%s'의 상품 정보입니다:

                %s

                위 정보를 바탕으로, 이 대출 상품의 핵심 특징을 고객이 이해하기 쉽게 2~3문장으로 간결하게 요약해주세요.
                - 고객 친화적인 언어를 사용하세요.
                - 주요 혜택과 대상 고객을 중심으로 서술하세요.
                - 요약문만 출력하고, 다른 설명이나 머리글은 포함하지 마세요.
                """, productName, descText);

        try {
            MessageCreateParams params = MessageCreateParams.builder()
                    .model("claude-opus-4-8")
                    .maxTokens(512L)
                    .addUserMessage(prompt)
                    .build();

            Message response = anthropicClient.messages().create(params);

            String summary = response.content().stream()
                    .flatMap(block -> block.text().stream())
                    .map(textBlock -> textBlock.text())
                    .findFirst()
                    .orElse(null);

            if (summary != null && !summary.isBlank()) {
                log.info("AI 요약 생성 완료: {} — {}자", productName, summary.length());
                return Optional.of(summary.strip());
            }

            return Optional.empty();

        } catch (Exception e) {
            log.error("Claude API 호출 실패 — 상품: {}", productName, e);
            return Optional.empty();
        }
    }
}
