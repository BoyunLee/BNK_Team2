package com.busanbank.loan.domain.admin.dto.response;

import com.busanbank.loan.domain.admin.entity.AdminUser;

/** 프론트 lib/admin.ts AdminUser 와 동일 형태. */
public record AdminUserResponse(
        Long id,
        String loginId,
        String name,
        String role,
        String department
) {
    public static AdminUserResponse from(AdminUser a) {
        return new AdminUserResponse(a.getId(), a.getLoginId(), a.getName(), a.getRole().name(), a.getDepartment());
    }
}
