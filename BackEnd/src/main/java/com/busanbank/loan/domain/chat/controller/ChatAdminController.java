package com.busanbank.loan.domain.chat.controller;

import com.busanbank.loan.domain.chat.service.ProductIndexService;
import com.busanbank.loan.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 챗봇 벡터 인덱싱 관리 API.
 * /api/v1/** 인터셉터로 보호됨(로그인 필요). 테스트용 전체 재적재 트리거.
 *
 * <p>NOTE: 상품 CRUD 연동 증분 재임베딩은 추후 관리자 API 명세에서 정식화한다.
 */
@RestController
@RequestMapping("/api/v1/admin/chat")
@RequiredArgsConstructor
public class ChatAdminController {

    private final ProductIndexService productIndexService;

    @PostMapping("/reindex")
    public ApiResponse<Map<String, Integer>> reindex() {
        int indexed = productIndexService.reindexAll();
        return ApiResponse.ok(Map.of("indexed", indexed), "상품 벡터 인덱싱이 완료되었습니다.");
    }
}
