package com.busanbank.loan.domain.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNo;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String address;

    @NotBlank
    private String birthDate;  // "yyyy-MM-dd" 형식 문자열로 받아 서비스에서 파싱

    @NotBlank
    private String simplePassword;

    @NotBlank
    private String accountPassword;

    @NotBlank
    private String signaturePassword;
}
