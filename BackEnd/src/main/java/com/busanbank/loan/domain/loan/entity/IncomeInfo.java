package com.busanbank.loan.domain.loan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "INCOME_INFO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncomeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_id")
    private Long incomeId;

    @Column(name = "loan_account_no", nullable = false, length = 30)
    private String loanAccountNo;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "annual_income")
    private Long annualIncome;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public IncomeInfo(String loanAccountNo, Long customerId, String companyName,
                      String jobType, String employmentType, Long annualIncome) {
        this.loanAccountNo = loanAccountNo;
        this.customerId = customerId;
        this.companyName = companyName;
        this.jobType = jobType;
        this.employmentType = employmentType;
        this.annualIncome = annualIncome;
        this.createdAt = LocalDateTime.now();
    }
}
