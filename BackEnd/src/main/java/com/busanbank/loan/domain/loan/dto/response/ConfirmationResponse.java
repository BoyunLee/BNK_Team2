package com.busanbank.loan.domain.loan.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ConfirmationResponse(
        String productName,
        String customerName,
        Long loanAmount,
        BigDecimal finalRate,
        String repaymentType,
        LocalDate maturityDate,
        String depositAccountNo,
        String fundPurpose,
        List<PreferentialRateInfo> preferentialRates
) {
    public record PreferentialRateInfo(
            String conditionName,
            BigDecimal rateValue
    ) {}
}
