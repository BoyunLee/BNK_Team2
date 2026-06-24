package com.busanbank.loan.global.logging.filter;

import com.busanbank.loan.global.util.SessionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 모든 HTTP 요청에 traceId를 부여하고 MDC에 등록한다.
 * 로그에 자동으로 traceId와 customerId가 포함되어 거래 추적이 가능해진다.
 * Security Filter보다 먼저 실행되도록 최우선 순위로 등록.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(MdcLoggingFilter.class);

    private static final String MDC_TRACE_ID = "traceId";
    private static final String MDC_CUSTOMER_ID = "customerId";
    private static final String MDC_REQUEST_IP = "requestIp";
    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long startMs = System.currentTimeMillis();
        String traceId = resolveTraceId(request);

        try {
            MDC.put(MDC_TRACE_ID, traceId);
            MDC.put(MDC_REQUEST_IP, getClientIp(request));
            setCustomerIdToMdc(request);

            response.setHeader(HEADER_TRACE_ID, traceId);
            chain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - startMs;
            log.info("[HTTP] method={} uri={} status={} duration={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
            MDC.clear();
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String headerTraceId = request.getHeader(HEADER_TRACE_ID);
        return (headerTraceId != null && !headerTraceId.isBlank())
                ? headerTraceId
                : UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private void setCustomerIdToMdc(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Long customerId = (Long) session.getAttribute(SessionUtil.SESSION_CUSTOMER_ID);
                if (customerId != null) {
                    MDC.put(MDC_CUSTOMER_ID, String.valueOf(customerId));
                }
            }
        } catch (Exception ignored) {
            // 세션 미존재 시 MDC에 customerId 미기록
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
