package com.busanbank.loan.domain.product.entity;

import com.busanbank.loan.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCT_PREFERENTIAL_RATE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductPreferentialRate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preferential_id")
    private Long preferentialId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "condition_code", nullable = false)
    private String conditionCode;

    @Column(name = "condition_name", nullable = false)
    private String conditionName;

    @Column(name = "rate_value", nullable = false, precision = 5, scale = 2)
    private BigDecimal rateValue;

    @Column(name = "description")
    private String description;

    @Builder
    public ProductPreferentialRate(Long productId, String conditionCode, String conditionName,
                                   BigDecimal rateValue, String description) {
        this.productId = productId;
        this.conditionCode = conditionCode;
        this.conditionName = conditionName;
        this.rateValue = rateValue;
        this.description = description;
    }
}
