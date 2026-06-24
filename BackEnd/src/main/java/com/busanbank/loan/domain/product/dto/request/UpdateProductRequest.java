package com.busanbank.loan.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(

        @NotBlank
        String productName,

        @NotNull
        BigDecimal baseRate,

        String loanPeriod,

        String status,

        List<CreateProductRequest.DescriptionItem> descriptions,

        List<CreateProductRequest.PreferentialRateItem> preferentialRates,

        List<CreateProductRequest.TermsItem> terms
) {}
