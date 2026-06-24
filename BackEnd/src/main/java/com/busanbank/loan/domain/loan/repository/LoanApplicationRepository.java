package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, String> {

    Optional<LoanApplication> findByLoanAccountNo(String loanAccountNo);

    boolean existsByCustomerIdAndStatusCodeNotIn(Long customerId, List<String> codes);

    List<LoanApplication> findAllByExpireAtBeforeAndStatusCodeNotIn(LocalDateTime now, List<String> codes);

    List<LoanApplication> findAllByCustomerIdOrderByAppliedAtDesc(Long customerId);
}
