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
     * 전체 상품을 재임베딩하여 Qdrant에 적재한다.
     * @return 적재한 상품 수
     */
    public int reindexAll() {
        vectorSearchService.ensureCollection(geminiProperties.getEmbeddingDimension());

        List<LoanProduct> products = loanProductRepository.findAll();
        int indexed = 0;

        for (LoanProduct product : products) {
            try {
                String text = buildProductText(product);
                float[] vector = geminiClient.embed(text);
                vectorSearchService.upsert(product.getProductId(), vector, buildPayload(product, text));
                indexed++;
            } catch (Exception e) {
                log.error("상품 임베딩 실패: productId={}", product.getProductId(), e);
            }
        }

        log.info("상품 벡터 인덱싱 완료 — 총 {}개 중 {}개 적재", products.size(), indexed);
        return indexed;
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
            String label = KEY_LABELS.getOrDefault(d.getAttrKey(), d.getAttrKey());
            sb.append(label).append(": ").append(d.getAttrValue()).append('\n');
        }
        return sb.toString().strip();
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
