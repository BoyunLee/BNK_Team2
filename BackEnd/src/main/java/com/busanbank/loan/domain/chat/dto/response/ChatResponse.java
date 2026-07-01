package com.busanbank.loan.domain.chat.dto.response;

import java.util.List;

/**
 * 챗봇 응답.
 * fallback=true 인 경우 매칭되는 상품 없이 금융 일반 지식으로 답변한 케이스
 * (referencedProducts 는 비어 있음). fallback=false 는 상품 정보에 근거한 답변.
 */
public record ChatResponse(
        String sessionId,
        String answer,
        List<String> referencedProducts,
        boolean fallback
) {}
