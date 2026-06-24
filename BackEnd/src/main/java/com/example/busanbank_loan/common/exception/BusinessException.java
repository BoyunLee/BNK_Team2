package com.example.busanbank_loan.common.exception;

import com.example.busanbank_loan.common.response.ResultCode;
import lombok.Getter;

/**
 * 비즈니스 규칙 위반 시 던지는 예외. {@link ResultCode} 를 담아 Global Handler 가 공통 형식으로 변환한다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}
