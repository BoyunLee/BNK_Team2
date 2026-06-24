package com.busanbank.loan.domain.customer;

import com.busanbank.loan.domain.customer.dto.request.LoginRequest;
import com.busanbank.loan.domain.customer.dto.request.RegisterRequest;
import com.busanbank.loan.domain.customer.entity.Account;
import com.busanbank.loan.domain.customer.entity.Customer;
import com.busanbank.loan.domain.customer.repository.AccountRepository;
import com.busanbank.loan.domain.customer.repository.CustomerRepository;
import com.busanbank.loan.domain.customer.service.CustomerService;
import com.busanbank.loan.domain.customer.service.EmailService;
import com.busanbank.loan.global.error.code.ErrorCode;
import com.busanbank.loan.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CustomerService customerService;

    // ──────────────── register ────────────────

    @Test
    @DisplayName("회원 가입 성공")
    void register_success() {
        // given
        RegisterRequest req = buildRegisterRequest("new@test.com", "123456");
        when(emailService.isEmailVerified("new@test.com")).thenReturn(true);
        when(customerRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedHash");

        Customer savedCustomer = buildCustomerWithId(1L, "new@test.com");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(accountRepository.save(any(Account.class))).thenReturn(
                Account.builder().accountNo("110000000001").customerId(1L).accountPassword("encodedHash").build()
        );

        // when
        var response = customerService.register(req);

        // then
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getAccountNo()).isNotNull();
        verify(customerRepository).save(any(Customer.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("회원 가입 실패 - 이메일 미인증")
    void register_emailNotVerified() {
        // given
        RegisterRequest req = buildRegisterRequest("notverified@test.com", "123456");
        when(emailService.isEmailVerified("notverified@test.com")).thenReturn(false);

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.register(req));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMAIL_NOT_VERIFIED);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("회원 가입 실패 - 중복 이메일")
    void register_duplicateEmail() {
        // given
        RegisterRequest req = buildRegisterRequest("dup@test.com", "123456");
        when(emailService.isEmailVerified("dup@test.com")).thenReturn(true);
        when(customerRepository.existsByEmail("dup@test.com")).thenReturn(true);

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.register(req));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
        verify(customerRepository, never()).save(any());
    }

    // ──────────────── login ────────────────

    @Test
    @DisplayName("로그인 성공 - 세션에 customerId 저장")
    void login_success() {
        // given
        LoginRequest req = buildLoginRequest("user@test.com", "123456");
        Customer customer = buildCustomerWithId(10L, "user@test.com");
        Account account = Account.builder()
                .accountNo("110000000010")
                .customerId(10L)
                .accountPassword("encodedHash")
                .build();

        when(customerRepository.findByEmail("user@test.com")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("123456", customer.getSimplePassword())).thenReturn(true);
        when(accountRepository.findByCustomerId(10L)).thenReturn(Optional.of(account));

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // when
        var response = customerService.login(req, httpRequest);

        // then
        assertThat(response.getCustomer().getCustomerId()).isEqualTo(10L);
        assertThat(response.getAccountNo()).isEqualTo("110000000010");

        // Verify session was set
        Object sessionAttr = httpRequest.getSession(false)
                .getAttribute("SESSION_CUSTOMER_ID");
        assertThat(sessionAttr).isEqualTo(10L);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_customerNotFound() {
        // given
        LoginRequest req = buildLoginRequest("ghost@test.com", "123456");
        when(customerRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.login(req, new MockHttpServletRequest()));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_wrongPassword() {
        // given
        LoginRequest req = buildLoginRequest("user@test.com", "wrongpwd");
        Customer customer = buildCustomerWithId(10L, "user@test.com");

        when(customerRepository.findByEmail("user@test.com")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("wrongpwd", customer.getSimplePassword())).thenReturn(false);

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.login(req, new MockHttpServletRequest()));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("로그인 실패 - 비활성 고객")
    void login_inactiveCustomer() {
        // given
        LoginRequest req = buildLoginRequest("inactive@test.com", "123456");
        Customer customer = buildInactiveCustomer(20L, "inactive@test.com");

        when(customerRepository.findByEmail("inactive@test.com")).thenReturn(Optional.of(customer));

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.login(req, new MockHttpServletRequest()));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INACTIVE_CUSTOMER);
    }

    // ──────────────── helpers ────────────────

    private RegisterRequest buildRegisterRequest(String email, String simplePassword) {
        // Use reflection to populate the validated RegisterRequest (which has no public all-args constructor)
        RegisterRequest req = new RegisterRequest();
        setField(req, "name", "테스트");
        setField(req, "phoneNo", "010-1234-5678");
        setField(req, "email", email);
        setField(req, "address", "부산시 중구");
        setField(req, "birthDate", "1990-01-01");
        setField(req, "simplePassword", simplePassword);
        setField(req, "accountPassword", "1234");
        setField(req, "signaturePassword", "654321");
        return req;
    }

    private LoginRequest buildLoginRequest(String email, String simplePassword) {
        LoginRequest req = new LoginRequest();
        setField(req, "email", email);
        setField(req, "simplePassword", simplePassword);
        return req;
    }

    private Customer buildCustomerWithId(Long id, String email) {
        Customer customer = Customer.builder()
                .name("테스트")
                .phoneNo("010-1234-5678")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("부산시")
                .email(email)
                .password("encodedSignaturePwd")
                .simplePassword("encodedSimplePwd")
                .build();
        setField(customer, "customerId", id);
        setField(customer, "status", "ACTIVE");
        return customer;
    }

    private Customer buildInactiveCustomer(Long id, String email) {
        Customer customer = buildCustomerWithId(id, email);
        setField(customer, "status", "INACTIVE");
        return customer;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName + " on " + target.getClass(), e);
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
        throw new RuntimeException("Field " + fieldName + " not found in class hierarchy of " + clazz.getName());
    }
}
