package com.busanbank.loan.domain.loan.dto.request;

public record SignatureRequest(
        String signStep,
        String signType,
        String tokenId,
        String originalValue
) {}
