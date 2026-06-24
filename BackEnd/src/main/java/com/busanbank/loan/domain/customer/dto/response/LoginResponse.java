package com.busanbank.loan.domain.customer.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private final CustomerResponse customer;
    private final String accountNo;
}
