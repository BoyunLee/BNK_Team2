package com.busanbank.loan.domain.loan.dto.response;

import com.busanbank.loan.domain.loan.entity.ApplicationDocumentLog;

import java.time.LocalDateTime;

public record DocumentActionResponse(
        String documentType,
        LocalDateTime viewedAt,
        LocalDateTime agreedAt
) {
    public static DocumentActionResponse viewed(ApplicationDocumentLog log) {
        return new DocumentActionResponse(log.getDocumentType(), log.getViewedAt(), null);
    }

    public static DocumentActionResponse agreed(ApplicationDocumentLog log) {
        return new DocumentActionResponse(log.getDocumentType(), log.getViewedAt(), log.getAgreedAt());
    }
}
