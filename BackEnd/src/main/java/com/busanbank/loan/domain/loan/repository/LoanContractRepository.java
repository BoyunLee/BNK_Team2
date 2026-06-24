package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.LoanContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanContractRepository extends JpaRepository<LoanContract, Long> {

    Optional<LoanContract> findByLoanAccountNo(String loanAccountNo);
}
