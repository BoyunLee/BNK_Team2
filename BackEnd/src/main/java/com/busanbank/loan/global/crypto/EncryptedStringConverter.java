package com.busanbank.loan.global.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Entity 필드에 @Convert(converter = EncryptedStringConverter.class) 를 붙이면
 * DB 저장 시 자동 암호화, 조회 시 자동 복호화된다.
 *
 * 적용 대상 (개인정보보호법상 암호화 의무):
 *   - CUSTOMER.name (이름)
 *   - CUSTOMER.phone_no (전화번호)
 *   - CUSTOMER.birth_date (생년월일) — DATE 타입이지만 String 변환 후 저장 시 적용
 *   - CUSTOMER.address (주소)
 *   - CUSTOMER.email (이메일)
 *   - ACCOUNT.account_no (계좌번호)
 */
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return AesEncryptor.encrypt(EncryptionKeyHolder.get(), attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return AesEncryptor.decrypt(EncryptionKeyHolder.get(), dbData);
    }
}
