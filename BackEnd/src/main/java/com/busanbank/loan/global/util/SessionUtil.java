package com.busanbank.loan.global.util;

import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class SessionUtil {

    public static final String SESSION_CUSTOMER_ID = "SESSION_CUSTOMER_ID";

    private SessionUtil() {}

    public static Long getCurrentCustomerId() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) throw new BusinessException(ErrorCode.UNAUTHORIZED);

        HttpSession session = attrs.getRequest().getSession(false);
        if (session == null) throw new BusinessException(ErrorCode.SESSION_EXPIRED);

        Long customerId = (Long) session.getAttribute(SESSION_CUSTOMER_ID);
        if (customerId == null) throw new BusinessException(ErrorCode.UNAUTHORIZED);

        return customerId;
    }

    public static void setCurrentCustomerId(HttpServletRequest request, Long customerId) {
        request.getSession(true).setAttribute(SESSION_CUSTOMER_ID, customerId);
    }

    public static void invalidate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
    }
}
