package com.busanbank.loan.global.config;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ClaudeConfig {

    @Bean
    @ConditionalOnProperty(name = "anthropic.api-key", matchIfMissing = false)
    public AnthropicClient anthropicClient(@Value("${anthropic.api-key}") String apiKey) {
        log.info("Claude API 클라이언트가 활성화되었습니다. AI 상품 요약 기능을 사용합니다.");
        return AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }
}
