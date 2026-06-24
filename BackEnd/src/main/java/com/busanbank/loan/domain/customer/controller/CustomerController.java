package com.busanbank.loan.domain.customer.controller;

import com.busanbank.loan.domain.customer.dto.request.EmailSendRequest;
import com.busanbank.loan.domain.customer.dto.request.EmailVerifyRequest;
import com.busanbank.loan.domain.customer.dto.request.LoginRequest;
import com.busanbank.loan.domain.customer.dto.request.RegisterRequest;
import com.busanbank.loan.domain.customer.dto.response.LoginResponse;
import com.busanbank.loan.domain.customer.dto.response.RegisterResponse;
import com.busanbank.loan.domain.customer.service.CustomerService;
import com.busanbank.loan.domain.customer.service.EmailService;
import com.busanbank.loan.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final EmailService emailService;

    @PostMapping("/email/send")
    public ApiResponse<Void> sendVerificationCode(@Valid @RequestBody EmailSendRequest req) {
        customerService.checkEmailForSend(req.getEmail());
        emailService.sendVerificationCode(req.getEmail());
        return ApiResponse.ok("인증 코드가 발송되었습니다.");
    }

    @PostMapping("/email/verify")
    public ApiResponse<Void> verifyEmailCode(@Valid @RequestBody EmailVerifyRequest req) {
        emailService.verifyCode(req.getEmail(), req.getCode());
        return ApiResponse.ok("이메일 인증이 완료되었습니다.");
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ApiResponse.created(customerService.register(req), "회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req,
                                            HttpServletRequest request) {
        return ApiResponse.ok(customerService.login(req, request), "로그인되었습니다.");
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        customerService.logout(request);
        return ApiResponse.ok("로그아웃되었습니다.");
    }
}
