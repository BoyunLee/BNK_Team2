package com.busanbank.loan.domain.loan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * verifyStep values: SUITABILITY, MYDATA_SIGN, LIMIT_SIGN, CONTRACT_SIGN, FINAL_AUTH
 * verifyMethod values: SIMPLE_PWD, SIGNATURE
 */
@Entity
@Table(name = "CUSTOMER_VERIFICATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long verificationId;

    @Column(name = "loan_account_no", nullable = false, length = 30)
    private String loanAccountNo;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "verify_step")
    private String verifyStep;

    @Column(name = "verify_method")
    private String verifyMethod;

    @Column(name = "result")
    private String result;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public CustomerVerification(String loanAccountNo, Long customerId, String verifyStep,
                                String verifyMethod, String result, LocalDateTime verifiedAt) {
        this.loanAccountNo = loanAccountNo;
        this.customerId = customerId;
        this.verifyStep = verifyStep;
        this.verifyMethod = verifyMethod;
        this.result = result;
        this.verifiedAt = verifiedAt;
        this.createdAt = LocalDateTime.now();
    }
}
