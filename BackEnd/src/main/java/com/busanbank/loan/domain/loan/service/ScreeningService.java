package com.busanbank.loan.domain.loan.service;

import com.busanbank.loan.domain.loan.dto.request.IncomeInfoRequest;
import com.busanbank.loan.domain.loan.dto.response.MydataResponse;
import com.busanbank.loan.domain.loan.dto.response.ScreeningResponse;
import com.busanbank.loan.domain.loan.entity.IncomeInfo;
import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.entity.LoanScreening;
import com.busanbank.loan.domain.loan.repository.IncomeInfoRepository;
import com.busanbank.loan.domain.loan.repository.LoanScreeningRepository;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final LoanScreeningRepository loanScreeningRepository;
    private final IncomeInfoRepository incomeInfoRepository;
    private final LoanProductRepository loanProductRepository;
    private final MydataService mydataService;
    private final LoanApplicationService loanApplicationService;

    @Transactional
    public void saveIncomeInfo(String loanAccountNo, Long customerId, IncomeInfoRequest data) {
        LoanApplication application = loanApplicationService.findAndValidate(loanAccountNo, "4");

        IncomeInfo incomeInfo = IncomeInfo.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .companyName(data.companyName())
                .jobType(data.jobType())
                .employmentType(data.employmentType())
                .annualIncome(data.annualIncome())
                .build();

        incomeInfoRepository.save(incomeInfo);
        application.updateStatus("5");
    }

    @Transactional(readOnly = true)
    public MydataResponse getMydataResult(String loanAccountNo) {
        loanApplicationService.findApplication(loanAccountNo);

        IncomeInfo incomeInfo = incomeInfoRepository.findTopByLoanAccountNoOrderByIncomeIdDesc(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        MydataService.MydataResult result = mydataService.fetchPublicData(loanAccountNo, incomeInfo.getAnnualIncome());
        return MydataResponse.from(result);
    }

    @Transactional
    public ScreeningResponse calculateScreening(String loanAccountNo) {
        LoanApplication application = loanApplicationService.findAndValidate(loanAccountNo, "5");

        IncomeInfo incomeInfo = incomeInfoRepository.findTopByLoanAccountNoOrderByIncomeIdDesc(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        LoanProduct product = loanProductRepository.findById(application.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        long maxLimitAmt = Math.min((long) (incomeInfo.getAnnualIncome() * 0.5), 100_000_000L);
        BigDecimal appliedBaseRate = product.getBaseRate();
        String result = "APPROVED";

        LoanScreening screening = LoanScreening.builder()
                .loanAccountNo(loanAccountNo)
                .maxLimitAmt(maxLimitAmt)
                .appliedBaseRate(appliedBaseRate)
                .result(result)
                .build();

        loanScreeningRepository.save(screening);
        application.updateStatus("6");

        return new ScreeningResponse(maxLimitAmt, appliedBaseRate, result);
    }

    @Transactional(readOnly = true)
    public ScreeningResponse getScreeningResult(String loanAccountNo) {
        LoanScreening screening = loanScreeningRepository.findByLoanAccountNo(loanAccountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCREENING_NOT_FOUND));

        return new ScreeningResponse(screening.getMaxLimitAmt(), screening.getAppliedBaseRate(), screening.getResult());
    }
}
