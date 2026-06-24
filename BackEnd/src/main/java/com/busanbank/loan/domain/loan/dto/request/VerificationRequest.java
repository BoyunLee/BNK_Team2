package com.busanbank.loan.domain.loan.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerificationRequest(
        @NotBlank String simplePassword
) {}
