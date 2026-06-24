package com.busanbank.loan.global.interceptor;

import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import com.busanbank.loan.global.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(ErrorCode.SESSION_EXPIRED);
        }

        Long customerId = (Long) session.getAttribute(SessionUtil.SESSION_CUSTOMER_ID);
        if (customerId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // MDC에 customerId 보강 (MdcLoggingFilter 이후 로그인 완료 시점 반영)
        MDC.put("customerId", String.valueOf(customerId));
        return true;
    }
}
