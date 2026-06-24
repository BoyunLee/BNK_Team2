package com.busanbank.loan.domain.product.service;

import com.busanbank.loan.domain.product.dto.request.CreateProductRequest;
import com.busanbank.loan.domain.product.dto.request.UpdateProductRequest;
import com.busanbank.loan.domain.product.dto.response.ProductDetailResponse;
import com.busanbank.loan.domain.product.dto.response.ProductResponse;
import com.busanbank.loan.domain.product.dto.response.ProductStatusResponse;
import com.busanbank.loan.domain.product.dto.response.TermsResponse;
import com.busanbank.loan.domain.product.entity.*;
import com.busanbank.loan.domain.product.repository.*;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final LoanProductRepository loanProductRepository;
    private final ProductDescriptionRepository productDescriptionRepository;
    private final ProductPreferentialRateRepository productPreferentialRateRepository;
    private final ProductTermsBaseRepository productTermsBaseRepository;
    private final ProductTermsHistoryRepository productTermsHistoryRepository;

    // ──────────────── READ ────────────────

    @Transactional(readOnly = true)
    @Cacheable("products")
    public List<ProductResponse> getProductsOnSale() {
        return loanProductRepository.findAllByStatus("SALE").stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return loanProductRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable("productDetail")
    public ProductDetailResponse getProductDetail(Long productId) {
        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        List<ProductDescription> descriptions =
                productDescriptionRepository.findAllByProductIdOrderBySortOrderAsc(productId);
        List<ProductPreferentialRate> preferentialRates =
                productPreferentialRateRepository.findAllByProductId(productId);

        return ProductDetailResponse.from(product, descriptions, preferentialRates);
    }

    @Transactional(readOnly = true)
    public List<ProductDetailResponse.DescriptionDto> getProductDescriptions(Long productId) {
        loanProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        return productDescriptionRepository.findAllByProductIdOrderBySortOrderAsc(productId).stream()
                .map(d -> new ProductDetailResponse.DescriptionDto(d.getAttrKey(), d.getAttrValue(), d.getSortOrder()))
                .toList();
    }

    @Transactional(readOnly = true)
    public TermsResponse getLatestTerms(Long productId, String termsType) {
        ProductTermsBase base = productTermsBaseRepository
                .findByProductIdAndTermsTypeAndActiveYn(productId, termsType, "Y")
                .orElseThrow(() -> new BusinessException(ErrorCode.TERMS_NOT_FOUND));

        ProductTermsHistory history = productTermsHistoryRepository
                .findTopByTermsIdOrderByTermsSeqDesc(base.getTermsId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TERMS_NOT_FOUND));

        return new TermsResponse(base.getTermsId(), history.getTermsSeq(), base.getTermsType(), history.getTermsPath());
    }

    // ──────────────── WRITE ────────────────

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "productDetail", allEntries = true)
    })
    public ProductDetailResponse createProduct(CreateProductRequest request) {
        LoanProduct product = loanProductRepository.save(
                LoanProduct.builder()
                        .productName(request.productName())
                        .baseRate(request.baseRate())
                        .loanPeriod(request.loanPeriod())
                        .status(request.status())
                        .build()
        );

        Long productId = product.getProductId();

        if (request.descriptions() != null) {
            List<ProductDescription> descriptions = request.descriptions().stream()
                    .map(d -> ProductDescription.builder()
                            .productId(productId)
                            .attrKey(d.attrKey())
                            .attrValue(d.attrValue())
                            .sortOrder(d.sortOrder())
                            .build())
                    .toList();
            productDescriptionRepository.saveAll(descriptions);
        }

        if (request.preferentialRates() != null) {
            List<ProductPreferentialRate> rates = request.preferentialRates().stream()
                    .map(r -> ProductPreferentialRate.builder()
                            .productId(productId)
                            .conditionCode(r.conditionCode())
                            .conditionName(r.conditionName())
                            .rateValue(r.rateValue())
                            .description(r.description())
                            .build())
                    .toList();
            productPreferentialRateRepository.saveAll(rates);
        }

        if (request.terms() != null) {
            for (CreateProductRequest.TermsItem item : request.terms()) {
                ProductTermsBase base = productTermsBaseRepository.save(
                        ProductTermsBase.builder()
                                .productId(productId)
                                .termsType(item.termsType())
                                .termsPath(item.termsPath())
                                .activeYn("Y")
                                .build()
                );
                productTermsHistoryRepository.save(
                        ProductTermsHistory.builder()
                                .termsId(base.getTermsId())
                                .termsSeq(1)
                                .termsPath(item.termsPath())
                                .build()
                );
            }
        }

        List<ProductDescription> savedDescriptions =
                productDescriptionRepository.findAllByProductIdOrderBySortOrderAsc(productId);
        List<ProductPreferentialRate> savedRates =
                productPreferentialRateRepository.findAllByProductId(productId);

        return ProductDetailResponse.from(product, savedDescriptions, savedRates);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "productDetail", allEntries = true)
    })
    public ProductDetailResponse updateProduct(Long productId, UpdateProductRequest request) {
        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.update(request.productName(), request.baseRate(), request.loanPeriod(), request.status());

        productDescriptionRepository.deleteAllByProductId(productId);
        productPreferentialRateRepository.deleteAllByProductId(productId);

        if (request.descriptions() != null) {
            List<ProductDescription> descriptions = request.descriptions().stream()
                    .map(d -> ProductDescription.builder()
                            .productId(productId)
                            .attrKey(d.attrKey())
                            .attrValue(d.attrValue())
                            .sortOrder(d.sortOrder())
                            .build())
                    .toList();
            productDescriptionRepository.saveAll(descriptions);
        }

        if (request.preferentialRates() != null) {
            List<ProductPreferentialRate> rates = request.preferentialRates().stream()
                    .map(r -> ProductPreferentialRate.builder()
                            .productId(productId)
                            .conditionCode(r.conditionCode())
                            .conditionName(r.conditionName())
                            .rateValue(r.rateValue())
                            .description(r.description())
                            .build())
                    .toList();
            productPreferentialRateRepository.saveAll(rates);
        }

        if (request.terms() != null) {
            for (CreateProductRequest.TermsItem item : request.terms()) {
                ProductTermsBase base = productTermsBaseRepository
                        .findByProductIdAndTermsTypeAndActiveYn(productId, item.termsType(), "Y")
                        .orElseGet(() -> productTermsBaseRepository.save(
                                ProductTermsBase.builder()
                                        .productId(productId)
                                        .termsType(item.termsType())
                                        .termsPath(item.termsPath())
                                        .activeYn("Y")
                                        .build()
                        ));

                int nextSeq = productTermsHistoryRepository
                        .findTopByTermsIdOrderByTermsSeqDesc(base.getTermsId())
                        .map(h -> h.getTermsSeq() + 1)
                        .orElse(1);

                productTermsHistoryRepository.save(
                        ProductTermsHistory.builder()
                                .termsId(base.getTermsId())
                                .termsSeq(nextSeq)
                                .termsPath(item.termsPath())
                                .build()
                );
            }
        }

        List<ProductDescription> savedDescriptions =
                productDescriptionRepository.findAllByProductIdOrderBySortOrderAsc(productId);
        List<ProductPreferentialRate> savedRates =
                productPreferentialRateRepository.findAllByProductId(productId);

        return ProductDetailResponse.from(product, savedDescriptions, savedRates);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "productDetail", allEntries = true)
    })
    public ProductStatusResponse deleteProduct(Long productId) {
        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.discontinue();
        return new ProductStatusResponse(product.getProductId(), product.getStatus());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "productDetail", allEntries = true)
    })
    public ProductDetailResponse.DescriptionDto upsertAiSummary(Long productId, String summary) {
        loanProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductDescription desc = productDescriptionRepository
                .findByProductIdAndAttrKey(productId, "AI_SUMMARY")
                .orElseGet(() -> productDescriptionRepository.save(
                        ProductDescription.builder()
                                .productId(productId)
                                .attrKey("AI_SUMMARY")
                                .attrValue(summary)
                                .sortOrder(99)
                                .build()
                ));

        desc.updateAttrValue(summary);

        return new ProductDetailResponse.DescriptionDto(desc.getAttrKey(), desc.getAttrValue(), desc.getSortOrder());
    }
}
