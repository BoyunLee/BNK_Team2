package com.busanbank.loan.domain.customer;

import com.busanbank.loan.domain.customer.dto.response.LoanDetailResponse;
import com.busanbank.loan.domain.customer.dto.response.LoanSummaryResponse;
import com.busanbank.loan.domain.customer.dto.response.MyAccountResponse;
import com.busanbank.loan.domain.customer.entity.Account;
import com.busanbank.loan.domain.customer.entity.Customer;
import com.busanbank.loan.domain.customer.repository.AccountRepository;
import com.busanbank.loan.domain.customer.repository.CustomerRepository;
import com.busanbank.loan.domain.customer.service.AccountService;
import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.entity.LoanContract;
import com.busanbank.loan.domain.loan.repository.LoanApplicationRepository;
import com.busanbank.loan.domain.loan.repository.LoanContractRepository;
import com.busanbank.loan.domain.loan.repository.LoanPreferentialAppliedRepository;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.domain.product.repository.ProductPreferentialRateRepository;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock CustomerRepository customerRepository;
    @Mock AccountRepository accountRepository;
    @Mock LoanApplicationRepository loanApplicationRepository;
    @Mock LoanContractRepository loanContractRepository;
    @Mock LoanProductRepository loanProductRepository;
    @Mock LoanPreferentialAppliedRepository loanPreferentialAppliedRepository;
    @Mock ProductPreferentialRateRepository productPreferentialRateRepository;

    @InjectMocks AccountService accountService;

    @Test
    @DisplayName("내 계좌 조회 - 성공")
    void getMyAccount_success() {
        Customer customer = buildCustomer(1L);
        Account account = buildAccount("110000000001", 1L, BigDecimal.valueOf(20_000_000));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByCustomerId(1L)).thenReturn(Optional.of(account));

        MyAccountResponse res = accountService.getMyAccount(1L);

        assertThat(res.balance()).isEqualByComparingTo(BigDecimal.valueOf(20_000_000));
        assertThat(res.status()).isEqualTo("ACTIVE");
        assertThat(res.accountNo()).contains("***");   // 마스킹 확인
        assertThat(res.customerName()).endsWith("*");  // 이름 마스킹 확인
    }

    @Test
    @DisplayName("내 계좌 조회 - 고객 없음")
    void getMyAccount_customerNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.getMyAccount(99L));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CUSTOMER_NOT_FOUND);
    }

    @Test
    @DisplayName("내 대출 목록 - 성공 (실행 완료 1건)")
    void getMyLoans_success() {
        LoanApplication app = buildApp("BNK001", 1L, 100L, "9");
        LoanProduct product = buildProduct(100L, "직장인 신용대출");
        LoanContract contract = buildContract("BNK001", 20_000_000L, BigDecimal.valueOf(4.1));

        when(loanApplicationRepository.findAllByCustomerIdOrderByAppliedAtDesc(1L))
                .thenReturn(List.of(app));
        when(loanProductRepository.findById(100L)).thenReturn(Optional.of(product));
        when(loanContractRepository.findByLoanAccountNo("BNK001")).thenReturn(Optional.of(contract));

        List<LoanSummaryResponse> list = accountService.getMyLoans(1L);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).statusName()).isEqualTo("대출 실행 완료");
        assertThat(list.get(0).loanAmount()).isEqualTo(20_000_000L);
        assertThat(list.get(0).finalRate()).isEqualByComparingTo(BigDecimal.valueOf(4.1));
    }

    @Test
    @DisplayName("내 대출 목록 - 빈 목록")
    void getMyLoans_empty() {
        when(loanApplicationRepository.findAllByCustomerIdOrderByAppliedAtDesc(1L))
                .thenReturn(List.of());

        List<LoanSummaryResponse> list = accountService.getMyLoans(1L);

        assertThat(list).isEmpty();
    }

    @Test
    @DisplayName("대출 상세 조회 - 실행 완료 건 (contract 포함)")
    void getLoanDetail_withContract() {
        LoanApplication app = buildApp("BNK001", 1L, 100L, "9");
        LoanProduct product = buildProduct(100L, "직장인 신용대출");
        LoanContract contract = buildContract("BNK001", 20_000_000L, BigDecimal.valueOf(4.1));

        when(loanApplicationRepository.findByLoanAccountNo("BNK001")).thenReturn(Optional.of(app));
        when(loanProductRepository.findById(100L)).thenReturn(Optional.of(product));
        when(loanContractRepository.findByLoanAccountNo("BNK001")).thenReturn(Optional.of(contract));
        when(loanPreferentialAppliedRepository.findAllByLoanAccountNo("BNK001")).thenReturn(List.of());

        LoanDetailResponse res = accountService.getLoanDetail(1L, "BNK001");

        assertThat(res.loanAccountNo()).isEqualTo("BNK001");
        assertThat(res.statusName()).isEqualTo("대출 실행 완료");
        assertThat(res.contract()).isNotNull();
        assertThat(res.contract().loanAmount()).isEqualTo(20_000_000L);
        assertThat(res.contract().depositAccountNo()).contains("***"); // 마스킹 확인
    }

    @Test
    @DisplayName("대출 상세 조회 - 진행 중인 신청서 (contract null)")
    void getLoanDetail_inProgress() {
        LoanApplication app = buildApp("BNK002", 1L, 100L, "3");

        when(loanApplicationRepository.findByLoanAccountNo("BNK002")).thenReturn(Optional.of(app));
        when(loanProductRepository.findById(100L)).thenReturn(Optional.of(buildProduct(100L, "직장인 신용대출")));
        when(loanContractRepository.findByLoanAccountNo("BNK002")).thenReturn(Optional.empty());

        LoanDetailResponse res = accountService.getLoanDetail(1L, "BNK002");

        assertThat(res.statusName()).isEqualTo("마이데이터 동의 완료");
        assertThat(res.contract()).isNull();
    }

    @Test
    @DisplayName("대출 상세 조회 - 타인 신청서 접근 불가")
    void getLoanDetail_otherCustomer() {
        LoanApplication app = buildApp("BNK001", 99L, 100L, "9"); // customerId=99

        when(loanApplicationRepository.findByLoanAccountNo("BNK001")).thenReturn(Optional.of(app));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.getLoanDetail(1L, "BNK001")); // customerId=1로 조회

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LOAN_NOT_FOUND);
    }

    // ── helpers ──────────────────────────────────────────────

    private Customer buildCustomer(Long id) {
        Customer c = Customer.builder()
                .name("홍길동").phoneNo("010-1234-5678").birthDate(LocalDate.of(1990,1,1))
                .address("부산시").email("test@test.com").simplePassword("sp").build();
        setField(c, "customerId", id);
        setField(c, "status", "ACTIVE");
        return c;
    }

    private Account buildAccount(String accountNo, Long customerId, BigDecimal balance) {
        Account a = Account.builder().accountNo(accountNo).customerId(customerId).accountPassword("pw").build();
        setField(a, "balance", balance);
        return a;
    }

    private LoanApplication buildApp(String no, Long customerId, Long productId, String status) {
        LoanApplication app = LoanApplication.builder()
                .loanAccountNo(no).customerId(customerId).productId(productId)
                .statusCode(status).expireAt(LocalDateTime.now().plusDays(1)).build();
        return app;
    }

    private LoanProduct buildProduct(Long id, String name) {
        LoanProduct p = LoanProduct.builder().productName(name).baseRate(BigDecimal.valueOf(4.5))
                .loanPeriod("36개월").status("SALE").build();
        setField(p, "productId", id);
        return p;
    }

    private LoanContract buildContract(String no, Long amount, BigDecimal rate) {
        LoanContract c = LoanContract.builder()
                .loanAccountNo(no).customerId(1L).loanAmount(amount).rateTypeCode("F")
                .finalRate(rate).repaymentType("원리금균등").loanPeriod("36개월")
                .maturityDate(LocalDate.now().plusMonths(36))
                .depositAccountNo("110000000001").fundPurpose("생활비").build();
        c.execute(LocalDateTime.now());
        return c;
    }

    private void setField(Object target, String name, Object value) {
        try {
            Class<?> clazz = target.getClass();
            while (clazz != null) {
                try {
                    java.lang.reflect.Field f = clazz.getDeclaredField(name);
                    f.setAccessible(true);
                    f.set(target, value);
                    return;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
