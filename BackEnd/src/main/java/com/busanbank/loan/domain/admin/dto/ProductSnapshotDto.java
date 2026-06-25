package com.busanbank.loan.domain.admin.dto;

/**
 * 상품 한 건의 전체 데이터 스냅샷(AS-IS / TO-BE 공통).
 * 프론트(lib/admin.ts ProductSnapshot)와 동일 형태. 폼 편의를 위해 모두 문자열.
 */
public record ProductSnapshotDto(
        String productName,
        String category,
        String baseRate,
        String rateMin,
        String rateMax,
        String loanPeriod,
        String catchphrase,
        String target,
        String loanLimit,
        String repayment,
        String summary
) {
}
