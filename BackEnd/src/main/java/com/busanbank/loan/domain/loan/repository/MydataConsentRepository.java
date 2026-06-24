package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.MydataConsent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MydataConsentRepository extends JpaRepository<MydataConsent, Long> {

    List<MydataConsent> findAllByLoanAccountNo(String loanAccountNo);
}
