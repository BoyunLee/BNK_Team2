package com.busanbank.loan.domain.loan;

import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.repository.LoanApplicationRepository;
import com.busanbank.loan.domain.loan.repository.MydataConsentRepository;
import com.busanbank.loan.domain.loan.service.LoanApplicationService;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanProductRepository loanProductRepository;

    @Mock
    private MydataConsentRepository mydataConsentRepository;

    @InjectMocks
    private LoanApplicationService loanApplicationService;

    // ──────────────── createApplication ────────────────

    @Test
    @DisplayName("대출 신청 생성 성공 - loanAccountNo 형식 검증 (BNK + date + 9digits)")
    void createApplication_success() {
        // given
        Long customerId = 1L;
        Long productId = 100L;

        LoanProduct product = buildProduct(productId);
        when(loanProductRepository.findById(productId)).thenReturn(Optional.of(product));
        when(loanApplicationRepository.existsByCustomerIdAndStatusCodeNotIn(anyLong(), anyList()))
                .thenReturn(false);
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        LoanApplication result = loanApplicationService.createApplication(customerId, productId);

        // then
        assertThat(result.getLoanAccountNo()).startsWith("BNK");
        // BNK + 8-digit date + 9-digit seq = 3 + 8 + 9 = 20 chars
        assertThat(result.getLoanAccountNo()).hasSize(20);
        // Date portion (chars 3-11) should be digits
        String datePart = result.getLoanAccountNo().substring(3, 11);
        assertThat(datePart).matches("\\d{8}");
        // Seq portion (chars 11-20) should be digits
        String seqPart = result.getLoanAccountNo().substring(11);
        assertThat(seqPart).matches("\\d{9}");

        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getStatusCode()).isEqualTo("1");
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("대출 신청 생성 실패 - 상품 없음")
    void createApplication_productNotFound() {
        // given
        when(loanProductRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanApplicationService.createApplication(1L, 999L));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("대출 신청 생성 실패 - 이미 진행 중인 대출")
    void createApplication_alreadyInProgress() {
        // given
        Long productId = 100L;
        when(loanProductRepository.findById(productId)).thenReturn(Optional.of(buildProduct(productId)));
        when(loanApplicationRepository.existsByCustomerIdAndStatusCodeNotIn(anyLong(), anyList()))
                .thenReturn(true);

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanApplicationService.createApplication(1L, productId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LOAN_ALREADY_IN_PROGRESS);
    }

    // ──────────────── findAndValidate ────────────────

    @Test
    @DisplayName("findAndValidate - 신청서 없음 → LOAN_NOT_FOUND")
    void findAndValidate_notFound() {
        // given
        when(loanApplicationRepository.findByLoanAccountNo("BNK20240101000000001"))
                .thenReturn(Optional.empty());

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanApplicationService.findAndValidate("BNK20240101000000001", "1"));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LOAN_NOT_FOUND);
    }

    @Test
    @DisplayName("findAndValidate - 만료된 신청서 → LOAN_EXPIRED")
    void findAndValidate_expired() {
        // given
        LoanApplication expiredApp = buildExpiredApplication("BNK20240101000000001", "1");
        when(loanApplicationRepository.findByLoanAccountNo("BNK20240101000000001"))
                .thenReturn(Optional.of(expiredApp));

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanApplicationService.findAndValidate("BNK20240101000000001", "1"));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LOAN_EXPIRED);
    }

    @Test
    @DisplayName("findAndValidate - 단계 불일치 → INVALID_STEP")
    void findAndValidate_wrongStatus() {
        // given
        LoanApplication app = buildValidApplication("BNK20240101000000001", "2");
        when(loanApplicationRepository.findByLoanAccountNo("BNK20240101000000001"))
                .thenReturn(Optional.of(app));

        // when - expecting status "1" but actual is "2"
        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanApplicationService.findAndValidate("BNK20240101000000001", "1"));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_STEP);
    }

    @Test
    @DisplayName("findAndValidate - 올바른 상태 → 신청서 반환")
    void findAndValidate_success() {
        // given
        LoanApplication app = buildValidApplication("BNK20240101000000001", "1");
        when(loanApplicationRepository.findByLoanAccountNo("BNK20240101000000001"))
                .thenReturn(Optional.of(app));

        // when
        LoanApplication result = loanApplicationService.findAndValidate("BNK20240101000000001", "1");

        // then
        assertThat(result.getLoanAccountNo()).isEqualTo("BNK20240101000000001");
        assertThat(result.getStatusCode()).isEqualTo("1");
    }

    // ──────────────── helpers ────────────────

    private LoanProduct buildProduct(Long productId) {
        LoanProduct product = LoanProduct.builder()
                .productName("테스트 대출")
                .baseRate(BigDecimal.valueOf(4.5))
                .loanPeriod("36개월")
                .status("SALE")
                .build();
        setField(product, "productId", productId);
        return product;
    }

    private LoanApplication buildValidApplication(String loanAccountNo, String statusCode) {
        LoanApplication app = LoanApplication.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(1L)
                .productId(100L)
                .statusCode(statusCode)
                .expireAt(LocalDateTime.now().plusDays(1))
                .build();
        return app;
    }

    private LoanApplication buildExpiredApplication(String loanAccountNo, String statusCode) {
        LoanApplication app = LoanApplication.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(1L)
                .productId(100L)
                .statusCode(statusCode)
                .expireAt(LocalDateTime.now().minusDays(1))
                .build();
        return app;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }

    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new RuntimeException("Field " + fieldName + " not found");
    }
}
