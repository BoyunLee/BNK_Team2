package com.busanbank.loan.domain.loan.dto.request;

import java.util.List;

public record MydataConsentRequest(
        List<ConsentItem> consents
) {
    public record ConsentItem(
            String consentType,
            String dataProvider
    ) {}
}
