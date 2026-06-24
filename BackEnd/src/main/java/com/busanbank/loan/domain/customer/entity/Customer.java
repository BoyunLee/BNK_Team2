package com.busanbank.loan.domain.customer.entity;

import com.busanbank.loan.global.crypto.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOMER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    // ── 개인정보: DB 저장 시 AES-256 암호화, 조회 시 자동 복호화 ──

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "phone_no", nullable = false, length = 100)
    private String phoneNo;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "address", length = 500)
    private String address;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "email", nullable = false, length = 300)
    private String email;

    // ── 비밀번호: BCrypt 단방향 해시 (복호화 불가, 검증만 가능) ──

    @Column(name = "email_verified_yn", length = 1)
    private String emailVerifiedYn = "N";

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @Column(name = "simple_password", nullable = false, length = 200)
    private String simplePassword;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Customer(String name, String phoneNo, LocalDate birthDate,
                    String address, String email,
                    String password, String simplePassword) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.birthDate = birthDate;
        this.address = address;
        this.email = email;
        this.password = password;
        this.simplePassword = simplePassword;
        this.emailVerifiedYn = "N";
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void verifyEmail() {
        this.emailVerifiedYn = "Y";
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}
