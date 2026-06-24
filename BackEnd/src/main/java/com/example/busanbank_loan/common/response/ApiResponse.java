package com.example.busanbank_loan.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * 공통 응답 형식: { success, code, message, data }
 * data 가 null 이면 직렬화에서 제외된다.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, ResultCode.SUCCESS.getCode(), message, data);
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, ResultCode.SUCCESS.getCode(), message, null);
    }

    public static ApiResponse<Void> error(ResultCode resultCode) {
        return new ApiResponse<>(false, resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static ApiResponse<Void> error(ResultCode resultCode, String message) {
        return new ApiResponse<>(false, resultCode.getCode(), message, null);
    }
}
