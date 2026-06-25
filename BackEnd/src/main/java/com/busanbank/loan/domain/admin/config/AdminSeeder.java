package com.busanbank.loan.domain.admin.config;

import com.busanbank.loan.domain.admin.entity.AdminRole;
import com.busanbank.loan.domain.admin.entity.AdminUser;
import com.busanbank.loan.domain.admin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 관리자 시드 — ADMIN_USER 가 비어있을 때만 기본 계정 4개를 생성한다.
 * 기본 비밀번호: admin1234 (운영 전 반드시 변경).
 */
@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private static final String DEFAULT_PW = "admin1234";

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (adminUserRepository.count() > 0) return;

        String pw = passwordEncoder.encode(DEFAULT_PW);
        adminUserRepository.saveAll(List.of(
                AdminUser.builder().loginId("drafter1").password(pw).name("김담당").role(AdminRole.DRAFTER).department("여신상품부").build(),
                AdminUser.builder().loginId("drafter2").password(pw).name("최주임").role(AdminRole.DRAFTER).department("여신상품부").build(),
                AdminUser.builder().loginId("approver1").password(pw).name("박책임").role(AdminRole.APPROVER).department("여신상품부").build(),
                AdminUser.builder().loginId("approver2").password(pw).name("이부장").role(AdminRole.APPROVER).department("여신기획팀").build()
        ));
        log.info("[ADMIN_SEED] 기본 관리자 4명 생성 (기본 비밀번호: {})", DEFAULT_PW);
    }
}
