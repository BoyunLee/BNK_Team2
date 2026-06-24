package com.example.busanbank_loan.account.repository;

import com.example.busanbank_loan.account.entity.Account;
import com.example.busanbank_loan.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNo(String accountNo);

    Optional<Account> findFirstByCustomerOrderByIdAsc(Customer customer);
}
