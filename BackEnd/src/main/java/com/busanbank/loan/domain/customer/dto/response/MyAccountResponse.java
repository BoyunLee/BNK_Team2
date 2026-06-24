package com.busanbank.loan.domain.customer.dto.response;

import com.busanbank.loan.domain.customer.entity.Account;
import com.busanbank.loan.domain.customer.entity.Customer;
import com.busanbank.loan.global.util.MaskingUtil;

import java.math.BigDecimal;

public record MyAccountResponse(
        String accountNo,
        BigDecimal balance,
        String status,
        String customerName
) {
    public static MyAccountResponse of(Account account, Customer customer) {
        return new MyAccountResponse(
                MaskingUtil.maskAccount(account.getAccountNo()),
                account.getBalance(),
                account.getStatus(),
                MaskingUtil.maskName(customer.getName())
        );
    }
}
