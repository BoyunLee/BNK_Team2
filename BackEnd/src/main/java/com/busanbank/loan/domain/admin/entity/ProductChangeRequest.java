package com.busanbank.loan.domain.admin.entity;

import com.busanbank.loan.domain.admin.dto.ProductSnapshotDto;
import com.busanbank.loan.global.audit.BaseTimeEntity;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 상품 변경 신청서 = 결재문서. TO-BE 를 들고 다니며 라이브(AS-IS)와 분리된다.
 * 작성(DRAFT) → 결재상신(PENDING) → 승인(APPROVED, 배포예약) / 반려(REJECTED)
 * → 스케줄러 형상이행(DEPLOYED). 배포 전까지 라이브(LOAN_PRODUCT)는 건드리지 않는다.
 */
@Entity
@Table(name = "PRODUCT_CHANGE_REQUEST")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductChangeRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 20)
    private ChangeType changeType;

    @Column(name = "product_id")
    private Long productId; // UPDATE/DISCONTINUE 대상(=AS-IS). CREATE 면 null(배포 후 채워짐)

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Convert(converter = ProductSnapshotConverter.class)
    @Column(name = "asis_snapshot", columnDefinition = "LONGTEXT")
    private ProductSnapshotDto asis; // 상신 시점 캡처(비교/감사용)

    @Convert(converter = ProductSnapshotConverter.class)
    @Column(name = "tobe_snapshot", columnDefinition = "LONGTEXT", nullable = false)
    private ProductSnapshotDto tobe; // 변경안

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ChangeStatus status;

    @Column(name = "drafter_id", nullable = false)
    private Long drafterId;

    @Column(name = "drafter_name", nullable = false, length = 50)
    private String drafterName;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approver_name", length = 50)
    private String approverName;

    @Column(name = "decision_comment", columnDefinition = "TEXT")
    private String decisionComment;

    @Column(name = "scheduled_deploy_at")
    private LocalDateTime scheduledDeployAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "deployed_at")
    private LocalDateTime deployedAt;

    @Builder
    public ProductChangeRequest(ChangeType changeType, Long productId, String title,
                                ProductSnapshotDto asis, ProductSnapshotDto tobe,
                                Long drafterId, String drafterName) {
        this.changeType = changeType;
        this.productId = productId;
        this.title = title;
        this.asis = asis;
        this.tobe = tobe;
        this.drafterId = drafterId;
        this.drafterName = drafterName;
        this.status = ChangeStatus.DRAFT;
    }

    private boolean isEditable() {
        return status == ChangeStatus.DRAFT || status == ChangeStatus.REJECTED;
    }

    /** 작성중/반려 신청서 내용 수정. 반려본은 다시 작성중으로 되돌린다. */
    public void editDraft(String title, ProductSnapshotDto tobe, ProductSnapshotDto asis) {
        if (!isEditable()) throw new BusinessException(ErrorCode.INVALID_CHANGE_STATE);
        this.title = title;
        this.tobe = tobe;
        this.asis = asis;
        if (status == ChangeStatus.REJECTED) {
            this.status = ChangeStatus.DRAFT;
            this.decisionComment = null;
        }
    }

    /** 결재상신 — 책임자 지정 후 PENDING. 상신 시점의 AS-IS 를 캡처한다. */
    public void submit(Long approverId, String approverName, ProductSnapshotDto asisCapture) {
        if (!isEditable()) throw new BusinessException(ErrorCode.INVALID_CHANGE_STATE);
        this.approverId = approverId;
        this.approverName = approverName;
        this.asis = asisCapture;
        this.status = ChangeStatus.PENDING;
        this.submittedAt = LocalDateTime.now();
        this.decisionComment = null;
    }

    /** 승인 — 배포예약시각 지정. */
    public void approve(LocalDateTime scheduledDeployAt, String comment) {
        if (status != ChangeStatus.PENDING) throw new BusinessException(ErrorCode.INVALID_CHANGE_STATE);
        this.status = ChangeStatus.APPROVED;
        this.scheduledDeployAt = scheduledDeployAt;
        this.decisionComment = comment;
        this.decidedAt = LocalDateTime.now();
    }

    /** 반려. */
    public void reject(String comment) {
        if (status != ChangeStatus.PENDING) throw new BusinessException(ErrorCode.INVALID_CHANGE_STATE);
        this.status = ChangeStatus.REJECTED;
        this.decisionComment = comment;
        this.decidedAt = LocalDateTime.now();
    }

    /** 신청 취소(작성중/반려). */
    public void cancel() {
        if (!isEditable()) throw new BusinessException(ErrorCode.INVALID_CHANGE_STATE);
        this.status = ChangeStatus.CANCELLED;
    }

    /** 형상이행 완료 처리. CREATE 의 경우 생성된 productId 를 채운다. */
    public void markDeployed(Long deployedProductId) {
        if (status != ChangeStatus.APPROVED) throw new BusinessException(ErrorCode.INVALID_CHANGE_STATE);
        this.productId = deployedProductId;
        this.status = ChangeStatus.DEPLOYED;
        this.deployedAt = LocalDateTime.now();
    }

    /** 데모용: 예약시각을 현재로 당겨 즉시 배포 가능하게 한다. */
    public void scheduleNow() {
        if (status != ChangeStatus.APPROVED) throw new BusinessException(ErrorCode.INVALID_CHANGE_STATE);
        this.scheduledDeployAt = LocalDateTime.now();
    }

    public boolean isDueForDeploy(LocalDateTime now) {
        return status == ChangeStatus.APPROVED
                && scheduledDeployAt != null
                && !scheduledDeployAt.isAfter(now);
    }
}
