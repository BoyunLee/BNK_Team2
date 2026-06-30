package com.busanbank.loan.domain.customer;

import com.busanbank.loan.domain.customer.repository.AccountRepository;
import com.busanbank.loan.domain.customer.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    private static final String BASE_URL = "/api/v1/auth";

    @BeforeEach
    void clearCaches() {
        cacheManager.getCache("emailCode").clear();
        cacheManager.getCache("emailVerified").clear();
    }

    // ──────────────── email/send ────────────────

    @Test
    @DisplayName("이메일 인증 코드 전송 - 성공 (미가입 이메일)")
    void sendVerificationCode_success() throws Exception {
        Map<String, String> request = Map.of("email", "new@test.com");

        mockMvc.perform(post(BASE_URL + "/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify code was stored in cache
        String cached = cacheManager.getCache("emailCode").get("new@test.com", String.class);
        assertThat(cached).isNotNull().hasSize(6);
    }

    @Test
    @DisplayName("이메일 인증 코드 전송 - 실패 (이미 가입된 이메일)")
    void sendVerificationCode_duplicateEmail() throws Exception {
        // Pre-register a customer first
        setEmailVerified("existing@test.com");
        registerCustomer("existing@test.com", "123456");

        Map<String, String> request = Map.of("email", "existing@test.com");

        mockMvc.perform(post(BASE_URL + "/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("AUTH001"));
    }

    // ──────────────── email/verify ────────────────

    @Test
    @DisplayName("이메일 인증 코드 검증 - 성공")
    void verifyEmailCode_success() throws Exception {
        String email = "verify@test.com";
        // Manually put code in cache
        cacheManager.getCache("emailCode").put(email, "123456");

        Map<String, String> request = Map.of("email", email, "code", "123456");

        mockMvc.perform(post(BASE_URL + "/email/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify code evicted and emailVerified set
        String code = cacheManager.getCache("emailCode").get(email, String.class);
        assertThat(code).isNull();
        String verified = cacheManager.getCache("emailVerified").get(email, String.class);
        assertThat(verified).isEqualTo("true");
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 - 실패 (잘못된 코드)")
    void verifyEmailCode_wrongCode() throws Exception {
        String email = "wrong@test.com";
        cacheManager.getCache("emailCode").put(email, "999999");

        Map<String, String> request = Map.of("email", email, "code", "111111");

        mockMvc.perform(post(BASE_URL + "/email/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AUTH002"));
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 - 실패 (캐시에 코드 없음)")
    void verifyEmailCode_noCodeInCache() throws Exception {
        Map<String, String> request = Map.of("email", "nocode@test.com", "code", "000000");

        mockMvc.perform(post(BASE_URL + "/email/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AUTH002"));
    }

    // ──────────────── register ────────────────

    @Test
    @DisplayName("회원 가입 - 성공")
    void register_success() throws Exception {
        String email = "register@test.com";
        setEmailVerified(email);

        Map<String, Object> request = buildRegisterRequest(email, "123456");

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.customerId").isNumber())
                .andExpect(jsonPath("$.data.accountNo").isString());

        // Verify customer and account saved in DB
        assertThat(customerRepository.existsByEmail(email)).isTrue();
    }

    @Test
    @DisplayName("회원 가입 - 실패 (이메일 인증 미완료)")
    void register_emailNotVerified() throws Exception {
        Map<String, Object> request = buildRegisterRequest("notverified@test.com", "123456");

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AUTH003"));
    }

    @Test
    @DisplayName("회원 가입 - 실패 (중복 이메일)")
    void register_duplicateEmail() throws Exception {
        String email = "dup@test.com";
        setEmailVerified(email);
        registerCustomer(email, "123456");

        // Try to register again with same email
        setEmailVerified(email);
        Map<String, Object> request = buildRegisterRequest(email, "123456");

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("AUTH001"));
    }

    // ──────────────── login ────────────────

    @Test
    @DisplayName("로그인 - 성공")
    void login_success() throws Exception {
        String email = "login@test.com";
        String password = "123456";
        setEmailVerified(email);
        registerCustomer(email, password);

        Map<String, String> request = Map.of("email", email, "simplePassword", password);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customer.customerId").isNumber())
                .andExpect(jsonPath("$.data.accountNo").isString());
    }

    @Test
    @DisplayName("로그인 - 실패 (잘못된 비밀번호)")
    void login_wrongPassword() throws Exception {
        String email = "wrongpwd@test.com";
        setEmailVerified(email);
        registerCustomer(email, "123456");

        Map<String, String> request = Map.of("email", email, "simplePassword", "654321");

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH004"));
    }

    @Test
    @DisplayName("로그인 - 실패 (존재하지 않는 이메일)")
    void login_emailNotFound() throws Exception {
        Map<String, String> request = Map.of("email", "ghost@test.com", "simplePassword", "123456");

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH004"));
    }

    // ──────────────── logout ────────────────

    @Test
    @DisplayName("로그아웃 - 성공 (세션 무효화)")
    void logout_success() throws Exception {
        String email = "logout@test.com";
        setEmailVerified(email);
        registerCustomer(email, "123456");
        MockHttpSession session = loginCustomer(email, "123456");

        mockMvc.perform(post(BASE_URL + "/logout")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(session.isInvalid()).isTrue();
    }

    // ──────────────── helpers ────────────────

    /**
     * Directly sets "emailVerified" cache for the given email.
     */
    private void setEmailVerified(String email) {
        cacheManager.getCache("emailVerified").put(email, "true");
    }

    /**
     * Performs the full register flow and returns the register response body.
     */
    private void registerCustomer(String email, String simplePassword) throws Exception {
        Map<String, Object> request = buildRegisterRequest(email, simplePassword);
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    /**
     * Logs in and returns the resulting MockHttpSession.
     */
    MockHttpSession loginCustomer(String email, String simplePassword) throws Exception {
        Map<String, String> loginReq = Map.of("email", email, "simplePassword", simplePassword);
        MvcResult result = mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession();
    }

    private Map<String, Object> buildRegisterRequest(String email, String simplePassword) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "테스트");
        map.put("phoneNo", "010-1234-5678");
        map.put("email", email);
        map.put("address", "부산시 중구");
        map.put("birthDate", LocalDate.of(1990, 1, 1).toString());
        map.put("simplePassword", simplePassword);
        map.put("accountPassword", "1234");
        return map;
    }
}
