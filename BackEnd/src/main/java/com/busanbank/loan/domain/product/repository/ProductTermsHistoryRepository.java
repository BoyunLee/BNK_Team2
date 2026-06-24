package com.busanbank.loan.domain.product.repository;

import com.busanbank.loan.domain.product.entity.ProductTermsHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductTermsHistoryRepository extends JpaRepository<ProductTermsHistory, Long> {

    Optional<ProductTermsHistory> findTopByTermsIdOrderByTermsSeqDesc(Long termsId);
}
