package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.SuitabilityResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuitabilityResponseRepository extends JpaRepository<SuitabilityResponse, Long> {

    List<SuitabilityResponse> findAllByLoanAccountNo(String loanAccountNo);
}
