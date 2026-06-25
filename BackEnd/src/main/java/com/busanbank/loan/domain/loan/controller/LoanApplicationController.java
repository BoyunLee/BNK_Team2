package com.busanbank.loan.domain.loan.controller;

import com.busanbank.loan.domain.loan.dto.request.*;
import com.busanbank.loan.domain.loan.dto.response.CreateApplicationResponse;
import com.busanbank.loan.domain.loan.dto.response.CurrentApplicationResponse;
import com.busanbank.loan.domain.loan.dto.response.DocumentActionResponse;
import com.busanbank.loan.domain.loan.dto.response.SignatureResponse;
import com.busanbank.loan.domain.loan.dto.response.TokenIssueResponse;
import com.busanbank.loan.domain.loan.entity.ApplicationDocumentLog;
import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.service.*;
import com.busanbank.loan.global.response.ApiResponse;
import com.busanbank.loan.global.util.SessionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loans/applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;
    private final VerificationService verificationService;
    private final DocumentService documentService;
    private final SignatureService signatureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateApplicationResponse> createApplication(
            @Valid @RequestBody CreateApplicationRequest request) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        LoanApplication application = loanApplicationService.createApplication(customerId, request.productId());
        return ApiResponse.created(CreateApplicationResponse.from(application), "대출 신청서가 생성되었습니다.");
    }

    @GetMapping("/current")
    public ApiResponse<CurrentApplicationResponse> getCurrentApplication(@RequestParam Long productId) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        LoanApplication application = loanApplicationService.getCurrentApplication(customerId, productId);
        return ApiResponse.ok(application == null ? null : CurrentApplicationResponse.from(application));
    }

    @PostMapping("/{loanAccountNo}/verification/suitability")
    public ApiResponse<Void> verifySuitability(
            @PathVariable String loanAccountNo,
            @Valid @RequestBody VerificationRequest request) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        verificationService.verifySuitability(loanAccountNo, customerId, request.simplePassword());
        return ApiResponse.ok("본인인증이 완료되었습니다.");
    }

    @PostMapping("/{loanAccountNo}/mydata-consent")
    public ApiResponse<Void> saveMydataConsent(
            @PathVariable String loanAccountNo,
            @RequestBody MydataConsentRequest request) {
        loanApplicationService.saveMydataConsent(loanAccountNo, request.consents());
        return ApiResponse.ok("공공마이데이터 이용 동의가 완료되었습니다.");
    }

    @PostMapping("/{loanAccountNo}/documents/{documentType}/view")
    public ApiResponse<DocumentActionResponse> recordDocumentView(
            @PathVariable String loanAccountNo,
            @PathVariable String documentType,
            @RequestParam Long productId) {
        ApplicationDocumentLog log = documentService.recordView(loanAccountNo, productId, documentType);
        return ApiResponse.ok(DocumentActionResponse.viewed(log), "서류 열람 기록이 저장되었습니다.");
    }

    @PostMapping("/{loanAccountNo}/documents/{documentType}/agree")
    public ApiResponse<DocumentActionResponse> recordDocumentAgree(
            @PathVariable String loanAccountNo,
            @PathVariable String documentType) {
        ApplicationDocumentLog log = documentService.recordAgree(loanAccountNo, documentType);
        return ApiResponse.ok(DocumentActionResponse.agreed(log), "서류 동의가 완료되었습니다.");
    }

    @PostMapping("/{loanAccountNo}/signatures")
    public ApiResponse<SignatureResponse> sign(
            @PathVariable String loanAccountNo,
            @RequestBody SignatureRequest request) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        Long signatureId = signatureService.sign(
                loanAccountNo, customerId,
                request.signStep(), request.signType(), request.tokenId(), request.originalValue());
        return ApiResponse.ok(new SignatureResponse(signatureId), "전자서명이 완료되었습니다.");
    }

    @PatchMapping("/{loanAccountNo}/cancel")
    public ApiResponse<Void> cancelApplication(@PathVariable String loanAccountNo) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        loanApplicationService.cancelApplication(loanAccountNo, customerId);
        return ApiResponse.ok("대출 신청이 취소되었습니다.");
    }

    @PostMapping("/{loanAccountNo}/signatures/token")
    public ApiResponse<TokenIssueResponse> issueToken(
            @PathVariable String loanAccountNo,
            @Valid @RequestBody TokenIssueRequest request) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        TokenIssueResponse response = signatureService.issueToken(loanAccountNo, customerId, request.signType());
        return ApiResponse.ok(response, "전자서명 토큰이 발급되었습니다.");
    }
}
