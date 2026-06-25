package com.busanbank.loan.domain.chat.controller;

import com.busanbank.loan.domain.chat.dto.request.ChatRequest;
import com.busanbank.loan.domain.chat.dto.response.ChatResponse;
import com.busanbank.loan.domain.chat.service.ChatService;
import com.busanbank.loan.global.response.ApiResponse;
import com.busanbank.loan.global.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 대출 상품 상담 챗봇 API. 비로그인 사용자도 이용 가능(/api/v1/** 인터셉터 대상 아님).
 */
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/api/chat")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request,
                                          HttpServletRequest httpRequest) {
        ChatResponse response = chatService.chat(request, resolveCustomerId(httpRequest));
        return ApiResponse.ok(response);
    }

    /** 로그인 세션이 있으면 customerId를 채우고, 없으면 null(비로그인 상담). */
    private Long resolveCustomerId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Long) session.getAttribute(SessionUtil.SESSION_CUSTOMER_ID);
    }
}
