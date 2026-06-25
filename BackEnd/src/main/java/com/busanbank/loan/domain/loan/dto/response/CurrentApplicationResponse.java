package com.busanbank.loan.domain.loan.dto.response;

import com.busanbank.loan.domain.loan.entity.LoanApplication;

/** 진행 중(미완료/미만료) 대출 신청서 — 재진입 복원용 */
public record CurrentApplicationResponse(
        String loanAccountNo,
        Long productId,
        String statusCode
) {
    public static CurrentApplicationResponse from(LoanApplication application) {
        return new CurrentApplicationResponse(
                application.getLoanAccountNo(),
                application.getProductId(),
                application.getStatusCode()
        );
    }
}
