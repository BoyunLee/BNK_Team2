package com.busanbank.loan.domain.chat.service;

import com.busanbank.loan.domain.chat.config.GeminiProperties;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.entity.ProductDescription;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.domain.product.repository.ProductDescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 대출 상품을 임베딩하여 Qdrant에 적재한다.
 *
 * <p>NOTE: 상품 CRUD 연동(증분 재임베딩)은 추후 관리자 API 명세에서 정식화한다.
 * 본 클래스는 전체 재적재(reindexAll) 기능만 제공하며, 챗봇 테스트용 시드 역할이다.
 * (CHATBOT_SPEC.md 9장 참고)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIndexService {

    private static final Map<String, String> KEY_LABELS = Map.of(
            "LOAN_TYPE",  "대출 종류",
            "ELIGIBLE",   "대출 대상",
            "TARGET",     "대출 대상",
            "LOAN_LIMIT", "대출 한도",
            "RATE_INFO",  "금리 정보",
            "REPAYMENT",  "상환 방법",
            "LOAN_TERM",  "대출 기간",
            "FEE",        "수수료",
            "CAUTION",    "유의사항"
    );

    private final LoanProductRepository loanProductRepository;
    private final ProductDescriptionRepository productDescriptionRepository;
    private final GeminiClient geminiClient;
    private final VectorSearchService vectorSearchService;
    private final GeminiProperties geminiProperties;

    /**
     * 전체 상품을 임베딩하여 Qdrant에 적재한다(upsert 누적).
     * - 임베딩 호출은 재시도+백오프, 호출 간 간격을 둬 버스트 레이트리밋을 회피.
     * - 부분 실패해도 이전 성공분은 유지되며, 재실행 시 빈 곳이 채워진다.
     * - 마지막에 현재 상품에 없는 stale 포인트를 정리.
     * @return 이번 호출에서 적재한 상품 수
     */
    public int reindexAll() {
        vectorSearchService.ensureCollection(geminiProperties.getEmbeddingDimension());

        List<LoanProduct> products = loanProductRepository.findAll();
        List<Long> currentIds = products.stream().map(LoanProduct::getProductId).toList();
        int indexed = 0;

        for (LoanProduct product : products) {
            try {
                String text = buildProductText(product);
                float[] vector = embedWithRetry(text);
                vectorSearchService.upsert(product.getProductId(), vector, buildPayload(product, text));
                indexed++;
                sleepQuietly(300); // 호출 간 간격(버스트 한도 회피)
            } catch (Exception e) {
                log.error("상품 임베딩 실패: productId={}", product.getProductId(), e);
            }
        }

        try {
            vectorSearchService.pruneExcept(currentIds);
        } catch (Exception e) {
            log.warn("stale 포인트 정리 실패: {}", e.getMessage());
        }

        log.info("상품 벡터 인덱싱 — 총 {}개 중 {}개 적재", products.size(), indexed);
        return indexed;
    }

    /** 임베딩을 재시도+지수백오프로 호출(레이트리밋/일시 오류 대응). */
    private float[] embedWithRetry(String text) {
        final int maxAttempts = 5;
        long backoffMs = 1000;
        RuntimeException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return geminiClient.embed(text);
            } catch (RuntimeException e) {
                last = e;
                log.warn("임베딩 재시도 {}/{} (대기 {}ms, 사유: {})", attempt, maxAttempts, backoffMs, e.getMessage());
                sleepQuietly(backoffMs);
                backoffMs = Math.min(backoffMs * 2, 16000);
            }
        }
        throw last;
    }

    private void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /** 상품명 + 캐치프레이즈 + 설명 항목들을 임베딩용 텍스트로 합친다. */
    private String buildProductText(LoanProduct product) {
        StringBuilder sb = new StringBuilder();
        sb.append("상품명: ").append(product.getProductName()).append('\n');
        if (product.getCategory() != null) {
            sb.append("카테고리: ").append(product.getCategory()).append('\n');
        }
        if (product.getCatchphrase() != null && !product.getCatchphrase().isBlank()) {
            sb.append("소개: ").append(product.getCatchphrase()).append('\n');
        }

        List<ProductDescription> descs = productDescriptionRepository
                .findAllByProductIdOrderBySortOrderAsc(product.getProductId());
        for (ProductDescription d : descs) {
            if ("AI_SUMMARY".equals(d.getAttrKey())) {
                continue;
            }
            String value = stripHtml(d.getAttrValue());
            if (value.isBlank()) {
                continue;
            }
            String label = KEY_LABELS.getOrDefault(d.getAttrKey(), d.getAttrKey());
            sb.append(label).append(": ").append(value).append('\n');
        }
        String text = sb.toString().strip();
        // 임베딩 입력 토큰 한도(약 2048) 대비 길이 캡 — HTML 제거 후에도 긴 상품 보호
        return text.length() > 8000 ? text.substring(0, 8000) : text;
    }

    /** HTML 태그/엔티티 제거 → 임베딩 입력을 깔끔하고 짧게(상품 설명이 HTML 본문임). */
    private static String stripHtml(String s) {
        if (s == null) return "";
        return s.replace("&nbsp;", " ")
                .replaceAll("<[^>]+>", " ")
                .replaceAll("&[a-zA-Z]+;", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /** Qdrant payload(메타데이터 + 임베딩 원문). null 값은 제외. (CHATBOT_SPEC.md 4.2) */
    private Map<String, Object> buildPayload(LoanProduct product, String text) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("productId", product.getProductId());
        payload.put("productName", product.getProductName());
        payload.put("status", product.getStatus());
        payload.put("text", text);
        putIfNotNull(payload, "productCode", product.getMkpdCd());
        putIfNotNull(payload, "category", product.getCategory());
        putIfNotNull(payload, "baseRate", product.getBaseRate());
        putIfNotNull(payload, "rateMin", product.getRateMin());
        putIfNotNull(payload, "rateMax", product.getRateMax());
        putIfNotNull(payload, "loanPeriod", product.getLoanPeriod());
        putIfNotNull(payload, "catchphrase", product.getCatchphrase());
        return payload;
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
