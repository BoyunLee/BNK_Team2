package com.busanbank.loan.domain.loan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * rateTypeCode: 'F' fixed, 'V' variable
 */
@Entity
@Table(name = "LOAN_CONTRACT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "loan_account_no", nullable = false, length = 30)
    private String loanAccountNo;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "loan_amount")
    private Long loanAmount;

    @Column(name = "rate_type_code", length = 1)
    private String rateTypeCode;

    @Column(name = "final_rate", precision = 5, scale = 2)
    private BigDecimal finalRate;

    @Column(name = "repayment_type")
    private String repaymentType;

    @Column(name = "rate_change_cycle")
    private String rateChangeCycle;

    @Column(name = "loan_period")
    private String loanPeriod;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "deposit_account_no")
    private String depositAccountNo;

    /** 실제 대출계좌번호 (ACCOUNT.account_no, LOAN 타입) — 대출 실행 시 채번 */
    @Column(name = "loan_deposit_account_no")
    private String loanDepositAccountNo;

    @Column(name = "fund_purpose")
    private String fundPurpose;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "execution_date")
    private LocalDateTime executionDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public LoanContract(String loanAccountNo, Long customerId, Long loanAmount, String rateTypeCode,
                        BigDecimal finalRate, String repaymentType, String rateChangeCycle,
                        String loanPeriod, LocalDate maturityDate, String depositAccountNo,
                        String fundPurpose) {
        this.loanAccountNo = loanAccountNo;
        this.customerId = customerId;
        this.loanAmount = loanAmount;
        this.rateTypeCode = rateTypeCode;
        this.finalRate = finalRate;
        this.repaymentType = repaymentType;
        this.rateChangeCycle = rateChangeCycle;
        this.loanPeriod = loanPeriod;
        this.maturityDate = maturityDate;
        this.depositAccountNo = depositAccountNo;
        this.fundPurpose = fundPurpose;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public void execute(LocalDateTime executionDate, String loanDepositAccountNo) {
        this.status = "CONTRACTED";
        this.executionDate = executionDate;
        this.loanDepositAccountNo = loanDepositAccountNo;
    }
}
