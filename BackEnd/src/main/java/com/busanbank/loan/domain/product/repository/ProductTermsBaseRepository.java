package com.busanbank.loan.domain.product.repository;

import com.busanbank.loan.domain.product.entity.ProductTermsBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductTermsBaseRepository extends JpaRepository<ProductTermsBase, Long> {

    Optional<ProductTermsBase> findByProductIdAndTermsTypeAndActiveYn(Long productId, String termsType, String activeYn);

    List<ProductTermsBase> findAllByProductId(Long productId);
}
