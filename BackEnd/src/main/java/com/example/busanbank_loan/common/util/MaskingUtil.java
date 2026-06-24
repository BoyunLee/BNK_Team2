package com.example.busanbank_loan.common.util;

/**
 * 개인정보 마스킹 유틸. 로그인 응답 등 화면 노출용 가공에 사용한다.
 */
public final class MaskingUtil {

    private MaskingUtil() {
    }

    /** 홍길동 -> 홍** */
    public static String maskName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        if (name.length() == 1) {
            return name;
        }
        return name.charAt(0) + "*".repeat(name.length() - 1);
    }

    /** 010-1234-5678 -> 010-****-5678 */
    public static String maskPhone(String phoneNo) {
        if (phoneNo == null) {
            return null;
        }
        String[] parts = phoneNo.split("-");
        if (parts.length == 3) {
            return parts[0] + "-" + "*".repeat(parts[1].length()) + "-" + parts[2];
        }
        return phoneNo;
    }

    /** test@email.com -> te**@email.com */
    public static String maskEmail(String email) {
        if (email == null) {
            return null;
        }
        int at = email.indexOf('@');
        if (at <= 0) {
            return email;
        }
        String local = email.substring(0, at);
        String domain = email.substring(at);
        String visible = local.length() <= 2 ? local.substring(0, 1) : local.substring(0, 2);
        int maskLen = Math.max(local.length() - visible.length(), 1);
        return visible + "*".repeat(maskLen) + domain;
    }

    /** 부산시 중구 중앙동 -> 부산시 중구 *** (앞 2개 토큰 유지) */
    public static String maskAddress(String address) {
        if (address == null || address.isBlank()) {
            return address;
        }
        String[] tokens = address.trim().split("\\s+");
        if (tokens.length <= 2) {
            return String.join(" ", tokens) + " ***";
        }
        return tokens[0] + " " + tokens[1] + " ***";
    }
}
