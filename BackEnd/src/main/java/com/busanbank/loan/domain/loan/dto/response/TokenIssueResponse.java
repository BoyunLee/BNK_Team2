package com.busanbank.loan.domain.loan.dto.response;

import java.time.LocalDateTime;

public record TokenIssueResponse(
        String tokenId,
        LocalDateTime expireAt
) {}
