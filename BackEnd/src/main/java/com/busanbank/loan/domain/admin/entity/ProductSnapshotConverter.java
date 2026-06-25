package com.busanbank.loan.domain.admin.entity;

import com.busanbank.loan.domain.admin.dto.ProductSnapshotDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** ProductSnapshotDto <-> JSON 문자열(컬럼 저장). TO-BE/AS-IS 스냅샷을 JSON 으로 보관. */
@Converter
public class ProductSnapshotConverter implements AttributeConverter<ProductSnapshotDto, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ProductSnapshotDto attribute) {
        if (attribute == null) return null;
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("스냅샷 직렬화 실패", e);
        }
    }

    @Override
    public ProductSnapshotDto convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return MAPPER.readValue(dbData, ProductSnapshotDto.class);
        } catch (Exception e) {
            throw new IllegalStateException("스냅샷 역직렬화 실패", e);
        }
    }
}
