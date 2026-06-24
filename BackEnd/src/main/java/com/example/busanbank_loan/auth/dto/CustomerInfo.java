package com.example.busanbank_loan.auth.dto;

import com.example.busanbank_loan.common.util.MaskingUtil;
import com.example.busanbank_loan.customer.entity.Customer;

import java.time.LocalDate;

/**
 * 로그인 응답용 고객 정보. {@code name/phoneNo/address/email} 은 마스킹된 값이다.
 */
public record CustomerInfo(
        Long customerId,
        String name,
        String phoneNo,
        LocalDate birthDate,
        String address,
        String email,
        String emailVerifiedYn,
        String status,
        String role
) {
    public static CustomerInfo from(Customer customer) {
        return new CustomerInfo(
                customer.getId(),
                MaskingUtil.maskName(customer.getName()),
                MaskingUtil.maskPhone(customer.getPhoneNo()),
                customer.getBirthDate(),
                MaskingUtil.maskAddress(customer.getAddress()),
                MaskingUtil.maskEmail(customer.getEmail()),
                customer.isEmailVerified() ? "Y" : "N",
                customer.getStatus().name(),
                customer.getRole().name());
    }
}
