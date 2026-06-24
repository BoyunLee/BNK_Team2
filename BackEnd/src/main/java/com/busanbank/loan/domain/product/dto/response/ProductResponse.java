package com.busanbank.loan.domain.product.dto.response;

import com.busanbank.loan.domain.product.entity.LoanProduct;

import java.math.BigDecimal;

public record ProductResponse(
        Long productId,
        String productName,
        BigDecimal baseRate,
        String loanPeriod,
        String status
) {

    public static ProductResponse from(LoanProduct product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getBaseRate(),
                product.getLoanPeriod(),
                product.getStatus()
        );
    }
}
