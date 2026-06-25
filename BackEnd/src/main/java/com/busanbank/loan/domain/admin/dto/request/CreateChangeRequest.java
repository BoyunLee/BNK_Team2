package com.busanbank.loan.domain.admin.dto.request;

import com.busanbank.loan.domain.admin.dto.ProductSnapshotDto;
import com.busanbank.loan.domain.admin.entity.ChangeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateChangeRequest(
        @NotNull ChangeType changeType,
        Long productId, // UPDATE/DISCONTINUE 시 대상. CREATE 면 null
        @NotBlank String title,
        @NotNull ProductSnapshotDto tobe
) {
}
