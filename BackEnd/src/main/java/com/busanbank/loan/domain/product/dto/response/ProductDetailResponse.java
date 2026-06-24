package com.busanbank.loan.domain.product.dto.response;

import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.entity.ProductDescription;
import com.busanbank.loan.domain.product.entity.ProductPreferentialRate;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long productId,
        String productName,
        BigDecimal baseRate,
        String loanPeriod,
        String status,
        List<DescriptionDto> descriptions,
        List<PreferentialRateDto> preferentialRates
) {

    public record DescriptionDto(
            String attrKey,
            String attrValue,
            int sortOrder
    ) {}

    public record PreferentialRateDto(
            Long preferentialId,
            String conditionCode,
            String conditionName,
            BigDecimal rateValue,
            String description
    ) {}

    public static ProductDetailResponse from(LoanProduct product,
                                             List<ProductDescription> descriptions,
                                             List<ProductPreferentialRate> preferentialRates) {
        List<DescriptionDto> descDtos = descriptions.stream()
                .map(d -> new DescriptionDto(d.getAttrKey(), d.getAttrValue(), d.getSortOrder()))
                .toList();

        List<PreferentialRateDto> rateDtos = preferentialRates.stream()
                .map(r -> new PreferentialRateDto(
                        r.getPreferentialId(),
                        r.getConditionCode(),
                        r.getConditionName(),
                        r.getRateValue(),
                        r.getDescription()))
                .toList();

        return new ProductDetailResponse(
                product.getProductId(),
                product.getProductName(),
                product.getBaseRate(),
                product.getLoanPeriod(),
                product.getStatus(),
                descDtos,
                rateDtos
        );
    }
}
