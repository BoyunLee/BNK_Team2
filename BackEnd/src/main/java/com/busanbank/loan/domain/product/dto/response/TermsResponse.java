package com.busanbank.loan.domain.product.dto.response;

public record TermsResponse(
        Long termsId,
        int termsSeq,
        String termsType,
        String termsPath
) {}
