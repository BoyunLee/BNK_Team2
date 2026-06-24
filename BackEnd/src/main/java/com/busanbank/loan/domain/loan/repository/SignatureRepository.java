package com.busanbank.loan.domain.loan.repository;

import com.busanbank.loan.domain.loan.entity.Signature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignatureRepository extends JpaRepository<Signature, Long> {

    Optional<Signature> findTopByLoanAccountNoAndSignStepOrderByCreatedAtDesc(String loanAccountNo, String signStep);

    Optional<Signature> findByTokenId(String tokenId);
}
