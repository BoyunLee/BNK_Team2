package com.busanbank.loan.domain.loan.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateApplicationRequest(
        @NotNull Long productId
) {}
