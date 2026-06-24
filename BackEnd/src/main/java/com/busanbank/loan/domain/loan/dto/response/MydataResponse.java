package com.busanbank.loan.domain.loan.dto.response;

import com.busanbank.loan.domain.loan.service.MydataService;

public record MydataResponse(
        boolean incomeVerified,
        boolean employmentVerified,
        TaxInfo taxInfo,
        NationalPension nationalPension
) {
    public record TaxInfo(Long annualIncome, Integer year) {}
    public record NationalPension(Long monthlyPremium) {}

    public static MydataResponse from(MydataService.MydataResult result) {
        return new MydataResponse(
                result.isIncomeVerified(),
                result.isEmploymentVerified(),
                new TaxInfo(result.getTaxAnnualIncome(), result.getTaxYear()),
                new NationalPension(result.getNationalPensionMonthly())
        );
    }
}
