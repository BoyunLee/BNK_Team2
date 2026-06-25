package com.busanbank.loan.global.interceptor;

import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import com.busanbank.loan.global.util.AdminSessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/** /api/v1/admin/** 보호 — 관리자 세션(SESSION_ADMIN_ID) 필요. 고객 인증과 분리. */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(ErrorCode.SESSION_EXPIRED);
        }

        Long adminId = (Long) session.getAttribute(AdminSessionUtil.SESSION_ADMIN_ID);
        if (adminId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        MDC.put("adminId", String.valueOf(adminId));
        return true;
    }
}
