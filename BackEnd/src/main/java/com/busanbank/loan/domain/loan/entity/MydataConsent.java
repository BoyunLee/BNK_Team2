package com.busanbank.loan.domain.loan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * consentType values: ADMIN_INFO, MYDATA_USE
 */
@Entity
@Table(name = "MYDATA_CONSENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MydataConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consent_id")
    private Long consentId;

    @Column(name = "loan_account_no", nullable = false, length = 30)
    private String loanAccountNo;

    @Column(name = "consent_type")
    private String consentType;

    @Column(name = "data_provider")
    private String dataProvider;

    @Column(name = "consent_yn", length = 1)
    private String consentYn = "Y";

    @Column(name = "consent_at")
    private LocalDateTime consentAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public MydataConsent(String loanAccountNo, String consentType, String dataProvider,
                         String consentYn, LocalDateTime consentAt) {
        this.loanAccountNo = loanAccountNo;
        this.consentType = consentType;
        this.dataProvider = dataProvider;
        this.consentYn = consentYn != null ? consentYn : "Y";
        this.consentAt = consentAt;
        this.createdAt = LocalDateTime.now();
    }
}
