package com.busanbank.loan.global.crypto;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CryptoProperties.class)
@RequiredArgsConstructor
public class CryptoConfig {

    private final CryptoProperties cryptoProperties;

    /**
     * 애플리케이션 구동 시점에 암호화 키를 홀더에 주입한다.
     * JPA Converter보다 먼저 실행되어야 하므로 @PostConstruct 사용.
     */
    @PostConstruct
    public void initEncryptionKey() {
        EncryptionKeyHolder.init(cryptoProperties.getSecretKey());
    }
}
