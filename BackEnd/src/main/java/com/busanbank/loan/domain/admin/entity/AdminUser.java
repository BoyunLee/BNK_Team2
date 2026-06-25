package com.busanbank.loan.domain.admin.entity;

import com.busanbank.loan.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 관리자 계정 — 고객(Customer)과 분리. 상품 결재의 담당자/책임자. */
@Entity
@Table(name = "ADMIN_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password; // BCrypt 해시

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private AdminRole role;

    @Column(name = "department", length = 50)
    private String department;

    @Builder
    public AdminUser(String loginId, String password, String name, AdminRole role, String department) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role;
        this.department = department;
    }

    public boolean isApprover() {
        return this.role == AdminRole.APPROVER;
    }
}
