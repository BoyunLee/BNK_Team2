package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.IncomeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncomeInfoRepository extends JpaRepository<IncomeInfo, Long> {

    Optional<IncomeInfo> findByLoanAccountNo(String loanAccountNo);

    /** 중복 입력 대비 — 최신 1건 */
    Optional<IncomeInfo> findTopByLoanAccountNoOrderByIncomeIdDesc(String loanAccountNo);
}
