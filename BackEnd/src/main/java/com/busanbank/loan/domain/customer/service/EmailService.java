package com.busanbank.loan.domain.customer.service;

import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final CacheManager cacheManager;

    public void sendVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(1_000_000));

        Cache emailCodeCache = cacheManager.getCache("emailCode");
        emailCodeCache.put(email, code);

        // 콘솔에서 코드를 바로 확인할 수 있도록 출력
        log.warn("========================================");
        log.warn("  이메일 인증 코드: {} → {}", email, code);
        log.warn("========================================");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[BNK3] 이메일 인증 코드");
            message.setText("인증 코드: " + code + "\n\n5분 내에 입력해 주세요.");
            mailSender.send(message);
            log.info("인증 코드 발송 완료: {}", email);
        } catch (Exception e) {
            // 메일 발송 실패해도 캐시에 저장된 코드로 인증 가능 (로컬 개발용)
            log.warn("메일 발송 실패 (위 로그의 코드로 직접 입력 가능): {}", e.getMessage());
        }
    }

    public void verifyCode(String email, String code) {
        Cache emailCodeCache = cacheManager.getCache("emailCode");
        String cached = emailCodeCache.get(email, String.class);

        if (cached == null || !cached.equals(code)) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_CODE);
        }

        emailCodeCache.evict(email);

        Cache emailVerifiedCache = cacheManager.getCache("emailVerified");
        emailVerifiedCache.put(email, "true");
    }

    public boolean isEmailVerified(String email) {
        Cache emailVerifiedCache = cacheManager.getCache("emailVerified");
        String verified = emailVerifiedCache.get(email, String.class);
        return "true".equals(verified);
    }
}
