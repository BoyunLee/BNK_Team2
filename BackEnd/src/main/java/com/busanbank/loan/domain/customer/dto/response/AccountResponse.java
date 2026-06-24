package com.busanbank.loan.domain.customer.dto.response;

import com.busanbank.loan.domain.customer.entity.Account;
import com.busanbank.loan.global.util.MaskingUtil;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AccountResponse {

    private final String accountNo;
    private final BigDecimal balance;
    private final String status;

    public static AccountResponse from(Account account) {
        return AccountResponse.builder()
                .accountNo(MaskingUtil.maskAccount(account.getAccountNo()))
                .balance(account.getBalance())
                .status(account.getStatus())
                .build();
    }
}
