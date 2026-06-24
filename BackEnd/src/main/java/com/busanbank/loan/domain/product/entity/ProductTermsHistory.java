package com.busanbank.loan.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCT_TERMS_HISTORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProductTermsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "terms_id", nullable = false)
    private Long termsId;

    @Column(name = "terms_seq", nullable = false)
    private int termsSeq;

    @Column(name = "terms_path", nullable = false)
    private String termsPath;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ProductTermsHistory(Long termsId, int termsSeq, String termsPath) {
        this.termsId = termsId;
        this.termsSeq = termsSeq;
        this.termsPath = termsPath;
    }
}
