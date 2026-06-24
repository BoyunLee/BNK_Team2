package com.busanbank.loan.domain.customer.dto.response;

import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.entity.LoanContract;
import com.busanbank.loan.domain.product.entity.LoanProduct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record LoanSummaryResponse(
        String loanAccountNo,
        Long productId,
        String productName,
        String statusCode,
        String statusName,
        Long loanAmount,
        BigDecimal finalRate,
        LocalDate maturityDate,
        LocalDateTime appliedAt
) {
    private static final Map<String, String> STATUS_NAMES = Map.ofEntries(
            Map.entry("1", "신청서 작성 중"),
            Map.entry("2", "적합성 확인 완료"),
            Map.entry("3", "마이데이터 동의 완료"),
            Map.entry("4", "사전 전자서명 완료"),
            Map.entry("5", "소득 정보 입력 완료"),
            Map.entry("6", "한도 조회 완료"),
            Map.entry("7", "대출 조건 입력 완료"),
            Map.entry("8", "약정 전자서명 완료"),
            Map.entry("9", "대출 실행 완료"),
            Map.entry("X", "만료"),
            Map.entry("R", "취소/거절")
    );

    public static LoanSummaryResponse of(LoanApplication app, LoanProduct product, LoanContract contract) {
        return new LoanSummaryResponse(
                app.getLoanAccountNo(),
                app.getProductId(),
                product != null ? product.getProductName() : "-",
                app.getStatusCode(),
                STATUS_NAMES.getOrDefault(app.getStatusCode(), "알 수 없음"),
                contract != null ? contract.getLoanAmount() : null,
                contract != null ? contract.getFinalRate() : null,
                contract != null ? contract.getMaturityDate() : null,
                app.getAppliedAt()
        );
    }
}
