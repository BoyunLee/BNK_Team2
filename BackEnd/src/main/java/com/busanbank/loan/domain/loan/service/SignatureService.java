package com.busanbank.loan.domain.loan.service;

import com.busanbank.loan.domain.loan.dto.response.TokenIssueResponse;
import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.entity.Signature;
import com.busanbank.loan.domain.loan.repository.SignatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureRepository signatureRepository;
    private final LoanApplicationService loanApplicationService;
    private final SignaturePayloadAssembler signaturePayloadAssembler;

    @Transactional
    public Long sign(String loanAccountNo, Long customerId, String signStep, String signType,
                     String tokenId, String originalValue) {
        LoanApplication application = loanApplicationService.findApplication(loanAccountNo);

        String signedData = signaturePayloadAssembler.assemble(loanAccountNo, customerId, signStep, signType);

        Signature signature = Signature.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .signStep(signStep)
                .signType(signType)
                .tokenId(tokenId)
                .originalValue(originalValue)
                .signedData(signedData)
                .result("SUCCESS")
                .signedAt(LocalDateTime.now())
                .build();

        Signature saved = signatureRepository.save(signature);

        if ("PRE_PROCESS".equals(signStep)) {
            application.updateStatus("4");
        }

        return saved.getSignatureId();
    }

    @Transactional
    public TokenIssueResponse issueToken(String loanAccountNo, Long customerId, String signType) {
        loanApplicationService.findAndValidate(loanAccountNo, "4");

        String tokenId = UUID.randomUUID().toString();

        String signedData = signaturePayloadAssembler.assemble(loanAccountNo, customerId, "LIMIT_INQUIRY", signType);

        Signature signature = Signature.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .signStep("LIMIT_INQUIRY")
                .signType(signType)
                .tokenId(tokenId)
                .signedData(signedData)
                .result(null)
                .signedAt(null)
                .build();

        signatureRepository.save(signature);

        return new TokenIssueResponse(tokenId, LocalDateTime.now().plusMinutes(30));
    }
}
