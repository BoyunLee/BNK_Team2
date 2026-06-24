package com.example.busanbank_loan.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * API-SPEC.md 의 공통 결과/에러 코드 정의.
 * enum 이름이 곧 응답 {@code code} 값이 된다.
 */
@Getter
public enum ResultCode {

    SUCCESS(HttpStatus.OK, "요청이 처리되었습니다."),

    // 공통
    C001(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다"),
    C002(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다"),
    C003(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),
    C004(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    C005(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),

    // 인증/회원
    AUTH001(HttpStatus.CONFLICT, "이미 가입된 이메일입니다"),
    AUTH002(HttpStatus.BAD_REQUEST, "인증 코드가 유효하지 않거나 만료되었습니다"),
    AUTH003(HttpStatus.BAD_REQUEST, "이메일 인증을 먼저 완료해주세요"),
    AUTH004(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다"),
    AUTH005(HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다. 다시 로그인해주세요"),

    // 고객/계좌
    CU001(HttpStatus.NOT_FOUND, "고객 정보를 찾을 수 없습니다"),
    CU002(HttpStatus.NOT_FOUND, "계좌 정보를 찾을 수 없습니다"),
    CU003(HttpStatus.FORBIDDEN, "비활성 상태의 고객입니다"),

    // 상품
    PRODUCT001(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"),
    PRODUCT002(HttpStatus.BAD_REQUEST, "판매 중이 아닌 상품입니다"),
    PRODUCT003(HttpStatus.NOT_FOUND, "약관 정보를 찾을 수 없습니다"),

    // 대출
    LOAN001(HttpStatus.NOT_FOUND, "대출 신청서를 찾을 수 없습니다"),
    LOAN002(HttpStatus.CONFLICT, "이미 진행 중인 대출 신청이 있습니다"),
    LOAN003(HttpStatus.BAD_REQUEST, "만료된 대출 신청서입니다"),
    LOAN004(HttpStatus.BAD_REQUEST, "현재 신청 단계에서 수행할 수 없는 요청입니다"),
    LOAN005(HttpStatus.BAD_REQUEST, "서류 열람 후 동의할 수 있습니다"),
    LOAN006(HttpStatus.INTERNAL_SERVER_ERROR, "대출 실행에 실패했습니다"),
    LOAN007(HttpStatus.BAD_REQUEST, "신청 금액이 승인 한도를 초과합니다"),
    LOAN008(HttpStatus.NOT_FOUND, "대출 심사 결과를 찾을 수 없습니다"),
    LOAN009(HttpStatus.BAD_REQUEST, "대출 심사가 거절되었습니다"),

    // 인증수단(비밀번호/전자서명)
    VERIFY001(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다"),
    VERIFY002(HttpStatus.BAD_REQUEST, "전자서명 인증에 실패했습니다"),
    VERIFY003(HttpStatus.BAD_REQUEST, "전자서명 토큰이 유효하지 않습니다"),
    VERIFY004(HttpStatus.CONFLICT, "이미 완료된 인증입니다");

    private final HttpStatus httpStatus;
    private final String message;

    ResultCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String getCode() {
        return name();
    }
}
