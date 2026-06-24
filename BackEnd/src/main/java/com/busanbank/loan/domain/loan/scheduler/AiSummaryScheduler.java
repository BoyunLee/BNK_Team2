package com.busanbank.loan.domain.loan.scheduler;

import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.entity.ProductDescription;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.domain.product.repository.ProductDescriptionRepository;
import com.busanbank.loan.domain.product.service.AiSummaryService;
import com.busanbank.loan.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiSummaryScheduler {

    private final LoanProductRepository loanProductRepository;
    private final ProductDescriptionRepository productDescriptionRepository;
    private final ProductService productService;
    private final AiSummaryService aiSummaryService;

    @Scheduled(cron = "0 0 2 * * *")
    public void generateAiSummaries() {
        List<LoanProduct> products = loanProductRepository.findAll();
        log.info("AI 상품 요약 스케줄러 시작 — 총 {}개 상품", products.size());

        int success = 0;
        int skipped = 0;

        for (LoanProduct product : products) {
            try {
                List<ProductDescription> descs = productDescriptionRepository
                        .findAllByProductIdOrderBySortOrderAsc(product.getProductId())
                        .stream()
                        .filter(d -> !"AI_SUMMARY".equals(d.getAttrKey()))
                        .toList();

                aiSummaryService.generateSummary(product.getProductName(), descs)
                        .ifPresentOrElse(
                                summary -> {
                                    productService.upsertAiSummary(product.getProductId(), summary);
                                    log.info("AI 요약 저장 완료: productId={}", product.getProductId());
                                },
                                () -> log.debug("AI 요약 건너뜀: productId={}", product.getProductId())
                        );

                success++;
            } catch (Exception e) {
                skipped++;
                log.error("AI 요약 실패: productId={}", product.getProductId(), e);
            }
        }

        log.info("AI 상품 요약 스케줄러 완료 — 성공: {}, 실패: {}", success, skipped);
    }
}
