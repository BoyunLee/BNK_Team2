package com.busanbank.loan.domain.admin.dto.request;

import jakarta.validation.constraints.NotNull;

public record SubmitApprovalRequest(
        @NotNull Long approverId
) {
}
