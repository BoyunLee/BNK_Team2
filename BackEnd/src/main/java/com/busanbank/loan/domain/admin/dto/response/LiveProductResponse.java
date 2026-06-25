package com.busanbank.loan.domain.admin.dto.response;

import com.busanbank.loan.domain.admin.dto.ProductSnapshotDto;

/** 프론트 lib/admin.ts LiveProduct 와 동일 형태. 현재 라이브(AS-IS) 상품. */
public record LiveProductResponse(
        Long productId,
        ProductSnapshotDto snapshot,
        String status,
        int version,
        String updatedAt
) {
}
