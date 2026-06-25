package com.busanbank.loan.domain.admin.dto.request;

import com.busanbank.loan.domain.admin.dto.ProductSnapshotDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateChangeRequest(
        @NotBlank String title,
        @NotNull ProductSnapshotDto tobe
) {
}
