package com.busanbank.loan.domain.loan.service;

import com.busanbank.loan.domain.customer.entity.Account;
import com.busanbank.loan.domain.customer.entity.Customer;
import com.busanbank.loan.domain.customer.repository.AccountRepository;
import com.busanbank.loan.domain.customer.repository.CustomerRepository;
import com.busanbank.loan.domain.loan.dto.request.ContractConditionsRequest;
import com.busanbank.loan.domain.loan.dto.response.ConditionsResponse;
import com.busanbank.loan.domain.loan.dto.response.ConfirmationResponse;
import com.busanbank.loan.domain.loan.dto.response.ExecuteResponse;
import com.busanbank.loan.domain.loan.entity.*;
import com.busanbank.loan.domain.loan.repository.*;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.entity.ProductPreferentialRate;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.domain.product.repository.ProductPreferentialRateRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final LoanContractRepository loanContractRepository;
    private final LoanPreferentialAppliedRepository loanPreferentialAppliedRepository;
    private final ProductPreferentialRateRepository productPreferentialRateRepository;
    private final LoanScreeningRepository loanScreeningRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CustomerVerificationRepository customerVerificationRepository;
    private final ApplicationDocumentLogRepository applicationDocumentLogRepository;
    private final LoanProductRepository loanProductRepository;
    private final LoanApplicationService loanApplicationService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void agreeTerms(String loanAccountNo, Long productId, List<String> documentTypes) {
        loanApplicationService.findAndValidate(loanAccountNo, "6");

        for (String documentType : documentTypes) {
            ApplicationDocumentLog log = applicationDocumentLogRepository
                    .findByLoanAccountNoAndDocumentType(loanAccountNo, documentType)
                    .orElseGet(() -> ApplicationDocumentLog.builder()
                            .loanAccountNo(loanAccountNo)
                            .documentType(documentType)
                            .termsId(0L)
                            .termsSeq(0)
                            .build());

            if (!"Y".equals(log.getViewedYn())) {
                log.markViewed();
            }
            if (!"Y".equals(log.getAgreedYn())) {
                log.markAgreed();
            }

            applicationDocumentLogRepository.save(log);
        }
    }

    @Transactional
    public ConditionsResponse saveConditions(String loanAccountNo, Long customerId, ContractConditionsRequest data) {
        LoanApplication application = loanApplicationService.findAndValidate(loanAccountNo, "6");

        LoanScreening screening = loanScreeningRepository.findTopByLoanAccountNoOrderByScreeningIdDesc(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCREENING_NOT_FOUND));

        if (!"APPROVED".equals(screening.getResult())) {
            throw new BusinessException(ErrorCode.SCREENING_REJECTED);
        }

        if (data.loanAmount() > screening.getMaxLimitAmt()) {
            throw new BusinessException(ErrorCode.LOAN_AMOUNT_EXCEEDED);
        }

        loanPreferentialAppliedRepository.deleteAllByLoanAccountNo(loanAccountNo);

        BigDecimal totalPreferentialRate = BigDecimal.ZERO;

        if (data.preferentialIds() != null && !data.preferentialIds().isEmpty()) {
            List<ProductPreferentialRate> preferentialRates = productPreferentialRateRepository
                    .findAllByPreferentialIdIn(data.preferentialIds());

            List<LoanPreferentialApplied> applied = preferentialRates.stream()
                    .map(rate -> LoanPreferentialApplied.builder()
                            .loanAccountNo(loanAccountNo)
                            .preferentialId(rate.getPreferentialId())
                            .appliedRateValue(rate.getRateValue())
                            .build())
                    .toList();

            loanPreferentialAppliedRepository.saveAll(applied);

            totalPreferentialRate = preferentialRates.stream()
                    .map(ProductPreferentialRate::getRateValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        BigDecimal minRate = new BigDecimal("0.1");
        BigDecimal finalRate = screening.getAppliedBaseRate().subtract(totalPreferentialRate);
        if (finalRate.compareTo(minRate) < 0) {
            finalRate = minRate;
        }

        LocalDate maturityDate = parseLoanPeriodToMaturityDate(data.loanPeriod());

        LoanContract contract = LoanContract.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .loanAmount(data.loanAmount())
                .rateTypeCode(data.rateTypeCode())
                .finalRate(finalRate)
                .repaymentType(data.repaymentType())
                .rateChangeCycle(data.rateChangeCycle())
                .loanPeriod(data.loanPeriod())
                .maturityDate(maturityDate)
                .depositAccountNo(data.depositAccountNo())
                .fundPurpose(data.fundPurpose())
                .build();

        loanContractRepository.save(contract);
        application.updateStatus("7");

        return new ConditionsResponse(
                data.loanAmount(),
                screening.getAppliedBaseRate(),
                totalPreferentialRate,
                finalRate,
                data.repaymentType(),
                maturityDate
        );
    }

    @Transactional(readOnly = true)
    public ConfirmationResponse getConfirmation(String loanAccountNo) {
        LoanApplication application = loanApplicationService.findApplication(loanAccountNo);

        LoanContract contract = loanContractRepository.findByLoanAccountNo(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Customer customer = customerRepository.findById(application.getCustomerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        LoanProduct product = loanProductRepository.findById(application.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        List<LoanPreferentialApplied> appliedList = loanPreferentialAppliedRepository
                .findAllByLoanAccountNo(loanAccountNo);

        List<ConfirmationResponse.PreferentialRateInfo> preferentialRateInfos = appliedList.stream()
                .map(applied -> productPreferentialRateRepository.findById(applied.getPreferentialId()).orElse(null))
                .filter(Objects::nonNull)
                .map(rate -> new ConfirmationResponse.PreferentialRateInfo(rate.getConditionName(), rate.getRateValue()))
                .toList();

        return new ConfirmationResponse(
                product.getProductName(),
                customer.getName(),
                contract.getLoanAmount(),
                contract.getFinalRate(),
                contract.getRepaymentType(),
                contract.getMaturityDate(),
                contract.getDepositAccountNo(),
                contract.getFundPurpose(),
                preferentialRateInfos
        );
    }

    @Transactional
    public ExecuteResponse executeLoan(String loanAccountNo, Long customerId, String simplePassword) {
        LoanApplication application = loanApplicationService.findAndValidate(loanAccountNo, "8");

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        if (!passwordEncoder.matches(simplePassword, customer.getSimplePassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        LoanContract contract = loanContractRepository.findByLoanAccountNo(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        customerVerificationRepository.save(CustomerVerification.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .verifyStep("FINAL_AUTH")
                .verifyMethod("SIMPLE_PWD")
                .result("SUCCESS")
                .verifiedAt(LocalDateTime.now())
                .build());

        LocalDateTime executionDate = LocalDateTime.now();
        contract.execute(executionDate);

        Account account = accountRepository.findByAccountNo(contract.getDepositAccountNo())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.deposit(BigDecimal.valueOf(contract.getLoanAmount()));
        application.updateStatus("9");

        return new ExecuteResponse(
                loanAccountNo,
                contract.getLoanAmount(),
                contract.getFinalRate(),
                contract.getMaturityDate(),
                contract.getDepositAccountNo(),
                executionDate
        );
    }

    private LocalDate parseLoanPeriodToMaturityDate(String loanPeriod) {
        if (loanPeriod != null && loanPeriod.endsWith("개월")) {
            try {
                int months = Integer.parseInt(loanPeriod.replace("개월", "").trim());
                return LocalDate.now().plusMonths(months);
            } catch (NumberFormatException ignored) {
            }
        }
        return LocalDate.now().plusYears(1);
    }
}
