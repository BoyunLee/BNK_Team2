package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.CustomerVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerVerificationRepository extends JpaRepository<CustomerVerification, Long> {

    Optional<CustomerVerification> findByLoanAccountNoAndVerifyStep(String loanAccountNo, String verifyStep);
}
