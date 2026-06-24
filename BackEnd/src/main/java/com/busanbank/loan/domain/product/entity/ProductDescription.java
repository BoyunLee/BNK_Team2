package com.busanbank.loan.domain.product.entity;

import com.busanbank.loan.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRODUCT_DESCRIPTION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDescription extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "description_id")
    private Long descriptionId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "attr_key", nullable = false)
    private String attrKey;

    @Lob
    @Column(name = "attr_value", columnDefinition = "LONGTEXT")
    private String attrValue;

    @Column(name = "sort_order")
    private int sortOrder;

    @Builder
    public ProductDescription(Long productId, String attrKey, String attrValue, int sortOrder) {
        this.productId = productId;
        this.attrKey = attrKey;
        this.attrValue = attrValue;
        this.sortOrder = sortOrder;
    }

    public void updateAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }
}
