package com.busanbank.loan.domain.loan.service;

import com.busanbank.loan.domain.customer.entity.Customer;
import com.busanbank.loan.domain.customer.repository.CustomerRepository;
import com.busanbank.loan.domain.loan.entity.CustomerVerification;
import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.entity.Signature;
import com.busanbank.loan.domain.loan.repository.CustomerVerificationRepository;
import com.busanbank.loan.domain.loan.repository.SignatureRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final CustomerVerificationRepository customerVerificationRepository;
    private final LoanApplicationService loanApplicationService;
    private final CustomerRepository customerRepository;
    private final SignatureRepository signatureRepository;
    private final PasswordEncoder passwordEncoder;

    public record SignatureData(String signStep, String signType, String tokenId, String originalValue) {}

    @Transactional
    public void verifySuitability(String loanAccountNo, Long customerId, String simplePassword) {
        LoanApplication application = loanApplicationService.findAndValidate(loanAccountNo, "1");

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        if (!passwordEncoder.matches(simplePassword, customer.getSimplePassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        customerVerificationRepository.save(CustomerVerification.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .verifyStep("SUITABILITY")
                .verifyMethod("SIMPLE_PWD")
                .result("SUCCESS")
                .verifiedAt(LocalDateTime.now())
                .build());

        application.updateStatus("2");
    }

    @Transactional
    public void verifyContractSignature(String loanAccountNo, Long customerId, SignatureData signatureData) {
        LoanApplication application = loanApplicationService.findAndValidate(loanAccountNo, "7");

        signatureRepository.save(Signature.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .signStep("CONTRACT")
                .signType(signatureData.signType())
                .tokenId(signatureData.tokenId())
                .originalValue(signatureData.originalValue())
                .result("SUCCESS")
                .signedAt(LocalDateTime.now())
                .build());

        customerVerificationRepository.save(CustomerVerification.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .verifyStep("CONTRACT_SIGN")
                .verifyMethod("SIGNATURE")
                .result("SUCCESS")
                .verifiedAt(LocalDateTime.now())
                .build());

        application.updateStatus("8");
    }
}
