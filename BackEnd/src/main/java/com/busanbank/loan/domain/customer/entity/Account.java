package com.busanbank.loan.domain.customer.entity;

import com.busanbank.loan.global.crypto.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCOUNT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "account_no", length = 100)
    private String accountNo;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "balance", precision = 15)
    private BigDecimal balance = BigDecimal.ZERO;

    // BCrypt 단방향 해시
    @Column(name = "account_password", nullable = false, length = 200)
    private String accountPassword;

    /** DEPOSIT 입출금 / LOAN 대출 */
    @Column(name = "account_type", length = 20)
    private String accountType = "DEPOSIT";

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Account(String accountNo, Long customerId, String accountPassword, String accountType) {
        this.accountNo = accountNo;
        this.customerId = customerId;
        this.accountPassword = accountPassword;
        this.accountType = accountType != null ? accountType : "DEPOSIT";
        this.balance = BigDecimal.ZERO;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}
