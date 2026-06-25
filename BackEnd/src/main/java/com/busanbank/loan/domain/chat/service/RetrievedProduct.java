package com.busanbank.loan.domain.chat.service;

/**
 * 벡터 검색으로 찾은 상품 한 건.
 *
 * @param productCode 상품 코드(mkpd_cd)
 * @param productName 상품명
 * @param score       코사인 유사도 점수(클수록 유사)
 * @param text        임베딩 원문(상품 설명 본문) — 프롬프트 컨텍스트로 주입
 */
public record RetrievedProduct(
        String productCode,
        String productName,
        double score,
        String text
) {}
