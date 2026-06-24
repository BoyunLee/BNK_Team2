package com.example.busanbank_loan.auth.controller;

import com.example.busanbank_loan.auth.dto.LoginRequest;
import com.example.busanbank_loan.auth.dto.LoginResponse;
import com.example.busanbank_loan.auth.service.AuthService;
import com.example.busanbank_loan.auth.service.LoginResult;
import com.example.busanbank_loan.common.exception.BusinessException;
import com.example.busanbank_loan.common.response.ApiResponse;
import com.example.busanbank_loan.common.response.ResultCode;
import com.example.busanbank_loan.common.security.CustomerPrincipal;
import com.example.busanbank_loan.common.security.SessionConst;
import com.example.busanbank_loan.customer.dto.EmailSendRequest;
import com.example.busanbank_loan.customer.dto.EmailVerifyRequest;
import com.example.busanbank_loan.customer.dto.RegisterRequest;
import com.example.busanbank_loan.customer.dto.RegisterResponse;
import com.example.busanbank_loan.customer.service.CustomerService;
import com.example.busanbank_loan.customer.service.EmailVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailVerificationService emailVerificationService;
    private final CustomerService customerService;
    private final AuthService authService;
    private final SecurityContextRepository securityContextRepository;

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    /** 1.1 이메일 인증 코드 발송 */
    @PostMapping("/email/send")
    public ApiResponse<Void> sendEmailCode(@Valid @RequestBody EmailSendRequest request) {
        emailVerificationService.sendCode(request.email());
        return ApiResponse.success("인증 코드가 발송되었습니다.");
    }

    /** 1.2 이메일 인증 코드 확인 */
    @PostMapping("/email/verify")
    public ApiResponse<Void> verifyEmailCode(@Valid @RequestBody EmailVerifyRequest request) {
        emailVerificationService.verifyCode(request.email(), request.code());
        return ApiResponse.success("이메일 인증이 완료되었습니다.");
    }

    /** 1.3 회원가입 */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = customerService.register(request);
        return ApiResponse.success("회원가입이 완료되었습니다.", response);
    }

    /** 1.4 로그인 */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                            HttpServletRequest httpRequest,
                                            HttpServletResponse httpResponse) {
        LoginResult result = authService.login(request);
        issueSession(result, httpRequest, httpResponse);
        return ApiResponse.success("로그인되었습니다.",
                LoginResponse.of(result.customer(), result.accountNo()));
    }

    /** 1.5 로그아웃 */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute(SessionConst.SESSION_CUSTOMER_ID) == null) {
            throw new BusinessException(ResultCode.AUTH005);
        }
        session.invalidate();
        securityContextHolderStrategy.clearContext();
        return ApiResponse.success("로그아웃되었습니다.");
    }

    private void issueSession(LoginResult result, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        CustomerPrincipal principal = new CustomerPrincipal(
                result.customer().getId(), result.customer().getRole().name());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + principal.role())));

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        httpRequest.getSession(true).setAttribute(SessionConst.SESSION_CUSTOMER_ID, principal.customerId());
    }
}
