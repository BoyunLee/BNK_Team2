package com.example.busanbank_loan.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청. {@code birthDate} 는 String 으로 수신 후 서비스에서 {@code LocalDate.parse} 한다.
 */
public record RegisterRequest(
        @NotBlank String name,
        @NotBlank String phoneNo,
        @NotBlank String birthDate,
        @NotBlank String address,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 6) String simplePassword,
        @NotBlank @Size(min = 4, max = 4) String accountPassword,
        @NotBlank @Size(min = 6, max = 6) String signaturePassword
) {
}
