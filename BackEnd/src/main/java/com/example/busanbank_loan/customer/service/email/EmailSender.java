package com.example.busanbank_loan.customer.service.email;

/**
 * 이메일 발송 추상화. 개발 단계에서는 Mock(콘솔) 구현을 사용하고, 추후 실제 SMTP 구현으로 교체한다.
 */
public interface EmailSender {

    void sendVerificationCode(String email, String code);
}
