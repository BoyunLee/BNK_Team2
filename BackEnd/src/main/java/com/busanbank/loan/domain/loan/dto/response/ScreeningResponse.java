package com.busanbank.loan.domain.loan.dto.response;

import java.math.BigDecimal;

public record ScreeningResponse(
        Long maxLimitAmt,
        BigDecimal appliedBaseRate,
        String result
) {}
