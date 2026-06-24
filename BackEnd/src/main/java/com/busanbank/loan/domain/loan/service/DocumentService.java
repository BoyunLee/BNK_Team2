package com.busanbank.loan.domain.loan.service;

import com.busanbank.loan.domain.loan.entity.ApplicationDocumentLog;
import com.busanbank.loan.domain.loan.repository.ApplicationDocumentLogRepository;
import com.busanbank.loan.domain.product.dto.response.TermsResponse;
import com.busanbank.loan.domain.product.service.ProductService;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final ApplicationDocumentLogRepository applicationDocumentLogRepository;
    private final ProductService productService;
    private final LoanApplicationService loanApplicationService;

    @Transactional
    public ApplicationDocumentLog recordView(String loanAccountNo, Long productId, String documentType) {
        loanApplicationService.findApplication(loanAccountNo);

        TermsResponse terms = productService.getLatestTerms(productId, documentType);

        ApplicationDocumentLog log = applicationDocumentLogRepository
                .findByLoanAccountNoAndDocumentType(loanAccountNo, documentType)
                .orElseGet(() -> ApplicationDocumentLog.builder()
                        .loanAccountNo(loanAccountNo)
                        .documentType(documentType)
                        .termsId(terms.termsId())
                        .termsSeq(terms.termsSeq())
                        .build());

        log.updateTerms(terms.termsId(), terms.termsSeq());
        log.markViewed();

        return applicationDocumentLogRepository.save(log);
    }

    @Transactional
    public ApplicationDocumentLog recordAgree(String loanAccountNo, String documentType) {
        loanApplicationService.findApplication(loanAccountNo);

        ApplicationDocumentLog log = applicationDocumentLogRepository
                .findByLoanAccountNoAndDocumentType(loanAccountNo, documentType)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!"Y".equals(log.getViewedYn())) {
            throw new BusinessException(ErrorCode.DOCUMENT_NOT_VIEWED);
        }

        log.markAgreed();
        return log;
    }
}
