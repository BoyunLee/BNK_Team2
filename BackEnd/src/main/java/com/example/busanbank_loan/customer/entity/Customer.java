package com.example.busanbank_loan.customer.entity;

import com.example.busanbank_loan.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "customer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNo;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified;

    /** 간편인증 비밀번호 (단방향 해시 저장) */
    @Column(nullable = false)
    private String simplePassword;

    /** 전자서명 비밀번호 (단방향 해시 저장) */
    @Column(nullable = false)
    private String signaturePassword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    private Customer(String name, String phoneNo, LocalDate birthDate, String address,
                     String email, String encodedSimplePassword, String encodedSignaturePassword) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.birthDate = birthDate;
        this.address = address;
        this.email = email;
        this.emailVerified = true;
        this.simplePassword = encodedSimplePassword;
        this.signaturePassword = encodedSignaturePassword;
        this.role = Role.USER;
        this.status = CustomerStatus.ACTIVE;
    }

    /**
     * 회원가입 시점 생성. 비밀번호는 호출 측에서 이미 인코딩된 값을 전달한다.
     */
    public static Customer create(String name, String phoneNo, LocalDate birthDate, String address,
                                  String email, String encodedSimplePassword, String encodedSignaturePassword) {
        return new Customer(name, phoneNo, birthDate, address, email,
                encodedSimplePassword, encodedSignaturePassword);
    }

    public boolean isActive() {
        return this.status == CustomerStatus.ACTIVE;
    }
}
