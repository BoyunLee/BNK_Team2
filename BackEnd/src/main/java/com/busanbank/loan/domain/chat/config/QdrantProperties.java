package com.busanbank.loan.domain.chat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Qdrant 벡터 DB 연동 설정 (REST API).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "qdrant")
public class QdrantProperties {

    /** Qdrant REST base URL. 로컬 Docker 기본값. */
    private String baseUrl = "http://localhost:6333";

    /** 대출 상품 벡터 컬렉션 이름. */
    private String collection = "loan-products";

    /** Qdrant Cloud 사용 시 API 키 (로컬은 비워둠). */
    private String apiKey;
}
