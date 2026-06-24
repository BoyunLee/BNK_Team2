package com.busanbank.loan.domain.loan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "LOAN_SCREENING")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanScreening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screening_id")
    private Long screeningId;

    @Column(name = "loan_account_no", nullable = false)
    private String loanAccountNo;

    @Column(name = "max_limit_amt")
    private Long maxLimitAmt;

    @Column(name = "applied_base_rate", precision = 5, scale = 2)
    private BigDecimal appliedBaseRate;

    @Column(name = "result")
    private String result;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public LoanScreening(String loanAccountNo, Long maxLimitAmt, BigDecimal appliedBaseRate, String result) {
        this.loanAccountNo = loanAccountNo;
        this.maxLimitAmt = maxLimitAmt;
        this.appliedBaseRate = appliedBaseRate;
        this.result = result;
        this.createdAt = LocalDateTime.now();
    }
}
