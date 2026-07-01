package com.busanbank.loan.domain.chat.service;

import com.busanbank.loan.domain.chat.config.GeminiProperties;
import com.busanbank.loan.domain.chat.entity.ChatMessage;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gemini Generative Language API(REST) 연동.
 * - embed(): 텍스트 → 벡터
 * - generate(): 시스템 프롬프트 + 대화 이력 + 질문 → 답변 텍스트
 */
@Slf4j
@Component
public class GeminiClient {

    private final RestClient geminiRestClient;
    private final GeminiProperties props;

    public GeminiClient(@Qualifier("geminiRestClient") RestClient geminiRestClient,
                        GeminiProperties props) {
        this.geminiRestClient = geminiRestClient;
        this.props = props;
    }

    public boolean isEnabled() {
        return props.isEnabled();
    }

    /**
     * 텍스트를 임베딩 벡터로 변환한다.
     * @return float 배열 (길이 = embeddingDimension)
     */
    public float[] embed(String text) {
        String modelPath = "models/" + props.getEmbeddingModel();
        Map<String, Object> body = Map.of(
                "model", modelPath,
                "content", Map.of("parts", List.of(Map.of("text", text))),
                "outputDimensionality", props.getEmbeddingDimension(),
                "taskType", "RETRIEVAL_QUERY"
        );

        JsonNode res = geminiRestClient.post()
                .uri("/models/{model}:embedContent?key={key}", props.getEmbeddingModel(), props.getApiKey())
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        JsonNode values = res != null ? res.path("embedding").path("values") : null;
        if (values == null || !values.isArray() || values.isEmpty()) {
            throw new IllegalStateException("Gemini 임베딩 응답이 비어 있습니다.");
        }

        float[] vector = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            vector[i] = (float) values.get(i).asDouble();
        }
        return vector;
    }

    /**
     * 시스템 프롬프트 + 대화 이력 + 현재 질문으로 답변을 생성한다.
     *
     * @param systemPrompt 상담원 페르소나/규칙 + 검색된 상품 컨텍스트
     * @param history      직전 대화 이력(시간순)
     * @param userMessage  현재 질문
     */
    public String generate(String systemPrompt, List<ChatMessage> history, String userMessage) {
        return generate(systemPrompt, history, userMessage, props.getMaxOutputTokens());
    }

    /**
     * maxOutputTokens 를 명시적으로 지정해 답변을 생성한다.
     * 간결 답변(일반 상담·상세 요청 없음) 시 낮은 상한으로 분량을 하드 캡한다.
     */
    public String generate(String systemPrompt, List<ChatMessage> history, String userMessage,
                           int maxOutputTokens) {
        List<Map<String, Object>> contents = new ArrayList<>();
        for (ChatMessage m : history) {
            String role = m.isUser() ? "user" : "model";
            contents.add(Map.of("role", role, "parts", List.of(Map.of("text", m.getContent()))));
        }
        contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", userMessage))));

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))),
                "contents", contents,
                "generationConfig", Map.of(
                        "temperature", props.getTemperature(),
                        "maxOutputTokens", maxOutputTokens,
                        // gemini-2.5-flash 는 thinking 모델 — 기본값이면 추론 토큰이 maxOutputTokens 를
                        // 소진해 답변이 잘린다. 상담 답변엔 추론이 불필요하므로 thinking 을 끈다.
                        "thinkingConfig", Map.of("thinkingBudget", 0)
                )
        );

        JsonNode res = geminiRestClient.post()
                .uri("/models/{model}:generateContent?key={key}", props.getChatModel(), props.getApiKey())
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        JsonNode parts = res != null
                ? res.path("candidates").path(0).path("content").path("parts")
                : null;
        if (parts == null || !parts.isArray() || parts.isEmpty()) {
            throw new IllegalStateException("Gemini 응답에 생성된 텍스트가 없습니다.");
        }

        StringBuilder sb = new StringBuilder();
        for (JsonNode part : parts) {
            sb.append(part.path("text").asText(""));
        }
        String answer = sb.toString().strip();
        if (answer.isBlank()) {
            throw new IllegalStateException("Gemini 응답 텍스트가 비어 있습니다.");
        }
        return answer;
    }
}
