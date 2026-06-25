package com.busanbank.loan.domain.admin.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejectRequest(
        @NotBlank String comment
) {
}
