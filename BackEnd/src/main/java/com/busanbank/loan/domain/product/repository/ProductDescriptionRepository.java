package com.busanbank.loan.domain.product.repository;

import com.busanbank.loan.domain.product.entity.ProductDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductDescriptionRepository extends JpaRepository<ProductDescription, Long> {

    List<ProductDescription> findAllByProductIdOrderBySortOrderAsc(Long productId);

    Optional<ProductDescription> findByProductIdAndAttrKey(Long productId, String attrKey);

    void deleteAllByProductId(Long productId);
}
