package com.busanbank.loan.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ────── Common ──────
    INVALID_INPUT("C001", "입력값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("C002", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("C003", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED("C004", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("C005", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // ────── Auth ──────
    DUPLICATE_EMAIL("AUTH001", "이미 가입된 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_EMAIL_CODE("AUTH002", "인증 코드가 유효하지 않거나 만료되었습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED("AUTH003", "이메일 인증을 먼저 완료해주세요.", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("AUTH004", "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    SESSION_EXPIRED("AUTH005", "세션이 만료되었습니다. 다시 로그인해주세요.", HttpStatus.UNAUTHORIZED),

    // ────── Customer ──────
    CUSTOMER_NOT_FOUND("CU001", "고객 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ACCOUNT_NOT_FOUND("CU002", "계좌 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INACTIVE_CUSTOMER("CU003", "비활성 상태의 고객입니다.", HttpStatus.FORBIDDEN),

    // ────── Product ──────
    PRODUCT_NOT_FOUND("PRODUCT001", "상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_ON_SALE("PRODUCT002", "판매 중이 아닌 상품입니다.", HttpStatus.BAD_REQUEST),
    TERMS_NOT_FOUND("PRODUCT003", "약관 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ────── Loan Application ──────
    LOAN_NOT_FOUND("LOAN001", "대출 신청서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    LOAN_ALREADY_IN_PROGRESS("LOAN002", "이미 진행 중인 대출 신청이 있습니다.", HttpStatus.CONFLICT),
    LOAN_EXPIRED("LOAN003", "만료된 대출 신청서입니다.", HttpStatus.BAD_REQUEST),
    INVALID_STEP("LOAN004", "현재 신청 단계에서 수행할 수 없는 요청입니다.", HttpStatus.BAD_REQUEST),
    DOCUMENT_NOT_VIEWED("LOAN005", "서류 열람 후 동의할 수 있습니다.", HttpStatus.BAD_REQUEST),
    LOAN_EXECUTE_FAILED("LOAN006", "대출 실행에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    LOAN_AMOUNT_EXCEEDED("LOAN007", "신청 금액이 승인 한도를 초과합니다.", HttpStatus.BAD_REQUEST),
    SCREENING_NOT_FOUND("LOAN008", "대출 심사 결과를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SCREENING_REJECTED("LOAN009", "대출 심사가 거절되었습니다.", HttpStatus.BAD_REQUEST),

    // ────── Verification ──────
    INVALID_PASSWORD("VERIFY001", "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    SIGNATURE_FAILED("VERIFY002", "전자서명 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    SIGNATURE_TOKEN_INVALID("VERIFY003", "전자서명 토큰이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_VERIFIED("VERIFY004", "이미 완료된 인증입니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
