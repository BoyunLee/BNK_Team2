package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.LoanScreening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanScreeningRepository extends JpaRepository<LoanScreening, Long> {

    Optional<LoanScreening> findByLoanAccountNo(String loanAccountNo);
}
