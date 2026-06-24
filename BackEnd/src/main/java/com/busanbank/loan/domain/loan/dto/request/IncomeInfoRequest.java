package com.busanbank.loan.domain.loan.dto.request;

public record IncomeInfoRequest(
        String companyName,
        String jobType,
        String employmentType,
        Long annualIncome
) {}
