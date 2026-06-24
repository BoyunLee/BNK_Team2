package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.IncomeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncomeInfoRepository extends JpaRepository<IncomeInfo, Long> {

    Optional<IncomeInfo> findByLoanAccountNo(String loanAccountNo);
}
