package com.busanbank.loan.global.util;

import java.util.regex.Pattern;

public final class MaskingUtil {

    private MaskingUtil() {}

    // 010-1234-5678 → 010-****-5678
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("(\\d{3})-?(\\d{3,4})-?(\\d{4})");

    // 110-123-456789 → 110-***-***789
    private static final Pattern ACCOUNT_PATTERN =
            Pattern.compile("(\\d{3})-?(\\d{3})-?(\\d{6})");

    // "password", "simplePassword", "accountPassword" 등 JSON 키 → 값 마스킹
    private static final Pattern PASSWORD_JSON_PATTERN =
            Pattern.compile("(\"(?i)(?:password|simplePassword|accountPassword|signaturePassword|simple_password|account_password)\"\\s*:\\s*\")([^\"]+)(\")",
                    Pattern.CASE_INSENSITIVE);

    public static String maskPhone(String phone) {
        if (phone == null) return null;
        return PHONE_PATTERN.matcher(phone).replaceAll("$1-****-$3");
    }

    // 110-123-456789 → 110-***-***789
    public static String maskAccount(String accountNo) {
        if (accountNo == null) return null;
        String digits = accountNo.replaceAll("[^0-9]", "");
        if (digits.length() < 7) return "***";
        String tail = digits.substring(digits.length() - 3);
        return digits.substring(0, 3) + "-***-***" + tail;
    }

    // test@email.com → te**@email.com
    public static String maskEmail(String email) {
        if (email == null) return null;
        int atIdx = email.indexOf('@');
        if (atIdx <= 2) return "***" + email.substring(atIdx);
        String local = email.substring(0, atIdx);
        String domain = email.substring(atIdx);
        String visible = local.substring(0, 2);
        String masked = "*".repeat(local.length() - 2);
        return visible + masked + domain;
    }

    // 부산시 중구 어딘가 → 부산시 중구 ***
    public static String maskAddress(String address) {
        if (address == null) return null;
        int cutoff = Math.min(address.length(), 8); // 앞 8자만 노출
        return address.substring(0, cutoff) + " ***";
    }

    public static String maskName(String name) {
        if (name == null || name.length() <= 1) return name;
        return name.charAt(0) + "*".repeat(name.length() - 1);
    }

    // 카드번호 앞 6자리 + **** + 마지막 4자리
    public static String maskCardNumber(String cardNo) {
        if (cardNo == null || cardNo.length() < 10) return "****";
        String digits = cardNo.replaceAll("[^0-9]", "");
        return digits.substring(0, Math.min(6, digits.length()))
                + "****"
                + digits.substring(Math.max(0, digits.length() - 4));
    }

    /**
     * JSON 문자열에서 패스워드 필드 값을 마스킹한다.
     * 로그 출력 전 JSON 직렬화 결과에 적용.
     */
    public static String maskJsonPasswords(String json) {
        if (json == null) return null;
        return PASSWORD_JSON_PATTERN.matcher(json).replaceAll("$1****$3");
    }

    /**
     * 전화번호, 계좌번호 패턴을 JSON 문자열에서 마스킹한다.
     */
    public static String maskSensitiveJson(String json) {
        if (json == null) return null;
        String masked = maskJsonPasswords(json);
        masked = PHONE_PATTERN.matcher(masked).replaceAll("$1-****-$3");
        return masked;
    }
}
