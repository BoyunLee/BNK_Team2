package com.busanbank.loan.domain.loan.dto.response;

import com.busanbank.loan.domain.loan.entity.LoanApplication;

import java.time.LocalDateTime;

public record CreateApplicationResponse(
        String loanAccountNo,
        LocalDateTime expireAt
) {
    public static CreateApplicationResponse from(LoanApplication application) {
        return new CreateApplicationResponse(application.getLoanAccountNo(), application.getExpireAt());
    }
}
