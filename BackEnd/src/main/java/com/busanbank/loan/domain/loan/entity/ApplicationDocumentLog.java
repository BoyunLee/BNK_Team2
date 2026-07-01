package com.busanbank.loan.domain.loan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * documentType values: ADMIN_INFO_REQUEST, PERSONAL_INFO_CONSENT, MOBILE_AUTH_TERMS, PRODUCT_TERMS, PRODUCT_DESCRIPTION, BOND_CONTRACT
 */
@Entity
@Table(name = "APPLICATION_DOCUMENT_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationDocumentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "loan_account_no", nullable = false, length = 30)
    private String loanAccountNo;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "terms_id")
    private Long termsId;

    @Column(name = "terms_seq")
    private int termsSeq;

    @Column(name = "viewed_yn", length = 1)
    private String viewedYn = "N";

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "agreed_yn", length = 1)
    private String agreedYn = "N";

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ApplicationDocumentLog(String loanAccountNo, String documentType, Long termsId, int termsSeq) {
        this.loanAccountNo = loanAccountNo;
        this.documentType = documentType;
        this.termsId = termsId;
        this.termsSeq = termsSeq;
        this.viewedYn = "N";
        this.agreedYn = "N";
        this.createdAt = LocalDateTime.now();
    }

    public void markViewed() {
        this.viewedYn = "Y";
        this.viewedAt = LocalDateTime.now();
    }

    public void markAgreed() {
        this.agreedYn = "Y";
        this.agreedAt = LocalDateTime.now();
    }

    public void updateTerms(Long termsId, int termsSeq) {
        this.termsId = termsId;
        this.termsSeq = termsSeq;
    }
}
