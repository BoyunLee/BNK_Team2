package com.busanbank.loan.domain.chat.service;

import com.busanbank.loan.domain.chat.config.QdrantProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Qdrant 벡터 DB(REST API) 연동.
 * - ensureCollection(): 컬렉션 생성(없을 때)
 * - upsert(): 상품 벡터 적재
 * - search(): 코사인 유사도 검색 (판매중 상품만 필터)
 */
@Slf4j
@Service
public class VectorSearchService {

    private final RestClient qdrantRestClient;
    private final QdrantProperties props;

    public VectorSearchService(@Qualifier("qdrantRestClient") RestClient qdrantRestClient,
                               QdrantProperties props) {
        this.qdrantRestClient = qdrantRestClient;
        this.props = props;
    }

    /** 컬렉션이 없으면 코사인 거리로 생성한다(이미 있으면 Qdrant가 멱등 처리). */
    public void ensureCollection(int vectorSize) {
        Map<String, Object> body = Map.of(
                "vectors", Map.of("size", vectorSize, "distance", "Cosine")
        );
        qdrantRestClient.put()
                .uri("/collections/{name}", props.getCollection())
                .body(body)
                .retrieve()
                .toBodilessEntity();
        log.info("Qdrant 컬렉션 준비 완료: {} (size={}, Cosine)", props.getCollection(), vectorSize);
    }

    /** 상품 한 건을 벡터로 upsert 한다. payload는 메타데이터 + 임베딩 원문(text). */
    public void upsert(long pointId, float[] vector, Map<String, Object> payload) {
        Map<String, Object> point = Map.of(
                "id", pointId,
                "vector", toList(vector),
                "payload", payload
        );
        Map<String, Object> body = Map.of("points", List.of(point));

        qdrantRestClient.put()
                .uri("/collections/{name}/points?wait=true", props.getCollection())
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * 질문 벡터로 유사 상품을 검색한다. status=SALE 상품만 대상.
     *
     * @return 유사도 내림차순 결과 (최대 topK개)
     */
    public List<RetrievedProduct> search(float[] queryVector, int topK) {
        Map<String, Object> filter = Map.of(
                "must", List.of(Map.of(
                        "key", "status",
                        "match", Map.of("value", "SALE")
                ))
        );
        Map<String, Object> body = Map.of(
                "vector", toList(queryVector),
                "limit", topK,
                "with_payload", true,
                "filter", filter
        );

        JsonNode res = qdrantRestClient.post()
                .uri("/collections/{name}/points/search", props.getCollection())
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        JsonNode result = res != null ? res.path("result") : null;
        List<RetrievedProduct> products = new ArrayList<>();
        if (result != null && result.isArray()) {
            for (JsonNode hit : result) {
                JsonNode payload = hit.path("payload");
                products.add(new RetrievedProduct(
                        payload.path("productCode").asText(""),
                        payload.path("productName").asText(""),
                        hit.path("score").asDouble(),
                        payload.path("text").asText("")
                ));
            }
        }
        return products;
    }

    private List<Float> toList(float[] vector) {
        List<Float> list = new ArrayList<>(vector.length);
        for (float v : vector) {
            list.add(v);
        }
        return list;
    }
}
