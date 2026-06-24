package com.example.busanbank_loan.common.security;

/**
 * 세션(SecurityContext)에 저장되는 인증 주체.
 */
public record CustomerPrincipal(Long customerId, String role) {
}
