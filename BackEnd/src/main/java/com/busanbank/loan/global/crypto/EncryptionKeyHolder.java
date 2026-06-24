package com.busanbank.loan.global.crypto;

import java.util.Base64;

/**
 * JPA Converter는 Spring 컨텍스트 외부에서 인스턴스화되므로
 * 암호화 키를 static으로 보관하는 홀더를 사용한다.
 * CryptoConfig의 @PostConstruct에서 단 한 번 초기화된다.
 */
public final class EncryptionKeyHolder {

    private static byte[] secretKey;

    private EncryptionKeyHolder() {}

    static void init(String base64Key) {
        if (secretKey != null) return; // 중복 초기화 방지
        byte[] decoded = Base64.getDecoder().decode(base64Key);
        if (decoded.length != 32) {
            throw new IllegalArgumentException(
                    "AES-256 키는 32바이트(256비트)여야 합니다. 현재: " + decoded.length + "바이트");
        }
        secretKey = decoded;
    }

    public static byte[] get() {
        if (secretKey == null) {
            throw new IllegalStateException("암호화 키가 초기화되지 않았습니다. CryptoConfig를 확인하세요.");
        }
        return secretKey;
    }
}
