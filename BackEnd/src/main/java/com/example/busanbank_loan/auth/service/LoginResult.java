package com.example.busanbank_loan.auth.service;

import com.example.busanbank_loan.customer.entity.Customer;

/**
 * 로그인 검증 결과(서비스 → 컨트롤러 전달용). 세션/마스킹 처리는 컨트롤러에서 수행한다.
 */
public record LoginResult(
        Customer customer,
        String accountNo
) {
}
