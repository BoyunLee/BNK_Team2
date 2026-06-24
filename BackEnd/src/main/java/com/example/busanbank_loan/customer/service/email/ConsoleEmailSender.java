package com.example.busanbank_loan.customer.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mock 이메일 발송기. 실제 메일을 보내지 않고 서버 콘솔에 인증 코드를 출력한다.
 * (로컬 개발 편의 — 추후 SMTP 구현으로 대체)
 */
@Slf4j
@Component
public class ConsoleEmailSender implements EmailSender {

    @Override
    public void sendVerificationCode(String email, String code) {
        log.info("[EMAIL][MOCK] 수신자={} 인증코드={}", email, code);
    }
}
