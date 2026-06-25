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

    @Column(name = "category", length = 20)
    private String category;

    @Column(name = "mkpd_cd", length = 20)
    private String mkpdCd;

    @Column(name = "catchphrase", length = 300)
    private String catchphrase;

    @Column(name = "rate_min", length = 50)
    private String rateMin;

    @Column(name = "rate_max", length = 50)
    private String rateMax;

    @Builder
    public LoanProduct(String productName, BigDecimal baseRate, String loanPeriod, String status,
                       String category, String mkpdCd, String catchphrase, String rateMin, String rateMax) {
        this.productName = productName;
        this.baseRate = baseRate;
        this.loanPeriod = loanPeriod;
        this.status = (status != null) ? status : "SALE";
        this.category = category;
        this.mkpdCd = mkpdCd;
        this.catchphrase = catchphrase;
        this.rateMin = rateMin;
        this.rateMax = rateMax;
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

    /** 상품 변경 결재(형상이행) 시 스냅샷의 모든 편집 가능한 컬럼을 반영하고 판매상태로 둔다. */
    public void applyChange(String productName, String category, BigDecimal baseRate, String loanPeriod,
                            String catchphrase, String rateMin, String rateMax) {
        this.productName = productName;
        this.category = category;
        this.baseRate = baseRate;
        this.loanPeriod = loanPeriod;
        this.catchphrase = catchphrase;
        this.rateMin = rateMin;
        this.rateMax = rateMax;
        this.status = "SALE";
    }
}
