package com.busanbank.loan.domain.product.controller;

import com.busanbank.loan.domain.product.dto.request.CreateProductRequest;
import com.busanbank.loan.domain.product.dto.request.UpdateProductRequest;
import com.busanbank.loan.domain.product.dto.response.ProductDetailResponse;
import com.busanbank.loan.domain.product.dto.response.ProductResponse;
import com.busanbank.loan.domain.product.dto.response.ProductStatusResponse;
import com.busanbank.loan.domain.product.entity.ProductDescription;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.domain.product.repository.ProductDescriptionRepository;
import com.busanbank.loan.domain.product.service.AiSummaryService;
import com.busanbank.loan.domain.product.service.ProductService;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import com.busanbank.loan.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final AiSummaryService aiSummaryService;
    private final LoanProductRepository loanProductRepository;
    private final ProductDescriptionRepository productDescriptionRepository;

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.ok(productService.getAllProducts());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductDetailResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.created(productService.createProduct(request), "상품이 등록되었습니다.");
    }

    @PutMapping("/{productId}")
    public ApiResponse<ProductDetailResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        return ApiResponse.ok(productService.updateProduct(productId, request), "상품 정보가 수정되었습니다.");
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<ProductStatusResponse> deleteProduct(@PathVariable Long productId) {
        return ApiResponse.ok(productService.deleteProduct(productId), "상품이 판매 중지되었습니다.");
    }

    /**
     * AI 요약 생성/갱신
     * - body에 "summary" 키가 있으면 해당 텍스트로 수동 저장
     * - body가 비어 있거나 "summary"가 null이면 Claude API로 자동 생성
     */
    @PostMapping("/{productId}/ai-summary")
    public ApiResponse<ProductDetailResponse.DescriptionDto> upsertAiSummary(
            @PathVariable Long productId,
            @RequestBody(required = false) Map<String, String> body) {

        String manualSummary = (body != null) ? body.get("summary") : null;

        if (manualSummary != null && !manualSummary.isBlank()) {
            return ApiResponse.ok(
                    productService.upsertAiSummary(productId, manualSummary),
                    "AI 요약이 수동으로 갱신되었습니다."
            );
        }

        // Claude로 자동 생성
        String productName = loanProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND))
                .getProductName();

        List<ProductDescription> descs = productDescriptionRepository
                .findAllByProductIdOrderBySortOrderAsc(productId)
                .stream()
                .filter(d -> !"AI_SUMMARY".equals(d.getAttrKey()))
                .toList();

        String generated = aiSummaryService.generateSummary(productName, descs)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                        "AI 요약 생성에 실패했습니다. ANTHROPIC_API_KEY가 설정되어 있는지 확인하세요."));

        return ApiResponse.ok(
                productService.upsertAiSummary(productId, generated),
                "AI 요약이 자동 생성되었습니다."
        );
    }
}
