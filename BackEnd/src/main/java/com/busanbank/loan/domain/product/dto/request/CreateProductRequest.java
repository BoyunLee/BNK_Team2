package com.busanbank.loan.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(

        @NotBlank
        String productName,

        @NotNull
        BigDecimal baseRate,

        String loanPeriod,

        String status,

        List<DescriptionItem> descriptions,

        List<PreferentialRateItem> preferentialRates,

        List<TermsItem> terms
) {

    public record DescriptionItem(
            String attrKey,
            String attrValue,
            int sortOrder
    ) {}

    public record PreferentialRateItem(
            String conditionCode,
            String conditionName,
            BigDecimal rateValue,
            String description
    ) {}

    public record TermsItem(
            String termsType,
            String termsPath
    ) {}
}
