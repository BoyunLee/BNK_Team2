package com.busanbank.loan.global.util;

import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** 관리자 세션 — 고객 세션(SESSION_CUSTOMER_ID)과 별도 속성으로 분리 관리. */
public final class AdminSessionUtil {

    public static final String SESSION_ADMIN_ID = "SESSION_ADMIN_ID";

    private AdminSessionUtil() {}

    public static Long getCurrentAdminId() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) throw new BusinessException(ErrorCode.UNAUTHORIZED);

        HttpSession session = attrs.getRequest().getSession(false);
        if (session == null) throw new BusinessException(ErrorCode.SESSION_EXPIRED);

        Long adminId = (Long) session.getAttribute(SESSION_ADMIN_ID);
        if (adminId == null) throw new BusinessException(ErrorCode.UNAUTHORIZED);

        return adminId;
    }

    public static void setCurrentAdminId(HttpServletRequest request, Long adminId) {
        request.getSession(true).setAttribute(SESSION_ADMIN_ID, adminId);
    }

    /** 관리자 로그아웃 — 고객 세션을 죽이지 않도록 속성만 제거. */
    public static void clear(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.removeAttribute(SESSION_ADMIN_ID);
    }
}
