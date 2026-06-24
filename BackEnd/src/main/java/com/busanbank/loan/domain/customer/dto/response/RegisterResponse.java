package com.busanbank.loan.domain.customer.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {

    private final Long customerId;
    private final String accountNo;
}
