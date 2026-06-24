package com.busanbank.loan.domain.loan.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ExecuteRequest(
        @NotBlank String simplePassword
) {}
