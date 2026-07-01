package com.busanbank.loan.domain.chat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 챗봇 동작 튜닝 설정. (CHATBOT_SPEC.md 5·6장 참고)
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "chat")
public class ChatProperties {

    /** 벡터 검색 결과 개수 (top-k). */
    private int topK = 3;

    /**
     * 최소 유사도 임계값 (코사인). 최상위 score가 이 값 미만이면 "관련 상품 없음" 처리.
     * 운영 중 튜닝. gemini-embedding-001 한국어 상품 매칭 점수대가 0.65~0.82여서
     * 0.70이면 실제 정답(예: 비상금 동백론 0.66, 원스피드론 0.65)도 컷된다.
     */
    private double similarityThreshold = 0.60;

    /** 프롬프트에 포함할 직전 대화 턴 수(메시지 쌍 기준). */
    private int historyLimit = 5;

    /** 관련 상품이 없을 때 반환할 안내 멘트. */
    private String fallbackMessage =
            "문의하신 내용과 관련된 대출 상품을 찾지 못했습니다. "
            + "정확한 상담을 위해 부산은행 고객센터(1588-6200) 또는 상담사 연결을 이용해 주세요.";
}
