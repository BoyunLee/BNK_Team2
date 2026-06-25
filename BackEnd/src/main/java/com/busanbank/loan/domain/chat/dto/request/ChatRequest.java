package com.busanbank.loan.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 챗봇 질의 요청.
 * sessionId가 비어 있으면 서버에서 새 세션을 발급한다.
 */
public record ChatRequest(

        @Size(max = 100, message = "sessionId 길이가 올바르지 않습니다.")
        String sessionId,

        @NotBlank(message = "질문 내용을 입력해 주세요.")
        @Size(max = 1000, message = "질문은 1000자 이내로 입력해 주세요.")
        String message
) {}
