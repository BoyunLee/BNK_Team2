package com.busanbank.loan.domain.admin.service;

import com.busanbank.loan.domain.admin.dto.ProductSnapshotDto;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.entity.ProductDescription;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** LoanProduct(+설명) -> ProductSnapshotDto 변환. 설명 항목 중 결재로 관리하는 키만 사용. */
@Component
public class ProductSnapshotMapper {

    // 결재 스냅샷이 관리하는 PRODUCT_DESCRIPTION 키
    public static final String K_TARGET = "TARGET";
    public static final String K_LIMIT = "LOAN_LIMIT";
    public static final String K_REPAYMENT = "REPAYMENT";
    public static final String K_SUMMARY = "SUMMARY";
    public static final String K_AI_SUMMARY = "AI_SUMMARY";

    public ProductSnapshotDto toSnapshot(LoanProduct p, List<ProductDescription> descriptions) {
        Map<String, String> m = descriptions.stream()
                .collect(Collectors.toMap(ProductDescription::getAttrKey, ProductDescription::getAttrValue, (a, b) -> a));

        String summary = m.getOrDefault(K_SUMMARY, m.getOrDefault(K_AI_SUMMARY, ""));

        return new ProductSnapshotDto(
                nz(p.getProductName()),
                nz(p.getCategory()),
                p.getBaseRate() != null ? p.getBaseRate().toPlainString() : "",
                nz(p.getRateMin()),
                nz(p.getRateMax()),
                nz(p.getLoanPeriod()),
                nz(p.getCatchphrase()),
                m.getOrDefault(K_TARGET, ""),
                m.getOrDefault(K_LIMIT, ""),
                m.getOrDefault(K_REPAYMENT, ""),
                summary
        );
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
