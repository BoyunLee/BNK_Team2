package com.example.busanbank_loan.common.exception;

import com.example.busanbank_loan.common.response.ApiResponse;
import com.example.busanbank_loan.common.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        ResultCode rc = e.getResultCode();
        return ResponseEntity.status(rc.getHttpStatus())
                .body(ApiResponse.error(rc, e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidation(Exception e) {
        log.debug("입력값 검증 실패: {}", e.getMessage());
        ResultCode rc = ResultCode.C001;
        return ResponseEntity.status(rc.getHttpStatus()).body(ApiResponse.error(rc));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        log.error("처리되지 않은 예외", e);
        ResultCode rc = ResultCode.C003;
        return ResponseEntity.status(rc.getHttpStatus()).body(ApiResponse.error(rc));
    }
}
