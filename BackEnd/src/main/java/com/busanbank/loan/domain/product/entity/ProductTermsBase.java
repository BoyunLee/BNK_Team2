package com.busanbank.loan.domain.product.entity;

import com.busanbank.loan.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRODUCT_TERMS_BASE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductTermsBase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terms_id")
    private Long termsId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "terms_type", nullable = false)
    private String termsType;

    @Column(name = "terms_path", nullable = false)
    private String termsPath;

    @Column(name = "active_yn", length = 1)
    private String activeYn = "Y";

    @Builder
    public ProductTermsBase(Long productId, String termsType, String termsPath, String activeYn) {
        this.productId = productId;
        this.termsType = termsType;
        this.termsPath = termsPath;
        this.activeYn = (activeYn != null) ? activeYn : "Y";
    }
}
