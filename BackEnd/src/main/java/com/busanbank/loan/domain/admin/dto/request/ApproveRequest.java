package com.busanbank.loan.domain.admin.dto.request;

import jakarta.validation.constraints.NotNull;

/** scheduledDeployAt: ISO-8601 문자열(예: 2026-06-25T14:30:00). */
public record ApproveRequest(
        @NotNull String scheduledDeployAt,
        String comment
) {
}
