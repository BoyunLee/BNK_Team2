package com.busanbank.loan.domain.loan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "LOAN_APPLICATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanApplication {

    @Id
    @Column(name = "loan_account_no")
    private String loanAccountNo;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "status_code", length = 1)
    private String statusCode = "1";

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public LoanApplication(String loanAccountNo, Long customerId, Long productId,
                           String statusCode, LocalDateTime expireAt) {
        this.loanAccountNo = loanAccountNo;
        this.customerId = customerId;
        this.productId = productId;
        this.statusCode = statusCode != null ? statusCode : "1";
        this.expireAt = expireAt;
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static String nextAccountNo(String date, long seq) {
        return "BNK" + date + String.format("%09d", seq);
    }

    public void updateStatus(String code) {
        this.statusCode = code;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expireAt.isBefore(LocalDateTime.now());
    }

    public boolean isInProgress() {
        return !List.of("9", "X", "R").contains(this.statusCode);
    }
}
