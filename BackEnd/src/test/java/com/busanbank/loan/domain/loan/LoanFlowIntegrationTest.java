package com.busanbank.loan.domain.loan;

import com.busanbank.loan.domain.customer.repository.AccountRepository;
import com.busanbank.loan.domain.loan.repository.LoanApplicationRepository;
import com.busanbank.loan.domain.product.entity.LoanProduct;
import com.busanbank.loan.domain.product.entity.ProductTermsBase;
import com.busanbank.loan.domain.product.entity.ProductTermsHistory;
import com.busanbank.loan.domain.product.repository.LoanProductRepository;
import com.busanbank.loan.domain.product.repository.ProductTermsBaseRepository;
import com.busanbank.loan.domain.product.repository.ProductTermsHistoryRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoanFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private LoanProductRepository loanProductRepository;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private ProductTermsBaseRepository productTermsBaseRepository;

    @Autowired
    private ProductTermsHistoryRepository productTermsHistoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    private MockHttpSession session;
    private Long productId;
    private String loanAccountNo;
    private Long customerId;
    private String accountNo;

    private static final String customerEmail = "loan-test@bank.com";
    private static final String simplePassword = "123456";
    private static final String AUTH_URL = "/api/v1/auth";
    private static final String LOANS_URL = "/api/v1/loans/applications";

    @BeforeEach
    void setUp() throws Exception {
        cacheManager.getCache("emailCode").clear();
        cacheManager.getCache("emailVerified").clear();

        // Step 1: Register customer (pre-set emailVerified cache)
        cacheManager.getCache("emailVerified").put(customerEmail, "true");
        Map<String, Object> registerReq = buildRegisterRequest(customerEmail, simplePassword);
        MvcResult registerResult = mockMvc.perform(post(AUTH_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> registerBody = parseBody(registerResult);
        Map<String, Object> registerData = (Map<String, Object>) registerBody.get("data");
        customerId = ((Number) registerData.get("customerId")).longValue();
        accountNo = (String) registerData.get("accountNo");

        // Step 2: Login to get session
        session = loginCustomer(customerEmail, simplePassword);

        // Step 3: Create a LoanProduct in DB
        LoanProduct product = loanProductRepository.save(
                LoanProduct.builder()
                        .productName("테스트 대출")
                        .baseRate(BigDecimal.valueOf(4.5))
                        .loanPeriod("36개월")
                        .status("SALE")
                        .build()
        );
        productId = product.getProductId();

        // Step 4: Create ProductTermsBase + ProductTermsHistory for required document types
        String[] termsTypes = {
                "ADMIN_INFO_REQUEST",
                "PERSONAL_INFO_CONSENT",
                "MOBILE_AUTH_TERMS",
                "PRODUCT_TERMS",
                "PRODUCT_DESCRIPTION",
                "BOND_CONTRACT"
        };
        for (String termsType : termsTypes) {
            ProductTermsBase base = productTermsBaseRepository.save(
                    ProductTermsBase.builder()
                            .productId(productId)
                            .termsType(termsType)
                            .termsPath("/terms/" + termsType.toLowerCase() + ".pdf")
                            .activeYn("Y")
                            .build()
            );
            productTermsHistoryRepository.save(
                    ProductTermsHistory.builder()
                            .termsId(base.getTermsId())
                            .termsSeq(1)
                            .termsPath("/terms/" + termsType.toLowerCase() + ".pdf")
                            .build()
            );
        }
    }

    @Test
    @DisplayName("대출 신청 전체 플로우")
    void fullLoanFlow() throws Exception {
        // ── Step 1: POST /api/v1/loans/applications → create application (status '1') ──
        Map<String, Object> createAppReq = Map.of("productId", productId);
        MvcResult createResult = mockMvc.perform(post(LOANS_URL)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAppReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.loanAccountNo").isString())
                .andReturn();

        Map<String, Object> createBody = parseBody(createResult);
        Map<String, Object> createData = (Map<String, Object>) createBody.get("data");
        loanAccountNo = (String) createData.get("loanAccountNo");

        assertThat(loanAccountNo).startsWith("BNK");
        assertThat(loanAccountNo.length()).isGreaterThan(3);

        // ── Step 2: POST /{loanAccountNo}/verification/suitability → status '2' ──
        // (적합성·적정성 폼 데이터는 프론트에서만 처리 — /suitability 엔드포인트 제거됨)
        Map<String, String> verifySuitabilityReq = Map.of("simplePassword", simplePassword);

        mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/verification/suitability")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifySuitabilityReq)))
                .andExpect(status().isOk());

        verifyApplicationStatus("2");

        // ── Step 4: POST /{loanAccountNo}/mydata-consent → status '3' ──
        Map<String, Object> consentItem = Map.of(
                "consentType", "MYDATA_USE",
                "dataProvider", "국세청"
        );
        Map<String, Object> mydataConsentReq = Map.of("consents", List.of(consentItem));

        mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/mydata-consent")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mydataConsentReq)))
                .andExpect(status().isOk());

        verifyApplicationStatus("3");

        // ── Step 5: POST /{loanAccountNo}/documents/ADMIN_INFO_REQUEST/view ──
        mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/documents/ADMIN_INFO_REQUEST/view")
                        .session(session)
                        .param("productId", String.valueOf(productId)))
                .andExpect(status().isOk());

        // ── Step 6: POST /{loanAccountNo}/documents/ADMIN_INFO_REQUEST/agree ──
        mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/documents/ADMIN_INFO_REQUEST/agree")
                        .session(session))
                .andExpect(status().isOk());

        // ── Step 7: POST /{loanAccountNo}/signatures → PRE_PROCESS sign (status '4') ──
        Map<String, Object> signatureReq = Map.of(
                "signStep", "PRE_PROCESS",
                "signType", "SIMPLE_CERT",
                "tokenId", "test-token-001",
                "originalValue", "서명 원문"
        );

        MvcResult signResult = mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/signatures")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signatureReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.signatureId").isNumber())
                .andReturn();

        verifyApplicationStatus("4");

        // ── Step 8: POST /{loanAccountNo}/signatures/token → issue token ──
        Map<String, String> tokenReq = Map.of("signType", "SIMPLE_CERT");

        MvcResult tokenResult = mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/signatures/token")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenId").isString())
                .andReturn();

        Map<String, Object> tokenBody = parseBody(tokenResult);
        Map<String, Object> tokenData = (Map<String, Object>) tokenBody.get("data");
        String tokenId = (String) tokenData.get("tokenId");

        // ── Step 9: POST /{loanAccountNo}/income → status '5' ──
        Map<String, Object> incomeReq = Map.of(
                "companyName", "테스트 회사",
                "jobType", "직장인",
                "employmentType", "정규직",
                "annualIncome", 45_000_000L
        );

        mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/income")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeReq)))
                .andExpect(status().isOk());

        verifyApplicationStatus("5");

        // ── Step 10: GET /{loanAccountNo}/mydata ──
        mockMvc.perform(get(LOANS_URL + "/" + loanAccountNo + "/mydata")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.incomeVerified").value(true));

        // ── Step 11: POST /{loanAccountNo}/screening → status '6' ──
        mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/screening")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.maxLimitAmt").value(22_500_000L))
                .andExpect(jsonPath("$.data.result").value("APPROVED"));

        verifyApplicationStatus("6");

        // ── Step 12: POST /{loanAccountNo}/contract/terms ──
        Map<String, Object> contractTermsReq = Map.of(
                "documentTypes", List.of("PRODUCT_TERMS", "BOND_CONTRACT")
        );

        mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/contract/terms")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractTermsReq)))
                .andExpect(status().isOk());

        // ── Step 13: POST /{loanAccountNo}/contract/conditions → status '7' ──
        Map<String, Object> conditionsReq = new LinkedHashMap<>();
        conditionsReq.put("repaymentType", "원리금균등");
        conditionsReq.put("rateTypeCode", "F");
        conditionsReq.put("rateChangeCycle", null);
        conditionsReq.put("loanPeriod", "36개월");
        conditionsReq.put("depositAccountNo", accountNo);
        conditionsReq.put("fundPurpose", "생활비");
        conditionsReq.put("loanAmount", 10_000_000L);
        conditionsReq.put("preferentialIds", List.of());

        mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/contract/conditions")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conditionsReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.loanAmount").value(10_000_000L));

        verifyApplicationStatus("7");

        // ── Step 14: GET /{loanAccountNo}/contract/confirm ──
        mockMvc.perform(get(LOANS_URL + "/" + loanAccountNo + "/contract/confirm")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("테스트 대출"))
                .andExpect(jsonPath("$.data.loanAmount").value(10_000_000L));

        // ── Step 15: POST /{loanAccountNo}/verification/contract → status '8' ──
        Map<String, Object> contractSignReq = Map.of(
                "signStep", "CONTRACT",
                "signType", "SIMPLE_CERT",
                "tokenId", tokenId,
                "originalValue", "계약 서명 원문"
        );

        mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/verification/contract")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractSignReq)))
                .andExpect(status().isOk());

        verifyApplicationStatus("8");

        // ── Step 16: POST /{loanAccountNo}/execute → status '9', verify account balance ──
        Map<String, String> executeReq = Map.of("simplePassword", simplePassword);

        mockMvc.perform(post(LOANS_URL + "/" + loanAccountNo + "/execute")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(executeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.loanAccountNo").value(loanAccountNo))
                .andExpect(jsonPath("$.data.loanAmount").value(10_000_000L));

        verifyApplicationStatus("9");

        // Verify account balance increased
        accountRepository.findByAccountNo(accountNo).ifPresent(account ->
                assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(10_000_000L))
        );
    }

    @Test
    @DisplayName("진행 중인 대출이 있으면 신규 신청 불가")
    void duplicateLoanApplication() throws Exception {
        // Create first application
        Map<String, Object> createAppReq = Map.of("productId", productId);
        mockMvc.perform(post(LOANS_URL)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAppReq)))
                .andExpect(status().isCreated());

        // Try to create second application → 409 LOAN_ALREADY_IN_PROGRESS
        mockMvc.perform(post(LOANS_URL)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAppReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("LOAN002"));
    }

    @Test
    @DisplayName("만료된 신청서로 다음 단계 진행 불가")
    void expiredApplicationCannotProceed() throws Exception {
        // Create first application
        Map<String, Object> createAppReq = Map.of("productId", productId);
        MvcResult createResult = mockMvc.perform(post(LOANS_URL)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAppReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> createBody = parseBody(createResult);
        Map<String, Object> createData = (Map<String, Object>) createBody.get("data");
        String createdLoanAccountNo = (String) createData.get("loanAccountNo");

        // Manually set expireAt to the past
        loanApplicationRepository.findByLoanAccountNo(createdLoanAccountNo).ifPresent(app -> {
            app.updateStatus("1"); // ensure status stays at 1
        });

        // Directly update expireAt via repository using a custom approach:
        // Since we can't set expireAt via updateStatus, we manipulate via EntityManager by rebuilding
        // We use a workaround: the findByLoanAccountNo returns the tracked entity; set expireAt via reflection.
        loanApplicationRepository.findByLoanAccountNo(createdLoanAccountNo).ifPresent(app -> {
            try {
                java.lang.reflect.Field expireAtField = app.getClass().getDeclaredField("expireAt");
                expireAtField.setAccessible(true);
                expireAtField.set(app, LocalDateTime.now().minusDays(1));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Try verification/suitability → 400 LOAN_EXPIRED
        // (/suitability 제거됨 — verification/suitability로 만료 검증)
        Map<String, String> verifyReq = Map.of("simplePassword", simplePassword);
        mockMvc.perform(post(LOANS_URL + "/" + createdLoanAccountNo + "/verification/suitability")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("LOAN003"));
    }

    // ──────────────── helpers ────────────────

    private MockHttpSession loginCustomer(String email, String password) throws Exception {
        Map<String, String> loginReq = Map.of("email", email, "simplePassword", password);
        MvcResult result = mockMvc.perform(post(AUTH_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession();
    }

    private Map<String, Object> buildRegisterRequest(String email, String password) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "테스트");
        map.put("phoneNo", "010-1234-5678");
        map.put("email", email);
        map.put("address", "부산시 중구");
        map.put("birthDate", LocalDate.of(1990, 1, 1).toString());
        map.put("simplePassword", password);
        map.put("accountPassword", "1234");
        map.put("signaturePassword", "654321");
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseBody(MvcResult result) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    }

    private void verifyApplicationStatus(String expectedStatus) {
        if (loanAccountNo == null) return;
        loanApplicationRepository.findByLoanAccountNo(loanAccountNo).ifPresent(app ->
                assertThat(app.getStatusCode()).isEqualTo(expectedStatus)
        );
    }
}
