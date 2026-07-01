package com.busanbank.loan.domain.customer.repository;

import com.busanbank.loan.domain.customer.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByCustomerId(Long customerId);

    /** 계좌 종류로 단건 조회 (고객당 입출금 계좌는 1개) */
    Optional<Account> findByCustomerIdAndAccountType(Long customerId, String accountType);

    Optional<Account> findByAccountNo(String accountNo);
}
