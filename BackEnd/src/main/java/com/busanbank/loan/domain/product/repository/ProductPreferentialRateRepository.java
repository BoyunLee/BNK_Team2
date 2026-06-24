package com.busanbank.loan.domain.product.repository;

import com.busanbank.loan.domain.product.entity.ProductPreferentialRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductPreferentialRateRepository extends JpaRepository<ProductPreferentialRate, Long> {

    List<ProductPreferentialRate> findAllByProductId(Long productId);

    List<ProductPreferentialRate> findAllByPreferentialIdIn(List<Long> ids);

    void deleteAllByProductId(Long productId);
}
