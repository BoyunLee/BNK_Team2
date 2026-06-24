package com.busanbank.loan.domain.loan.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConditionsResponse(
        Long loanAmount,
        BigDecimal appliedBaseRate,
        BigDecimal totalPreferentialRate,
        BigDecimal finalRate,
        String repaymentType,
        LocalDate maturityDate
) {}
