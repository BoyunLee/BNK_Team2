package com.busanbank.loan.domain.loan.dto.request;

import java.util.List;

public record ContractTermsRequest(
        List<String> documentTypes
) {}
