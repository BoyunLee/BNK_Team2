package com.busanbank.loan.domain.customer.service;

import com.busanbank.loan.domain.customer.dto.response.LoanDetailResponse;
import com.busanbank.loan.domain.customer.dto.response.LoanSummaryResponse;
import com.busanbank.loan.domain.customer.dto.response.MyAccountResponse;
import com.busanbank.loan.domain.customer.entity.Account;
import com.busanbank.loan.domain.customer.entity.Customer;
import com.busanbank.loan.domain.customer.repository.AccountRepository;
import com.busanbank.loan.domain.customer.repository.CustomerRepository;
import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.entity.LoanContract;
import com.busanbank.loan.domain.loan.entity.LoanPreferentialApplied;
import com.busanbank.loan.domain.loan.repository.LoanApplicationRepository;
import com.busanbank.loan.domain.loan.repository.LoanContractRepository;
import com.busanbank.loan.domain.loan.repository.LoanPreferentialAppliedRepository;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.entity.ProductPreferentialRate;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.domain.product.repository.ProductPreferentialRateRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanContractRepository loanContractRepository;
    private final LoanProductRepository loanProductRepository;
    private final LoanPreferentialAppliedRepository loanPreferentialAppliedRepository;
    private final ProductPreferentialRateRepository productPreferentialRateRepository;

    public MyAccountResponse getMyAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));
        Account account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        return MyAccountResponse.of(account, customer);
    }

    public List<LoanSummaryResponse> getMyLoans(Long customerId) {
        List<LoanApplication> applications =
                loanApplicationRepository.findAllByCustomerIdOrderByAppliedAtDesc(customerId);

        return applications.stream()
                .map(app -> {
                    LoanProduct product = loanProductRepository.findById(app.getProductId()).orElse(null);
                    LoanContract contract = loanContractRepository.findByLoanAccountNo(app.getLoanAccountNo()).orElse(null);
                    return LoanSummaryResponse.of(app, product, contract);
                })
                .toList();
    }

    public LoanDetailResponse getLoanDetail(Long customerId, String loanAccountNo) {
        LoanApplication app = loanApplicationRepository.findByLoanAccountNo(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        if (!app.getCustomerId().equals(customerId)) {
            throw new BusinessException(ErrorCode.LOAN_NOT_FOUND);
        }

        LoanProduct product = loanProductRepository.findById(app.getProductId()).orElse(null);
        LoanContract contract = loanContractRepository.findByLoanAccountNo(loanAccountNo).orElse(null);

        List<LoanPreferentialApplied> appliedList = List.of();
        List<ProductPreferentialRate> rateList = List.of();

        if (contract != null) {
            appliedList = loanPreferentialAppliedRepository.findAllByLoanAccountNo(loanAccountNo);
            if (!appliedList.isEmpty()) {
                List<Long> prefIds = appliedList.stream()
                        .map(LoanPreferentialApplied::getPreferentialId)
                        .toList();
                rateList = productPreferentialRateRepository.findAllByPreferentialIdIn(prefIds);
            }
        }

        return LoanDetailResponse.of(app, product, contract, appliedList, rateList);
    }
}
