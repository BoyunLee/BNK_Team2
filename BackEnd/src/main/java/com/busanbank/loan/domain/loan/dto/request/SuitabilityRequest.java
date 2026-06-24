package com.busanbank.loan.domain.loan.dto.request;

import java.util.List;

public record SuitabilityRequest(
        List<SuitabilityItem> responses
) {
    public record SuitabilityItem(
            String questionCode,
            String question,
            String answer
    ) {}
}
