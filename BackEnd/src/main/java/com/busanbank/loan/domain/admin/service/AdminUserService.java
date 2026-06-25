package com.busanbank.loan.domain.admin.service;

import com.busanbank.loan.domain.admin.entity.AdminRole;
import com.busanbank.loan.domain.admin.entity.AdminUser;
import com.busanbank.loan.domain.admin.repository.AdminUserRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AdminUser login(String loginId, String rawPassword) {
        AdminUser admin = adminUserRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        return admin;
    }

    @Transactional(readOnly = true)
    public AdminUser getById(Long adminId) {
        return adminUserRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<AdminUser> listApprovers() {
        return adminUserRepository.findAllByRole(AdminRole.APPROVER);
    }
}
