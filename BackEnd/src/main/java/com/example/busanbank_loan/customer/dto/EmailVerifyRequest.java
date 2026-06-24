package com.example.busanbank_loan.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailVerifyRequest(
        @NotBlank @Email String email,
        @NotBlank String code
) {
}
