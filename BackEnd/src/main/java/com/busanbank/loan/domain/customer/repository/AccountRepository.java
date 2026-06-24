package com.busanbank.loan.domain.customer.repository;

import com.busanbank.loan.domain.customer.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByCustomerId(Long customerId);

    Optional<Account> findByAccountNo(String accountNo);
}
