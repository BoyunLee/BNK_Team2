package com.busanbank.loan.domain.chat.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 챗봇용 외부 REST 클라이언트(Gemini / Qdrant) 빈 구성.
 */
@Configuration
@EnableConfigurationProperties({GeminiProperties.class, QdrantProperties.class, ChatProperties.class})
public class ChatClientConfig {

    @Bean
    public RestClient geminiRestClient(GeminiProperties props) {
        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }

    @Bean
    public RestClient qdrantRestClient(QdrantProperties props) {
        RestClient.Builder builder = RestClient.builder().baseUrl(props.getBaseUrl());
        if (props.getApiKey() != null && !props.getApiKey().isBlank()) {
            builder.defaultHeader("api-key", props.getApiKey());
        }
        return builder.build();
    }
}
