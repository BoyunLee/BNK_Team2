package com.busanbank.loan.domain.customer.service;

import com.busanbank.loan.domain.customer.dto.request.LoginRequest;
import com.busanbank.loan.domain.customer.dto.request.RegisterRequest;
import com.busanbank.loan.domain.customer.dto.response.LoginResponse;
import com.busanbank.loan.domain.customer.dto.response.RegisterResponse;
import com.busanbank.loan.domain.customer.entity.Account;
import com.busanbank.loan.domain.customer.entity.Customer;
import com.busanbank.loan.domain.customer.repository.AccountRepository;
import com.busanbank.loan.domain.customer.repository.CustomerRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import com.busanbank.loan.global.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

import static com.busanbank.loan.domain.customer.dto.response.CustomerResponse.masked;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void checkEmailForSend(String email) {
        if (customerRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    public RegisterResponse register(RegisterRequest req) {
        if (!emailService.isEmailVerified(req.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        if (customerRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(req.getBirthDate());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "생년월일 형식이 올바르지 않습니다 (yyyy-MM-dd)");
        }

        Customer customer = Customer.builder()
                .name(req.getName())
                .phoneNo(req.getPhoneNo())
                .birthDate(birthDate)
                .address(req.getAddress())
                .email(req.getEmail())
                .simplePassword(passwordEncoder.encode(req.getSimplePassword()))
                .build();
        Customer saved = customerRepository.save(customer);

        String accountNo = "110" + String.format("%09d", new Random().nextLong(1_000_000_000L));
        Account account = Account.builder()
                .accountNo(accountNo)
                .customerId(saved.getCustomerId())
                .accountPassword(passwordEncoder.encode(req.getAccountPassword()))
                .build();
        accountRepository.save(account);

        return RegisterResponse.builder()
                .customerId(saved.getCustomerId())
                .accountNo(accountNo)
                .build();
    }

    public LoginResponse login(LoginRequest req, HttpServletRequest request) {
        Customer customer = customerRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!customer.isActive()) {
            throw new BusinessException(ErrorCode.INACTIVE_CUSTOMER);
        }

        if (!passwordEncoder.matches(req.getSimplePassword(), customer.getSimplePassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        SessionUtil.setCurrentCustomerId(request, customer.getCustomerId());

        Account account = accountRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        return LoginResponse.builder()
                .customer(masked(customer))
                .accountNo(account.getAccountNo())
                .build();
    }

    public void logout(HttpServletRequest request) {
        SessionUtil.invalidate(request);
    }
}
