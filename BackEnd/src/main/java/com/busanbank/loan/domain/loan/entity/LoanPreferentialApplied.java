package com.busanbank.loan.domain.loan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "LOAN_PREFERENTIAL_APPLIED")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanPreferentialApplied {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applied_id")
    private Long appliedId;

    @Column(name = "loan_account_no", nullable = false)
    private String loanAccountNo;

    @Column(name = "preferential_id", nullable = false)
    private Long preferentialId;

    @Column(name = "applied_rate_value", precision = 5, scale = 2)
    private BigDecimal appliedRateValue;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public LoanPreferentialApplied(String loanAccountNo, Long preferentialId, BigDecimal appliedRateValue) {
        this.loanAccountNo = loanAccountNo;
        this.preferentialId = preferentialId;
        this.appliedRateValue = appliedRateValue;
        this.createdAt = LocalDateTime.now();
    }
}
