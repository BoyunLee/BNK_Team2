package com.example.busanbank_loan.auth.dto;

import com.example.busanbank_loan.customer.entity.Customer;

public record LoginResponse(
        CustomerInfo customer,
        String accountNo
) {
    public static LoginResponse of(Customer customer, String accountNo) {
        return new LoginResponse(CustomerInfo.from(customer), accountNo);
    }
}
