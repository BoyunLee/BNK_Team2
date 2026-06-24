package com.example.busanbank_loan.customer.service;

import com.example.busanbank_loan.account.entity.Account;
import com.example.busanbank_loan.account.service.AccountService;
import com.example.busanbank_loan.common.exception.BusinessException;
import com.example.busanbank_loan.common.response.ResultCode;
import com.example.busanbank_loan.customer.dto.RegisterRequest;
import com.example.busanbank_loan.customer.dto.RegisterResponse;
import com.example.busanbank_loan.customer.entity.Customer;
import com.example.busanbank_loan.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final EmailVerificationService emailVerificationService;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    /** 1.3 회원가입 + 계좌 자동 생성 */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        emailVerificationService.assertVerified(request.email());

        if (customerRepository.existsByEmail(request.email())) {
            throw new BusinessException(ResultCode.AUTH001);
        }

        LocalDate birthDate = parseBirthDate(request.birthDate());

        Customer customer = Customer.create(
                request.name(),
                request.phoneNo(),
                birthDate,
                request.address(),
                request.email(),
                passwordEncoder.encode(request.simplePassword()),
                passwordEncoder.encode(request.signaturePassword()));
        customerRepository.save(customer);

        Account account = accountService.createAccount(customer, request.accountPassword());

        return new RegisterResponse(customer.getId(), account.getAccountNo());
    }

    private LocalDate parseBirthDate(String raw) {
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.C001, "생년월일 형식이 올바르지 않습니다 (yyyy-MM-dd)");
        }
    }
}
