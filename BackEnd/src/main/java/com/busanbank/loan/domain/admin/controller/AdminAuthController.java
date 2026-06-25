package com.busanbank.loan.domain.admin.controller;

import com.busanbank.loan.domain.admin.dto.request.AdminLoginRequest;
import com.busanbank.loan.domain.admin.dto.response.AdminUserResponse;
import com.busanbank.loan.domain.admin.entity.AdminUser;
import com.busanbank.loan.domain.admin.service.AdminUserService;
import com.busanbank.loan.global.response.ApiResponse;
import com.busanbank.loan.global.util.AdminSessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 관리자 인증 — 고객 인증과 분리된 세션(SESSION_ADMIN_ID). */
@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminUserService adminUserService;

    @PostMapping("/login")
    public ApiResponse<AdminUserResponse> login(@Valid @RequestBody AdminLoginRequest request,
                                                HttpServletRequest httpRequest) {
        AdminUser admin = adminUserService.login(request.loginId(), request.password());
        AdminSessionUtil.setCurrentAdminId(httpRequest, admin.getId());
        return ApiResponse.ok(AdminUserResponse.from(admin), "로그인되었습니다.");
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest httpRequest) {
        AdminSessionUtil.clear(httpRequest);
        return ApiResponse.ok("로그아웃되었습니다.");
    }

    @GetMapping("/me")
    public ApiResponse<AdminUserResponse> me() {
        Long adminId = AdminSessionUtil.getCurrentAdminId();
        return ApiResponse.ok(AdminUserResponse.from(adminUserService.getById(adminId)));
    }
}
