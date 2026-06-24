package com.busanbank.loan.domain.loan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * signStep: PRE_PROCESS, LIMIT_INQUIRY, CONTRACT, FINAL_AUTH
 * signType: COMMON_CERT, SIMPLE_CERT
 */
@Entity
@Table(name = "SIGNATURE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "signature_id")
    private Long signatureId;

    @Column(name = "loan_account_no", nullable = false)
    private String loanAccountNo;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "sign_step")
    private String signStep;

    @Column(name = "sign_type")
    private String signType;

    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "original_value", columnDefinition = "TEXT")
    private String originalValue;

    @Column(name = "result")
    private String result;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Signature(String loanAccountNo, Long customerId, String signStep, String signType,
                     String tokenId, String originalValue, String result, LocalDateTime signedAt) {
        this.loanAccountNo = loanAccountNo;
        this.customerId = customerId;
        this.signStep = signStep;
        this.signType = signType;
        this.tokenId = tokenId;
        this.originalValue = originalValue;
        this.result = result;
        this.signedAt = signedAt;
        this.createdAt = LocalDateTime.now();
    }

    public void complete() {
        this.result = "SUCCESS";
        this.signedAt = LocalDateTime.now();
    }
}
