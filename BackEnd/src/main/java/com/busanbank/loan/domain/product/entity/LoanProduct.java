package com.busanbank.loan.domain.product.entity;

import com.busanbank.loan.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "LOAN_PRODUCT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanProduct extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "base_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal baseRate;

    @Column(name = "loan_period")
    private String loanPeriod;

    @Column(name = "status", length = 20)
    private String status = "SALE";

    @Builder
    public LoanProduct(String productName, BigDecimal baseRate, String loanPeriod, String status) {
        this.productName = productName;
        this.baseRate = baseRate;
        this.loanPeriod = loanPeriod;
        this.status = (status != null) ? status : "SALE";
    }

    public void discontinue() {
        this.status = "DISCONTINUED";
    }

    public void update(String productName, BigDecimal baseRate, String loanPeriod, String status) {
        this.productName = productName;
        this.baseRate = baseRate;
        this.loanPeriod = loanPeriod;
        this.status = status;
    }
}
