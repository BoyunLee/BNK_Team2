package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.ApplicationDocumentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationDocumentLogRepository extends JpaRepository<ApplicationDocumentLog, Long> {

    Optional<ApplicationDocumentLog> findByLoanAccountNoAndDocumentType(String loanAccountNo, String documentType);

    List<ApplicationDocumentLog> findAllByLoanAccountNo(String loanAccountNo);
}
