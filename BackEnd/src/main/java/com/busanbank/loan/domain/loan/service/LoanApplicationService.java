package com.busanbank.loan.domain.loan.service;

import com.busanbank.loan.domain.loan.dto.request.MydataConsentRequest;
import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.entity.MydataConsent;
import com.busanbank.loan.domain.loan.repository.LoanApplicationRepository;
import com.busanbank.loan.domain.loan.repository.MydataConsentRepository;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanProductRepository loanProductRepository;
    private final MydataConsentRepository mydataConsentRepository;

    private final AtomicLong loanSeq = new AtomicLong(0);

    @Transactional
    public LoanApplication createApplication(Long customerId, Long productId) {
        loanProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // 진행 중 신청서는 상품별로 1건만 — 다른 상품은 동시 진행 가능
        boolean inProgress = loanApplicationRepository
                .existsByCustomerIdAndProductIdAndStatusCodeNotIn(customerId, productId, List.of("9", "X", "R"));
        if (inProgress) {
            throw new BusinessException(ErrorCode.LOAN_ALREADY_IN_PROGRESS);
        }

        // 채번: 인메모리 시퀀스가 재기동 시 0으로 리셋되므로, 이미 존재하면 다음 번호로 건너뛴다.
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String loanAccountNo;
        do {
            loanAccountNo = LoanApplication.nextAccountNo(date, loanSeq.incrementAndGet());
        } while (loanApplicationRepository.existsById(loanAccountNo));

        LoanApplication application = LoanApplication.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .productId(productId)
                .statusCode("1")
                .expireAt(LocalDateTime.now().plusDays(1))
                .build();

        return loanApplicationRepository.save(application);
    }

    /** 특정 상품의 진행 중(미완료·미만료) 신청서 최신 1건. 없으면 null. */
    @Transactional(readOnly = true)
    public LoanApplication getCurrentApplication(Long customerId, Long productId) {
        return loanApplicationRepository.findAllByCustomerIdOrderByAppliedAtDesc(customerId).stream()
                .filter(a -> a.getProductId().equals(productId))
                .filter(a -> !List.of("9", "X", "R").contains(a.getStatusCode()))
                .filter(a -> !a.isExpired())
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public LoanApplication findAndValidate(String loanAccountNo, String expectedStatus) {
        LoanApplication application = loanApplicationRepository.findByLoanAccountNo(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        if (application.isExpired()) {
            throw new BusinessException(ErrorCode.LOAN_EXPIRED);
        }

        if (!application.getStatusCode().equals(expectedStatus)) {
            throw new BusinessException(ErrorCode.INVALID_STEP);
        }

        return application;
    }

    @Transactional(readOnly = true)
    public LoanApplication findAndValidateAtLeast(String loanAccountNo, String minStatus) {
        LoanApplication application = loanApplicationRepository.findByLoanAccountNo(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        if (application.isExpired()) {
            throw new BusinessException(ErrorCode.LOAN_EXPIRED);
        }

        if (application.getStatusCode().compareTo(minStatus) < 0) {
            throw new BusinessException(ErrorCode.INVALID_STEP);
        }

        return application;
    }

    @Transactional(readOnly = true)
    public LoanApplication findApplication(String loanAccountNo) {
        LoanApplication application = loanApplicationRepository.findByLoanAccountNo(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        if (application.isExpired()) {
            throw new BusinessException(ErrorCode.LOAN_EXPIRED);
        }

        return application;
    }

    @Transactional
    public void cancelApplication(String loanAccountNo, Long customerId) {
        LoanApplication application = loanApplicationRepository.findByLoanAccountNo(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        if (!application.getCustomerId().equals(customerId)) {
            throw new BusinessException(ErrorCode.LOAN_NOT_FOUND);
        }

        if (List.of("9", "X", "R").contains(application.getStatusCode())) {
            throw new BusinessException(ErrorCode.INVALID_STEP);
        }

        application.updateStatus("R");
    }

    @Transactional
    public void saveMydataConsent(String loanAccountNo, List<MydataConsentRequest.ConsentItem> consents) {
        LoanApplication application = findAndValidate(loanAccountNo, "2");

        List<MydataConsent> consentEntities = consents.stream()
                .map(item -> MydataConsent.builder()
                        .loanAccountNo(loanAccountNo)
                        .consentType(item.consentType())
                        .dataProvider(item.dataProvider())
                        .consentYn("Y")
                        .consentAt(LocalDateTime.now())
                        .build())
                .toList();

        mydataConsentRepository.saveAll(consentEntities);
        application.updateStatus("3");
    }
}
