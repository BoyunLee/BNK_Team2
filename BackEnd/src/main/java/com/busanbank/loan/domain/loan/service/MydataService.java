package com.busanbank.loan.domain.loan.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class MydataService {

    public MydataResult fetchPublicData(String loanAccountNo, Long annualIncome) {
        return MydataResult.builder()
                .incomeVerified(true)
                .employmentVerified(true)
                .taxAnnualIncome(annualIncome)
                .taxYear(2025)
                .nationalPensionMonthly(150000L)
                .build();
    }

    @Getter
    @Builder
    public static class MydataResult {
        private boolean incomeVerified;
        private boolean employmentVerified;
        private Long taxAnnualIncome;
        private Integer taxYear;
        private Long nationalPensionMonthly;
    }
}
