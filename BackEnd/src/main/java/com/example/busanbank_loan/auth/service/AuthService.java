package com.example.busanbank_loan.auth.service;

import com.example.busanbank_loan.account.entity.Account;
import com.example.busanbank_loan.account.repository.AccountRepository;
import com.example.busanbank_loan.auth.dto.LoginRequest;
import com.example.busanbank_loan.common.exception.BusinessException;
import com.example.busanbank_loan.common.response.ResultCode;
import com.example.busanbank_loan.customer.entity.Customer;
import com.example.busanbank_loan.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    /** 1.4 로그인: 이메일로 고객 식별 + 간편비밀번호 검증 (세션 발급은 컨트롤러) */
    @Transactional(readOnly = true)
    public LoginResult login(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ResultCode.AUTH004));

        if (!passwordEncoder.matches(request.simplePassword(), customer.getSimplePassword())) {
            throw new BusinessException(ResultCode.AUTH004);
        }
        if (!customer.isActive()) {
            throw new BusinessException(ResultCode.CU003);
        }

        String accountNo = accountRepository.findFirstByCustomerOrderByIdAsc(customer)
                .map(Account::getAccountNo)
                .orElse(null);

        return new LoginResult(customer, accountNo);
    }
}
