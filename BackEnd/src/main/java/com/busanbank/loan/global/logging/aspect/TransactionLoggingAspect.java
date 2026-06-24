package com.busanbank.loan.global.logging.aspect;

import com.busanbank.loan.global.error.exception.BusinessException;
import com.busanbank.loan.global.util.MaskingUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * 도메인 서비스 레이어의 모든 메서드에 거래 로그를 기록한다.
 *
 * 실제 은행 시스템의 거래 추적 요건을 반영:
 * - 진입(ENTER): traceId, customerId, 클래스/메서드명, 파라미터(민감정보 마스킹)
 * - 정상 종료(EXIT): 소요시간
 * - 비즈니스 예외(BIZ_FAIL): 에러 코드, 메시지, 소요시간
 * - 시스템 예외(SYS_FAIL): 스택트레이스 포함, 소요시간
 */
@Aspect
@Component
public class TransactionLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(TransactionLoggingAspect.class);
    private final ObjectMapper objectMapper;

    public TransactionLoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("within(com.busanbank.loan.domain..*) && @within(org.springframework.stereotype.Service)")
    public void domainService() {}

    @Around("domainService()")
    public Object logTransaction(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = sig.getName();
        String action = className + "." + methodName;
        String traceId = MDC.get("traceId");
        String customerId = MDC.get("customerId");

        String params = serializeArgs(pjp.getArgs());

        log.info("[TX_ENTER] traceId={} customerId={} action={} params={}",
                traceId, customerId, action, params);

        long startMs = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            long duration = System.currentTimeMillis() - startMs;

            log.info("[TX_EXIT] traceId={} customerId={} action={} duration={}ms result=SUCCESS",
                    traceId, customerId, action, duration);

            return result;

        } catch (BusinessException ex) {
            long duration = System.currentTimeMillis() - startMs;
            log.warn("[TX_BIZ_FAIL] traceId={} customerId={} action={} duration={}ms errorCode={} message={}",
                    traceId, customerId, action, duration,
                    ex.getErrorCode().getCode(), ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startMs;
            log.error("[TX_SYS_FAIL] traceId={} customerId={} action={} duration={}ms error={}",
                    traceId, customerId, action, duration, ex.getMessage(), ex);
            throw ex;
        }
    }

    private String serializeArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        try {
            String json = objectMapper.writeValueAsString(args);
            return MaskingUtil.maskSensitiveJson(json);
        } catch (JsonProcessingException e) {
            return "[직렬화 불가]";
        }
    }
}
