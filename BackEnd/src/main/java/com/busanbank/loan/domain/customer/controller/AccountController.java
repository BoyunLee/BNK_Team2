package com.busanbank.loan.domain.customer.controller;

import com.busanbank.loan.domain.customer.dto.response.LoanDetailResponse;
import com.busanbank.loan.domain.customer.dto.response.LoanSummaryResponse;
import com.busanbank.loan.domain.customer.dto.response.MyAccountResponse;
import com.busanbank.loan.domain.customer.service.AccountService;
import com.busanbank.loan.global.response.ApiResponse;
import com.busanbank.loan.global.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers/me")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account")
    public ApiResponse<MyAccountResponse> getMyAccount() {
        Long customerId = SessionUtil.getCurrentCustomerId();
        return ApiResponse.ok(accountService.getMyAccount(customerId));
    }

    @GetMapping("/loans")
    public ApiResponse<List<LoanSummaryResponse>> getMyLoans() {
        Long customerId = SessionUtil.getCurrentCustomerId();
        return ApiResponse.ok(accountService.getMyLoans(customerId));
    }

    @GetMapping("/loans/{loanAccountNo}")
    public ApiResponse<LoanDetailResponse> getLoanDetail(@PathVariable String loanAccountNo) {
        Long customerId = SessionUtil.getCurrentCustomerId();
        return ApiResponse.ok(accountService.getLoanDetail(customerId, loanAccountNo));
    }
}
