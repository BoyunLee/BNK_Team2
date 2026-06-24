package com.busanbank.loan.domain.loan.controller;

import com.busanbank.loan.domain.loan.dto.request.IncomeInfoRequest;
import com.busanbank.loan.domain.loan.dto.response.MydataResponse;
import com.busanbank.loan.domain.loan.dto.response.ScreeningResponse;
import com.busanbank.loan.domain.loan.service.ScreeningService;
import com.busanbank.loan.global.response.ApiResponse;
import com.busanbank.loan.global.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loans/applications")
@RequiredArgsConstructor
public class LoanScreeningController {

    private final ScreeningService screeningService;

    @PostMapping("/{loanAccountNo}/income")
    public ApiResponse<Void> saveIncomeInfo(
            @PathVariable String loanAccountNo,
            @RequestBody IncomeInfoRequest request) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        screeningService.saveIncomeInfo(loanAccountNo, customerId, request);
        return ApiResponse.ok("직장·소득정보가 등록되었습니다.");
    }

    @GetMapping("/{loanAccountNo}/mydata")
    public ApiResponse<MydataResponse> getMydataResult(
            @PathVariable String loanAccountNo) {
        MydataResponse response = screeningService.getMydataResult(loanAccountNo);
        return ApiResponse.ok(response);
    }

    @PostMapping("/{loanAccountNo}/screening")
    public ApiResponse<ScreeningResponse> calculateScreening(
            @PathVariable String loanAccountNo) {
        ScreeningResponse response = screeningService.calculateScreening(loanAccountNo);
        return ApiResponse.ok(response, "대출 한도 산출이 완료되었습니다.");
    }

    @GetMapping("/{loanAccountNo}/screening")
    public ApiResponse<ScreeningResponse> getScreeningResult(
            @PathVariable String loanAccountNo) {
        ScreeningResponse response = screeningService.getScreeningResult(loanAccountNo);
        return ApiResponse.ok(response);
    }
}
