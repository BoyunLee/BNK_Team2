package com.busanbank.loan.domain.customer.dto.response;

import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.entity.LoanContract;
import com.busanbank.loan.domain.loan.entity.LoanPreferentialApplied;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.entity.ProductPreferentialRate;
import com.busanbank.loan.global.util.MaskingUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record LoanDetailResponse(
        String loanAccountNo,
        String productName,
        String statusCode,
        String statusName,
        LocalDateTime appliedAt,
        LocalDateTime expireAt,
        ContractInfo contract
) {
    public record ContractInfo(
            Long loanAmount,
            BigDecimal finalRate,
            String repaymentType,
            String rateTypeCode,
            String loanPeriod,
            LocalDate maturityDate,
            String depositAccountNo,
            String loanDepositAccountNo,
            String fundPurpose,
            LocalDateTime executionDate,
            List<PreferentialRateInfo> preferentialRates
    ) {}

    public record PreferentialRateInfo(
            String conditionName,
            BigDecimal rateValue
    ) {}

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

    public static LoanDetailResponse of(
            LoanApplication app,
            LoanProduct product,
            LoanContract contract,
            List<LoanPreferentialApplied> appliedList,
            List<ProductPreferentialRate> rateList
    ) {
        ContractInfo contractInfo = null;
        if (contract != null) {
            Map<Long, ProductPreferentialRate> rateMap = rateList.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            ProductPreferentialRate::getPreferentialId, r -> r));

            List<PreferentialRateInfo> prefRates = appliedList.stream()
                    .map(a -> rateMap.get(a.getPreferentialId()))
                    .filter(java.util.Objects::nonNull)
                    .map(r -> new PreferentialRateInfo(r.getConditionName(), r.getRateValue()))
                    .toList();

            contractInfo = new ContractInfo(
                    contract.getLoanAmount(),
                    contract.getFinalRate(),
                    contract.getRepaymentType(),
                    contract.getRateTypeCode(),
                    contract.getLoanPeriod(),
                    contract.getMaturityDate(),
                    MaskingUtil.maskAccount(contract.getDepositAccountNo()),
                    MaskingUtil.maskAccount(contract.getLoanDepositAccountNo()),
                    contract.getFundPurpose(),
                    contract.getExecutionDate(),
                    prefRates
            );
        }

        return new LoanDetailResponse(
                app.getLoanAccountNo(),
                product != null ? product.getProductName() : "-",
                app.getStatusCode(),
                STATUS_NAMES.getOrDefault(app.getStatusCode(), "알 수 없음"),
                app.getAppliedAt(),
                app.getExpireAt(),
                contractInfo
        );
    }
}
