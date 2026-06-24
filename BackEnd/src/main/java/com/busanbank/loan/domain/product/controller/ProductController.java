package com.busanbank.loan.domain.product.controller;

import com.busanbank.loan.domain.product.dto.response.ProductDetailResponse;
import com.busanbank.loan.domain.product.dto.response.ProductResponse;
import com.busanbank.loan.domain.product.service.ProductService;
import com.busanbank.loan.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductResponse>> getProductsOnSale() {
        return ApiResponse.ok(productService.getProductsOnSale());
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable Long productId) {
        return ApiResponse.ok(productService.getProductDetail(productId));
    }

    @GetMapping("/{productId}/descriptions")
    public ApiResponse<List<ProductDetailResponse.DescriptionDto>> getProductDescriptions(
            @PathVariable Long productId) {
        return ApiResponse.ok(productService.getProductDescriptions(productId));
    }
}
