package com.busanbank.loan.domain.loan.dto.request;

import java.util.List;

public record ContractConditionsRequest(
        String repaymentType,
        String rateTypeCode,
        String rateChangeCycle,
        String loanPeriod,
        String depositAccountNo,
        String fundPurpose,
        Long loanAmount,
        List<Long> preferentialIds
) {}
