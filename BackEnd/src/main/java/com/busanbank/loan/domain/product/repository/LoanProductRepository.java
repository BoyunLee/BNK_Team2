package com.busanbank.loan.domain.product.repository;

import com.busanbank.loan.domain.product.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {

    List<LoanProduct> findAllByStatus(String status);
}
