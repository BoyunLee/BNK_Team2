package com.busanbank.loan.global.crypto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "crypto")
public class CryptoProperties {

    /**
     * AES-256 키 (Base64 인코딩된 32바이트).
     * 생성 명령: openssl rand -base64 32
     * 환경변수 CRYPTO_SECRET_KEY 또는 application-{profile}.yml에 설정.
     */
    private String secretKey;
}
