package com.example.busanbank_loan.account.service;

import com.example.busanbank_loan.account.entity.Account;
import com.example.busanbank_loan.account.repository.AccountRepository;
import com.example.busanbank_loan.customer.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final String ACCOUNT_PREFIX = "110";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    /** 회원가입 시 입출금 계좌 자동 생성 */
    @Transactional
    public Account createAccount(Customer customer, String rawAccountPassword) {
        String accountNo = generateUniqueAccountNo();
        Account account = Account.create(customer, accountNo, passwordEncoder.encode(rawAccountPassword));
        return accountRepository.save(account);
    }

    private String generateUniqueAccountNo() {
        String accountNo;
        do {
            accountNo = ACCOUNT_PREFIX + String.format("%09d", random.nextInt(1_000_000_000));
        } while (accountRepository.existsByAccountNo(accountNo));
        return accountNo;
    }
}
