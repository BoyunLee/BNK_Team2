package com.busanbank.loan.domain.admin.dto.response;

import com.busanbank.loan.domain.admin.dto.ProductSnapshotDto;
import com.busanbank.loan.domain.admin.entity.ProductChangeRequest;

import java.time.LocalDateTime;

/** 프론트 lib/admin.ts ProductChangeRequest 와 동일 형태. 날짜는 ISO 문자열. */
public record ChangeRequestResponse(
        Long id,
        String changeType,
        Long productId,
        String title,
        ProductSnapshotDto asis,
        ProductSnapshotDto tobe,
        String status,
        Long drafterId,
        String drafterName,
        Long approverId,
        String approverName,
        String decisionComment,
        String scheduledDeployAt,
        String createdAt,
        String submittedAt,
        String decidedAt,
        String deployedAt
) {
    private static String iso(LocalDateTime t) {
        return t == null ? null : t.toString();
    }

    public static ChangeRequestResponse from(ProductChangeRequest r) {
        return new ChangeRequestResponse(
                r.getId(),
                r.getChangeType().name(),
                r.getProductId(),
                r.getTitle(),
                r.getAsis(),
                r.getTobe(),
                r.getStatus().name(),
                r.getDrafterId(),
                r.getDrafterName(),
                r.getApproverId(),
                r.getApproverName(),
                r.getDecisionComment(),
                iso(r.getScheduledDeployAt()),
                iso(r.getCreatedAt()),
                iso(r.getSubmittedAt()),
                iso(r.getDecidedAt()),
                iso(r.getDeployedAt())
        );
    }
}
