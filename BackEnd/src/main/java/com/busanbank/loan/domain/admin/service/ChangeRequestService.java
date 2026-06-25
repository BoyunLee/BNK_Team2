package com.busanbank.loan.domain.admin.service;

import com.busanbank.loan.domain.admin.dto.ProductSnapshotDto;
import com.busanbank.loan.domain.admin.dto.request.ApproveRequest;
import com.busanbank.loan.domain.admin.dto.request.CreateChangeRequest;
import com.busanbank.loan.domain.admin.dto.request.UpdateChangeRequest;
import com.busanbank.loan.domain.admin.dto.response.ChangeRequestResponse;
import com.busanbank.loan.domain.admin.dto.response.LiveProductResponse;
import com.busanbank.loan.domain.admin.entity.AdminUser;
import com.busanbank.loan.domain.admin.entity.ChangeStatus;
import com.busanbank.loan.domain.admin.entity.ProductChangeRequest;
import com.busanbank.loan.domain.admin.repository.ProductChangeRequestRepository;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.entity.ProductDescription;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.domain.product.repository.ProductDescriptionRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeRequestService {

    private static final Logger log = LoggerFactory.getLogger(ChangeRequestService.class);

    private final ProductChangeRequestRepository changeRepo;
    private final AdminUserService adminUserService;
    private final LoanProductRepository productRepo;
    private final ProductDescriptionRepository descRepo;
    private final ProductSnapshotMapper snapshotMapper;
    private final CacheManager cacheManager;

    // ──────────────── 조회 ────────────────

    @Transactional(readOnly = true)
    public List<ChangeRequestResponse> list(ChangeStatus status, Long approverId) {
        List<ProductChangeRequest> list;
        if (status != null && approverId != null) {
            list = changeRepo.findAllByStatusAndApproverIdOrderByIdDesc(status, approverId);
        } else if (status != null) {
            list = changeRepo.findAllByStatusOrderByIdDesc(status);
        } else if (approverId != null) {
            list = changeRepo.findAllByApproverIdOrderByIdDesc(approverId);
        } else {
            list = changeRepo.findAllByOrderByIdDesc();
        }
        return list.stream().map(ChangeRequestResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ChangeRequestResponse get(Long id) {
        return ChangeRequestResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<LiveProductResponse> listLiveProducts() {
        return productRepo.findAll().stream()
                .map(p -> {
                    List<ProductDescription> descs =
                            descRepo.findAllByProductIdOrderBySortOrderAsc(p.getProductId());
                    int deployed = changeRepo.countByProductIdAndStatus(p.getProductId(), ChangeStatus.DEPLOYED);
                    return new LiveProductResponse(
                            p.getProductId(),
                            snapshotMapper.toSnapshot(p, descs),
                            p.getStatus(),
                            1 + deployed,
                            p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null
                    );
                })
                .toList();
    }

    // ──────────────── 작성/상신 ────────────────

    @Transactional
    public ChangeRequestResponse createDraft(Long adminId, CreateChangeRequest r) {
        AdminUser drafter = adminUserService.getById(adminId);
        ProductSnapshotDto asis = (r.productId() != null) ? captureAsis(r.productId()) : null;

        ProductChangeRequest e = ProductChangeRequest.builder()
                .changeType(r.changeType())
                .productId(r.productId())
                .title(r.title())
                .asis(asis)
                .tobe(r.tobe())
                .drafterId(drafter.getId())
                .drafterName(drafter.getName())
                .build();

        return ChangeRequestResponse.from(changeRepo.save(e));
    }

    @Transactional
    public ChangeRequestResponse updateDraft(Long adminId, Long id, UpdateChangeRequest r) {
        ProductChangeRequest e = find(id);
        ProductSnapshotDto asis = (e.getProductId() != null) ? captureAsis(e.getProductId()) : null;
        e.editDraft(r.title(), r.tobe(), asis);
        return ChangeRequestResponse.from(e);
    }

    @Transactional
    public ChangeRequestResponse submit(Long adminId, Long id, Long approverId) {
        ProductChangeRequest e = find(id);
        AdminUser approver = adminUserService.getById(approverId);
        if (!approver.isApprover()) {
            throw new BusinessException(ErrorCode.APPROVER_REQUIRED);
        }
        ProductSnapshotDto asis = (e.getProductId() != null) ? captureAsis(e.getProductId()) : null;
        e.submit(approver.getId(), approver.getName(), asis);
        return ChangeRequestResponse.from(e);
    }

    @Transactional
    public ChangeRequestResponse cancel(Long adminId, Long id) {
        ProductChangeRequest e = find(id);
        e.cancel();
        return ChangeRequestResponse.from(e);
    }

    // ──────────────── 결재(책임자) ────────────────

    @Transactional
    public ChangeRequestResponse approve(Long adminId, Long id, ApproveRequest r) {
        ProductChangeRequest e = find(id);
        ensureAssignedApprover(adminId, e);
        e.approve(parseIso(r.scheduledDeployAt()), r.comment());
        return ChangeRequestResponse.from(e);
    }

    @Transactional
    public ChangeRequestResponse reject(Long adminId, Long id, String comment) {
        ProductChangeRequest e = find(id);
        ensureAssignedApprover(adminId, e);
        e.reject(comment);
        return ChangeRequestResponse.from(e);
    }

    // ──────────────── 형상이행(배포) ────────────────

    /** 예약시각이 도래한 APPROVED 신청서를 라이브에 반영. 반환: 배포 건수. (스케줄러가 호출) */
    @Transactional
    public int runDueDeployments() {
        LocalDateTime now = LocalDateTime.now();
        List<ProductChangeRequest> due = changeRepo.findAllByStatus(ChangeStatus.APPROVED).stream()
                .filter(e -> e.isDueForDeploy(now))
                .toList();
        for (ProductChangeRequest e : due) {
            deployOne(e);
        }
        if (!due.isEmpty()) {
            evictProductCaches();
            log.info("[CHANGE_DEPLOY] {}건 형상이행 완료", due.size());
        }
        return due.size();
    }

    /** 데모용: 예약과 무관하게 지금 즉시 배포. */
    @Transactional
    public ChangeRequestResponse deployNow(Long adminId, Long id) {
        ProductChangeRequest e = find(id);
        e.scheduleNow();
        deployOne(e);
        evictProductCaches();
        return ChangeRequestResponse.from(e);
    }

    private void deployOne(ProductChangeRequest e) {
        ProductSnapshotDto s = e.getTobe();
        switch (e.getChangeType()) {
            case CREATE -> {
                LoanProduct p = productRepo.save(LoanProduct.builder()
                        .productName(s.productName())
                        .category(s.category())
                        .baseRate(parseRate(s.baseRate()))
                        .loanPeriod(s.loanPeriod())
                        .catchphrase(s.catchphrase())
                        .rateMin(s.rateMin())
                        .rateMax(s.rateMax())
                        .status("SALE")
                        .build());
                upsertDescriptions(p.getProductId(), s);
                e.markDeployed(p.getProductId());
            }
            case UPDATE -> {
                LoanProduct p = productRepo.findById(e.getProductId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
                p.applyChange(s.productName(), s.category(), parseRate(s.baseRate()),
                        s.loanPeriod(), s.catchphrase(), s.rateMin(), s.rateMax());
                upsertDescriptions(p.getProductId(), s);
                e.markDeployed(p.getProductId());
            }
            case DISCONTINUE -> {
                LoanProduct p = productRepo.findById(e.getProductId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
                p.discontinue();
                e.markDeployed(p.getProductId());
            }
        }
    }

    // ──────────────── 내부 헬퍼 ────────────────

    private ProductChangeRequest find(Long id) {
        return changeRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANGE_REQUEST_NOT_FOUND));
    }

    private void ensureAssignedApprover(Long adminId, ProductChangeRequest e) {
        AdminUser me = adminUserService.getById(adminId);
        if (!me.isApprover() || !me.getId().equals(e.getApproverId())) {
            throw new BusinessException(ErrorCode.NOT_APPROVER);
        }
    }

    /** 현재 라이브 상품 -> AS-IS 스냅샷. 상품이 없으면 null. */
    private ProductSnapshotDto captureAsis(Long productId) {
        return productRepo.findById(productId)
                .map(p -> snapshotMapper.toSnapshot(p, descRepo.findAllByProductIdOrderBySortOrderAsc(productId)))
                .orElse(null);
    }

    private void upsertDescriptions(Long productId, ProductSnapshotDto s) {
        upsertDesc(productId, ProductSnapshotMapper.K_TARGET, s.target(), 10);
        upsertDesc(productId, ProductSnapshotMapper.K_LIMIT, s.loanLimit(), 11);
        upsertDesc(productId, ProductSnapshotMapper.K_REPAYMENT, s.repayment(), 12);
        upsertDesc(productId, ProductSnapshotMapper.K_SUMMARY, s.summary(), 13);
    }

    private void upsertDesc(Long productId, String key, String value, int order) {
        String v = value == null ? "" : value;
        descRepo.findByProductIdAndAttrKey(productId, key)
                .ifPresentOrElse(
                        d -> d.updateAttrValue(v),
                        () -> descRepo.save(ProductDescription.builder()
                                .productId(productId).attrKey(key).attrValue(v).sortOrder(order).build())
                );
    }

    private void evictProductCaches() {
        clearCache("products");
        clearCache("productDetail");
    }

    private void clearCache(String name) {
        Cache c = cacheManager.getCache(name);
        if (c != null) c.clear();
    }

    private static BigDecimal parseRate(String s) {
        if (s == null || s.isBlank()) return new BigDecimal("0.00");
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            return new BigDecimal("0.00");
        }
    }

    /** ISO-8601 문자열 -> 서버 로컬시각. Z/offset/naive 모두 허용. */
    private static LocalDateTime parseIso(String s) {
        try {
            return OffsetDateTime.parse(s).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception ignore) {
            try {
                return LocalDateTime.ofInstant(Instant.parse(s), ZoneId.systemDefault());
            } catch (Exception ignore2) {
                return LocalDateTime.parse(s);
            }
        }
    }
}
