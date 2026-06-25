package com.busanbank.loan.domain.chat.dto.response;

import java.util.List;

/**
 * 챗봇 응답.
 * fallback=true 인 경우 관련 상품을 찾지 못해 상담사 연결을 안내하는 케이스.
 */
public record ChatResponse(
        String sessionId,
        String answer,
        List<String> referencedProducts,
        boolean fallback
) {}
