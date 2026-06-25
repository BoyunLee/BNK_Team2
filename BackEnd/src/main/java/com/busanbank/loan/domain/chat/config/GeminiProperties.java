package com.busanbank.loan.domain.chat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Gemini API 연동 설정.
 * api-key 미설정 시 챗봇 기능은 비활성화된다(CHAT_DISABLED).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

    /** Gemini API 키 (Google AI Studio 발급). 미설정 시 챗봇 비활성화. */
    private String apiKey;

    /** Generative Language API base URL. */
    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta";

    /** 답변 생성 모델. */
    private String chatModel = "gemini-2.0-flash";

    /** 임베딩 모델. */
    private String embeddingModel = "text-embedding-004";

    /** 임베딩 차원 (Qdrant 컬렉션 vector size와 일치해야 함). text-embedding-004 = 768. */
    private int embeddingDimension = 768;

    /** 생성 temperature (낮을수록 보수적). */
    private double temperature = 0.2;

    /** 최대 출력 토큰. */
    private int maxOutputTokens = 1024;

    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }
}
