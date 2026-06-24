package com.example.busanbank_loan.account.entity;

import com.example.busanbank_loan.common.entity.BaseTimeEntity;
import com.example.busanbank_loan.customer.entity.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, unique = true)
    private String accountNo;

    /** 계좌 비밀번호 (단방향 해시 저장) */
    @Column(nullable = false)
    private String accountPassword;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    private Account(Customer customer, String accountNo, String encodedAccountPassword) {
        this.customer = customer;
        this.accountNo = accountNo;
        this.accountPassword = encodedAccountPassword;
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
    }

    public static Account create(Customer customer, String accountNo, String encodedAccountPassword) {
        return new Account(customer, accountNo, encodedAccountPassword);
    }
}
