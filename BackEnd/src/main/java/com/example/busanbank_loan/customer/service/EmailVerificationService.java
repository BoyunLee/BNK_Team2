package com.example.busanbank_loan.customer.service;

import com.example.busanbank_loan.common.exception.BusinessException;
import com.example.busanbank_loan.common.response.ResultCode;
import com.example.busanbank_loan.customer.entity.EmailVerification;
import com.example.busanbank_loan.customer.repository.CustomerRepository;
import com.example.busanbank_loan.customer.repository.EmailVerificationRepository;
import com.example.busanbank_loan.customer.service.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final int EXPIRE_MINUTES = 5;

    private final EmailVerificationRepository emailVerificationRepository;
    private final CustomerRepository customerRepository;
    private final EmailSender emailSender;
    private final SecureRandom random = new SecureRandom();

    /** 1.1 인증 코드 발송 */
    @Transactional
    public void sendCode(String email) {
        if (customerRepository.existsByEmail(email)) {
            throw new BusinessException(ResultCode.AUTH001);
        }
        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRE_MINUTES);

        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .map(existing -> {
                    existing.updateCode(code, expiresAt);
                    return existing;
                })
                .orElseGet(() -> EmailVerification.create(email, code, expiresAt));
        emailVerificationRepository.save(verification);

        emailSender.sendVerificationCode(email, code);
    }

    /** 1.2 인증 코드 확인 */
    @Transactional
    public void verifyCode(String email, String code) {
        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ResultCode.AUTH002));
        if (verification.isExpired() || !verification.matches(code)) {
            throw new BusinessException(ResultCode.AUTH002);
        }
        verification.verify();
    }

    /** 회원가입 전 이메일 인증 완료 여부 확인 */
    @Transactional(readOnly = true)
    public void assertVerified(String email) {
        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ResultCode.AUTH003));
        if (!verification.isVerified()) {
            throw new BusinessException(ResultCode.AUTH003);
        }
    }

    private String generateCode() {
        return String.format("%06d", random.nextInt(1_000_000));
    }
}
