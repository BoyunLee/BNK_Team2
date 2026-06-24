package com.busanbank.loan.domain.loan;

import com.busanbank.loan.domain.loan.dto.response.ScreeningResponse;
import com.busanbank.loan.domain.loan.entity.IncomeInfo;
import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.entity.LoanScreening;
import com.busanbank.loan.domain.loan.repository.IncomeInfoRepository;
import com.busanbank.loan.domain.loan.repository.LoanApplicationRepository;
import com.busanbank.loan.domain.loan.repository.LoanScreeningRepository;
import com.busanbank.loan.domain.loan.service.LoanApplicationService;
import com.busanbank.loan.domain.loan.service.MydataService;
import com.busanbank.loan.domain.loan.service.ScreeningService;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    @Mock
    private LoanScreeningRepository loanScreeningRepository;

    @Mock
    private IncomeInfoRepository incomeInfoRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanProductRepository loanProductRepository;

    @Mock
    private MydataService mydataService;

    @Mock
    private LoanApplicationService loanApplicationService;

    @InjectMocks
    private ScreeningService screeningService;

    @Test
    @DisplayName("심사 계산 - 연소득 4500만원 → 한도 2250만원 (50% 적용)")
    void calculateScreening_normalIncome() {
        // given
        String loanAccountNo = "BNK20240101000000001";
        Long annualIncome = 45_000_000L;

        LoanApplication application = buildValidApplication(loanAccountNo, "5", 100L);
        when(loanApplicationService.findAndValidate(loanAccountNo, "5")).thenReturn(application);

        IncomeInfo incomeInfo = buildIncomeInfo(loanAccountNo, 1L, annualIncome);
        when(incomeInfoRepository.findByLoanAccountNo(loanAccountNo)).thenReturn(Optional.of(incomeInfo));

        LoanProduct product = buildProduct(100L, BigDecimal.valueOf(4.5));
        when(loanProductRepository.findById(100L)).thenReturn(Optional.of(product));

        when(loanScreeningRepository.save(any(LoanScreening.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        ScreeningResponse response = screeningService.calculateScreening(loanAccountNo);

        // then
        assertThat(response.maxLimitAmt()).isEqualTo(22_500_000L);
        assertThat(response.appliedBaseRate()).isEqualByComparingTo(BigDecimal.valueOf(4.5));
        assertThat(response.result()).isEqualTo("APPROVED");

        // Verify LoanScreening was saved with correct values
        ArgumentCaptor<LoanScreening> captor = ArgumentCaptor.forClass(LoanScreening.class);
        verify(loanScreeningRepository).save(captor.capture());
        LoanScreening savedScreening = captor.getValue();
        assertThat(savedScreening.getMaxLimitAmt()).isEqualTo(22_500_000L);
        assertThat(savedScreening.getResult()).isEqualTo("APPROVED");

        // Verify application status updated to '6'
        assertThat(application.getStatusCode()).isEqualTo("6");
    }

    @Test
    @DisplayName("심사 계산 - 연소득 2억5천만원 → 한도 1억원 (상한 적용)")
    void calculateScreening_highIncomeCapped() {
        // given
        String loanAccountNo = "BNK20240101000000002";
        Long annualIncome = 250_000_000L;

        LoanApplication application = buildValidApplication(loanAccountNo, "5", 100L);
        when(loanApplicationService.findAndValidate(loanAccountNo, "5")).thenReturn(application);

        IncomeInfo incomeInfo = buildIncomeInfo(loanAccountNo, 1L, annualIncome);
        when(incomeInfoRepository.findByLoanAccountNo(loanAccountNo)).thenReturn(Optional.of(incomeInfo));

        LoanProduct product = buildProduct(100L, BigDecimal.valueOf(3.8));
        when(loanProductRepository.findById(100L)).thenReturn(Optional.of(product));

        when(loanScreeningRepository.save(any(LoanScreening.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        ScreeningResponse response = screeningService.calculateScreening(loanAccountNo);

        // then
        // min(250_000_000 * 0.5, 100_000_000) = min(125_000_000, 100_000_000) = 100_000_000
        assertThat(response.maxLimitAmt()).isEqualTo(100_000_000L);
        assertThat(response.result()).isEqualTo("APPROVED");

        // Verify saved screening has capped amount
        ArgumentCaptor<LoanScreening> captor = ArgumentCaptor.forClass(LoanScreening.class);
        verify(loanScreeningRepository).save(captor.capture());
        assertThat(captor.getValue().getMaxLimitAmt()).isEqualTo(100_000_000L);
    }

    // ──────────────── helpers ────────────────

    private LoanApplication buildValidApplication(String loanAccountNo, String statusCode, Long productId) {
        LoanApplication app = LoanApplication.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(1L)
                .productId(productId)
                .statusCode(statusCode)
                .expireAt(LocalDateTime.now().plusDays(1))
                .build();
        return app;
    }

    private IncomeInfo buildIncomeInfo(String loanAccountNo, Long customerId, Long annualIncome) {
        return IncomeInfo.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .companyName("테스트 회사")
                .jobType("직장인")
                .employmentType("정규직")
                .annualIncome(annualIncome)
                .build();
    }

    private LoanProduct buildProduct(Long productId, BigDecimal baseRate) {
        LoanProduct product = LoanProduct.builder()
                .productName("테스트 대출")
                .baseRate(baseRate)
                .loanPeriod("36개월")
                .status("SALE")
                .build();
        setField(product, "productId", productId);
        return product;
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
