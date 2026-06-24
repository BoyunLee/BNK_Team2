package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.LoanPreferentialApplied;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoanPreferentialAppliedRepository extends JpaRepository<LoanPreferentialApplied, Long> {

    List<LoanPreferentialApplied> findAllByLoanAccountNo(String loanAccountNo);

    @Transactional
    @Modifying
    @Query("DELETE FROM LoanPreferentialApplied lpa WHERE lpa.loanAccountNo = :loanAccountNo")
    void deleteAllByLoanAccountNo(@Param("loanAccountNo") String loanAccountNo);
}
