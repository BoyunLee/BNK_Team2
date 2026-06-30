package com.busanbank.loan.domain.loan;

import com.busanbank.loan.domain.customer.entity.Account;
import com.busanbank.loan.domain.customer.entity.Customer;
import com.busanbank.loan.domain.customer.repository.AccountRepository;
import com.busanbank.loan.domain.customer.repository.CustomerRepository;
import com.busanbank.loan.domain.loan.dto.request.ContractConditionsRequest;
import com.busanbank.loan.domain.loan.dto.response.ConditionsResponse;
import com.busanbank.loan.domain.loan.dto.response.ExecuteResponse;
import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.entity.LoanContract;
import com.busanbank.loan.domain.loan.entity.LoanScreening;
import com.busanbank.loan.domain.loan.repository.ApplicationDocumentLogRepository;
import com.busanbank.loan.domain.loan.repository.CustomerVerificationRepository;
import com.busanbank.loan.domain.loan.repository.LoanContractRepository;
import com.busanbank.loan.domain.loan.repository.LoanPreferentialAppliedRepository;
import com.busanbank.loan.domain.loan.repository.LoanScreeningRepository;
import com.busanbank.loan.domain.loan.service.ContractService;
import com.busanbank.loan.domain.loan.service.LoanApplicationService;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.domain.product.repository.ProductDescriptionRepository;
import com.busanbank.loan.domain.product.repository.ProductPreferentialRateRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private LoanContractRepository loanContractRepository;

    @Mock
    private LoanPreferentialAppliedRepository loanPreferentialAppliedRepository;

    @Mock
    private ProductPreferentialRateRepository productPreferentialRateRepository;

    @Mock
    private LoanScreeningRepository loanScreeningRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerVerificationRepository customerVerificationRepository;

    @Mock
    private ApplicationDocumentLogRepository applicationDocumentLogRepository;

    @Mock
    private LoanProductRepository loanProductRepository;

    @Mock
    private ProductDescriptionRepository productDescriptionRepository;

    @Mock
    private LoanApplicationService loanApplicationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ContractService contractService;

    // ──────────────── saveConditions ────────────────

    @Test
    @DisplayName("대출 조건 저장 성공 - finalRate = baseRate (우대금리 없음)")
    void saveConditions_success_noPreferential() {
        // given
        String loanAccountNo = "BNK20240101000000001";
        Long customerId = 1L;

        LoanApplication application = buildValidApplication(loanAccountNo, "6");
        when(loanApplicationService.findAndValidate(loanAccountNo, "6")).thenReturn(application);

        LoanScreening screening = buildScreening(loanAccountNo, 22_500_000L, BigDecimal.valueOf(4.5), "APPROVED");
        when(loanScreeningRepository.findByLoanAccountNo(loanAccountNo)).thenReturn(Optional.of(screening));

        when(loanContractRepository.save(any(LoanContract.class))).thenAnswer(inv -> inv.getArgument(0));

        ContractConditionsRequest request = new ContractConditionsRequest(
                "원리금균등", "F", null, null, "36개월",
                "110000000001", "생활비", 10_000_000L, List.of()
        );

        // when
        ConditionsResponse response = contractService.saveConditions(loanAccountNo, customerId, request);

        // then
        // finalRate = 4.5 - 0 = 4.5
        assertThat(response.finalRate()).isEqualByComparingTo(BigDecimal.valueOf(4.5));
        assertThat(response.loanAmount()).isEqualTo(10_000_000L);

        // Verify application status updated to '7'
        assertThat(application.getStatusCode()).isEqualTo("7");

        // Verify contract saved
        verify(loanContractRepository).save(any(LoanContract.class));
    }

    @Test
    @DisplayName("대출 조건 저장 성공 - finalRate 최소값 0.1% 적용")
    void saveConditions_finalRateFlooredToMin() {
        // given
        String loanAccountNo = "BNK20240101000000001";
        Long customerId = 1L;

        LoanApplication application = buildValidApplication(loanAccountNo, "6");
        when(loanApplicationService.findAndValidate(loanAccountNo, "6")).thenReturn(application);

        // baseRate = 2.0
        LoanScreening screening = buildScreening(loanAccountNo, 22_500_000L, BigDecimal.valueOf(2.0), "APPROVED");
        when(loanScreeningRepository.findByLoanAccountNo(loanAccountNo)).thenReturn(Optional.of(screening));

        // preferential rate 3.0 → totalPreferential = 3.0 → finalRate = 2.0 - 3.0 = -1.0 → clamped to 0.1
        ProductPreferentialRateRepository mockRateRepo = productPreferentialRateRepository;
        com.busanbank.loan.domain.product.entity.ProductPreferentialRate preferentialRate =
                buildPreferentialRate(1L, 100L, BigDecimal.valueOf(3.0));
        when(productPreferentialRateRepository.findAllByPreferentialIdIn(List.of(1L)))
                .thenReturn(List.of(preferentialRate));
        when(loanPreferentialAppliedRepository.saveAll(any())).thenReturn(List.of());
        when(loanContractRepository.save(any(LoanContract.class))).thenAnswer(inv -> inv.getArgument(0));

        ContractConditionsRequest request = new ContractConditionsRequest(
                "원리금균등", "F", null, null, "36개월",
                "110000000001", "생활비", 10_000_000L, List.of(1L)
        );

        // when
        ConditionsResponse response = contractService.saveConditions(loanAccountNo, customerId, request);

        // then - finalRate should be floored to 0.1
        assertThat(response.finalRate()).isEqualByComparingTo(new BigDecimal("0.1"));
    }

    @Test
    @DisplayName("대출 조건 저장 실패 - 신청 금액이 승인 한도 초과")
    void saveConditions_loanAmountExceeded() {
        // given
        String loanAccountNo = "BNK20240101000000001";
        Long customerId = 1L;

        LoanApplication application = buildValidApplication(loanAccountNo, "6");
        when(loanApplicationService.findAndValidate(loanAccountNo, "6")).thenReturn(application);

        // maxLimitAmt = 10_000_000
        LoanScreening screening = buildScreening(loanAccountNo, 10_000_000L, BigDecimal.valueOf(4.5), "APPROVED");
        when(loanScreeningRepository.findByLoanAccountNo(loanAccountNo)).thenReturn(Optional.of(screening));

        // loanAmount = 20_000_000 > maxLimitAmt = 10_000_000
        ContractConditionsRequest request = new ContractConditionsRequest(
                "원리금균등", "F", null, null, "36개월",
                "110000000001", "생활비", 20_000_000L, List.of()
        );

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> contractService.saveConditions(loanAccountNo, customerId, request));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LOAN_AMOUNT_EXCEEDED);
    }

    @Test
    @DisplayName("대출 조건 저장 실패 - 심사 결과 거절")
    void saveConditions_screeningRejected() {
        // given
        String loanAccountNo = "BNK20240101000000001";
        Long customerId = 1L;

        LoanApplication application = buildValidApplication(loanAccountNo, "6");
        when(loanApplicationService.findAndValidate(loanAccountNo, "6")).thenReturn(application);

        LoanScreening screening = buildScreening(loanAccountNo, 10_000_000L, BigDecimal.valueOf(4.5), "REJECTED");
        when(loanScreeningRepository.findByLoanAccountNo(loanAccountNo)).thenReturn(Optional.of(screening));

        ContractConditionsRequest request = new ContractConditionsRequest(
                "원리금균등", "F", null, null, "36개월",
                "110000000001", "생활비", 5_000_000L, List.of()
        );

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> contractService.saveConditions(loanAccountNo, customerId, request));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.SCREENING_REJECTED);
    }

    // ──────────────── executeLoan ────────────────

    @Test
    @DisplayName("대출 실행 성공 - 계좌 잔액 증가, 상태 '9'")
    void executeLoan_success() {
        // given
        String loanAccountNo = "BNK20240101000000001";
        Long customerId = 1L;
        String depositAccountNo = "110000000001";

        LoanApplication application = buildValidApplication(loanAccountNo, "8");
        when(loanApplicationService.findAndValidate(loanAccountNo, "8")).thenReturn(application);

        Customer customer = buildCustomer(customerId, "encodedSimplePwd");
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("123456", "encodedSimplePwd")).thenReturn(true);

        LoanContract contract = buildContract(loanAccountNo, customerId, 10_000_000L, depositAccountNo);
        when(loanContractRepository.findByLoanAccountNo(loanAccountNo)).thenReturn(Optional.of(contract));

        Account account = buildAccount(depositAccountNo, customerId);
        when(accountRepository.findByAccountNo(depositAccountNo)).thenReturn(Optional.of(account));

        when(customerVerificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        ExecuteResponse response = contractService.executeLoan(loanAccountNo, customerId, "123456");

        // then
        assertThat(response.loanAccountNo()).isEqualTo(loanAccountNo);
        assertThat(response.loanAmount()).isEqualTo(10_000_000L);

        // Verify account balance deposited
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(10_000_000L));

        // Verify application status updated to '9'
        assertThat(application.getStatusCode()).isEqualTo("9");

        // Verify contract executed
        assertThat(contract.getStatus()).isEqualTo("CONTRACTED");

        // Verify CustomerVerification saved
        verify(customerVerificationRepository).save(any());
    }

    @Test
    @DisplayName("대출 실행 실패 - 잘못된 간편 비밀번호")
    void executeLoan_wrongPassword() {
        // given
        String loanAccountNo = "BNK20240101000000001";
        Long customerId = 1L;

        LoanApplication application = buildValidApplication(loanAccountNo, "8");
        when(loanApplicationService.findAndValidate(loanAccountNo, "8")).thenReturn(application);

        Customer customer = buildCustomer(customerId, "encodedSimplePwd");
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("wrongpwd", "encodedSimplePwd")).thenReturn(false);

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> contractService.executeLoan(loanAccountNo, customerId, "wrongpwd"));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
        verify(accountRepository, never()).findByAccountNo(anyString());
    }

    // ──────────────── helpers ────────────────

    private LoanApplication buildValidApplication(String loanAccountNo, String statusCode) {
        return LoanApplication.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(1L)
                .productId(100L)
                .statusCode(statusCode)
                .expireAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    private LoanScreening buildScreening(String loanAccountNo, Long maxLimitAmt,
                                         BigDecimal baseRate, String result) {
        return LoanScreening.builder()
                .loanAccountNo(loanAccountNo)
                .maxLimitAmt(maxLimitAmt)
                .appliedBaseRate(baseRate)
                .result(result)
                .build();
    }

    private LoanContract buildContract(String loanAccountNo, Long customerId,
                                       Long loanAmount, String depositAccountNo) {
        return LoanContract.builder()
                .loanAccountNo(loanAccountNo)
                .customerId(customerId)
                .loanAmount(loanAmount)
                .rateTypeCode("F")
                .finalRate(BigDecimal.valueOf(4.5))
                .repaymentType("원리금균등")
                .loanPeriod("36개월")
                .maturityDate(LocalDate.now().plusMonths(36))
                .depositAccountNo(depositAccountNo)
                .fundPurpose("생활비")
                .build();
    }

    private Customer buildCustomer(Long customerId, String encodedSimplePassword) {
        Customer customer = Customer.builder()
                .name("테스트")
                .phoneNo("010-1234-5678")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("부산시")
                .email("test@test.com")
                .simplePassword(encodedSimplePassword)
                .build();
        setField(customer, "customerId", customerId);
        setField(customer, "status", "ACTIVE");
        return customer;
    }

    private Account buildAccount(String accountNo, Long customerId) {
        return Account.builder()
                .accountNo(accountNo)
                .customerId(customerId)
                .accountPassword("encodedAcctPwd")
                .build();
    }

    private com.busanbank.loan.domain.product.entity.ProductPreferentialRate buildPreferentialRate(
            Long preferentialId, Long productId, BigDecimal rateValue) {
        com.busanbank.loan.domain.product.entity.ProductPreferentialRate rate =
                com.busanbank.loan.domain.product.entity.ProductPreferentialRate.builder()
                        .productId(productId)
                        .conditionCode("COND001")
                        .conditionName("급여이체 우대")
                        .rateValue(rateValue)
                        .description("급여이체 시 우대금리 적용")
                        .build();
        setField(rate, "preferentialId", preferentialId);
        return rate;
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
