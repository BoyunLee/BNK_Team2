package com.busanbank.loan.global.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES-256-CBC 기반 개인정보 암복호화 유틸리티.
 *
 * [알고리즘 선택 근거]
 * 이메일·전화번호처럼 DB에서 동등 조건(WHERE email = ?) 검색이 필요한 컬럼이 있어
 * 결정적 암호화(Deterministic Encryption)를 채택한다.
 * IV는 키에서 파생(키의 앞 16바이트)하여 동일 평문 → 동일 암호문을 보장한다.
 *
 * [보안 고려사항]
 * - 키는 반드시 환경변수(CRYPTO_SECRET_KEY) 또는 Vault 등 외부 키 저장소에서 주입
 * - 동일 키로 두 개의 다른 값이 같은 암호문을 갖지 않도록 키 교체 주기 관리 필요
 * - 향후 검색 불필요 컬럼(주소 등)은 AES-GCM(랜덤 IV) 별도 컨버터로 전환 가능
 */
public final class AesEncryptor {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private AesEncryptor() {}

    /**
     * 평문을 AES-256-CBC로 암호화하여 Base64 문자열 반환.
     * null 입력 시 null 반환 (nullable 컬럼 지원).
     */
    public static String encrypt(byte[] key, String plainText) {
        if (plainText == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, toKeySpec(key), toIvSpec(key));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new CryptoException("암호화에 실패했습니다.", e);
        }
    }

    /**
     * Base64 암호문을 AES-256-CBC로 복호화하여 평문 반환.
     * null 입력 시 null 반환.
     */
    public static String decrypt(byte[] key, String encryptedText) {
        if (encryptedText == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, toKeySpec(key), toIvSpec(key));
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException("복호화에 실패했습니다.", e);
        }
    }

    private static SecretKeySpec toKeySpec(byte[] key) {
        return new SecretKeySpec(key, "AES");
    }

    // IV = 키의 앞 16바이트 (결정적 암호화용 고정 IV)
    private static IvParameterSpec toIvSpec(byte[] key) {
        return new IvParameterSpec(Arrays.copyOf(key, 16));
    }
}
