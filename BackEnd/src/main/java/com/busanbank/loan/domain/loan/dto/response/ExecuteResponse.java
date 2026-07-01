package com.busanbank.loan.domain.loan.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExecuteResponse(
        String loanAccountNo,
        Long loanAmount,
        BigDecimal finalRate,
        LocalDate maturityDate,
        String depositAccountNo,
        String loanDepositAccountNo,
        LocalDateTime executionDate
) {}
