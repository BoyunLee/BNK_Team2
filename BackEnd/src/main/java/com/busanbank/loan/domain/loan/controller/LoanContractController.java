package com.busanbank.loan.domain.loan.controller;

import com.busanbank.loan.domain.loan.dto.request.*;
import com.busanbank.loan.domain.loan.dto.response.ConditionsResponse;
import com.busanbank.loan.domain.loan.dto.response.ConfirmationResponse;
import com.busanbank.loan.domain.loan.dto.response.ExecuteResponse;
import com.busanbank.loan.domain.loan.service.ContractService;
import com.busanbank.loan.domain.loan.service.VerificationService;
import com.busanbank.loan.global.response.ApiResponse;
import com.busanbank.loan.global.util.SessionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loans/applications")
@RequiredArgsConstructor
public class LoanContractController {

    private final ContractService contractService;
    private final VerificationService verificationService;

    @PostMapping("/{loanAccountNo}/contract/terms")
    public ApiResponse<Void> agreeTerms(
            @PathVariable String loanAccountNo,
            @RequestParam(required = false) Long productId,
            @RequestBody ContractTermsRequest request) {
        contractService.agreeTerms(loanAccountNo, productId, request.documentTypes());
        return ApiResponse.ok("약관 동의가 완료되었습니다.");
    }

    @PostMapping("/{loanAccountNo}/contract/conditions")
    public ApiResponse<ConditionsResponse> saveConditions(
            @PathVariable String loanAccountNo,
            @RequestBody ContractConditionsRequest request) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        ConditionsResponse response = contractService.saveConditions(loanAccountNo, customerId, request);
        return ApiResponse.ok(response, "대출 조건이 등록되었습니다.");
    }

    @GetMapping("/{loanAccountNo}/contract/confirm")
    public ApiResponse<ConfirmationResponse> getConfirmation(
            @PathVariable String loanAccountNo) {
        ConfirmationResponse response = contractService.getConfirmation(loanAccountNo);
        return ApiResponse.ok(response);
    }

    @PostMapping("/{loanAccountNo}/verification/contract")
    public ApiResponse<Void> verifyContractSignature(
            @PathVariable String loanAccountNo,
            @RequestBody SignatureRequest request) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        VerificationService.SignatureData signatureData = new VerificationService.SignatureData(
                request.signStep(), request.signType(), request.tokenId(), request.originalValue());
        verificationService.verifyContractSignature(loanAccountNo, customerId, signatureData);
        return ApiResponse.ok("약정 전자서명이 완료되었습니다.");
    }

    @PostMapping("/{loanAccountNo}/execute")
    public ApiResponse<ExecuteResponse> executeLoan(
            @PathVariable String loanAccountNo,
            @Valid @RequestBody ExecuteRequest request) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        ExecuteResponse response = contractService.executeLoan(loanAccountNo, customerId, request.simplePassword());
        return ApiResponse.ok(response, "대출이 실행되었습니다.");
    }
}
