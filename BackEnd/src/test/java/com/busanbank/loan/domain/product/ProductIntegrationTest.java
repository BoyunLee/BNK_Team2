package com.busanbank.loan.domain.product;

import com.busanbank.loan.domain.product.repository.LoanProductRepository;
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
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {

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

    private MockHttpSession session;

    private static final String AUTH_URL = "/api/v1/auth";
    private static final String PRODUCT_URL = "/api/v1/products";
    private static final String ADMIN_URL = "/api/v1/admin/products";

    @BeforeEach
    void setUp() throws Exception {
        cacheManager.getCache("emailCode").clear();
        cacheManager.getCache("emailVerified").clear();
        cacheManager.getCache("products").clear();
        cacheManager.getCache("productDetail").clear();

        // Register a customer and log in to get an authenticated session
        String email = "product-test@bank.com";
        cacheManager.getCache("emailVerified").put(email, "true");

        Map<String, Object> registerReq = buildRegisterRequest(email, "123456");
        mockMvc.perform(post(AUTH_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        session = loginCustomer(email, "123456");
    }

    // ──────────────── GET /api/v1/products ────────────────

    @Test
    @DisplayName("상품 목록 조회 - 인증 없이 접근 시 401")
    void getProductsOnSale_unauthorized() throws Exception {
        mockMvc.perform(get(PRODUCT_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("상품 목록 조회 - 성공 (상품 없을 때 빈 배열)")
    void getProductsOnSale_emptyList() throws Exception {
        mockMvc.perform(get(PRODUCT_URL).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    // ──────────────── POST /api/v1/admin/products ────────────────

    @Test
    @DisplayName("상품 생성 - 성공")
    void createProduct_success() throws Exception {
        Map<String, Object> request = buildCreateProductRequest("테스트 대출");

        mockMvc.perform(post(ADMIN_URL)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.productName").value("테스트 대출"))
                .andExpect(jsonPath("$.data.baseRate").value(4.5))
                .andExpect(jsonPath("$.data.loanPeriod").value("최대 5년"))
                .andExpect(jsonPath("$.data.status").value("SALE"))
                .andExpect(jsonPath("$.data.descriptions[0].attrKey").value("LOAN_LIMIT"));
    }

    @Test
    @DisplayName("상품 생성 후 목록 조회 - 생성된 상품 포함")
    void createProduct_thenGetList() throws Exception {
        // Create product
        Map<String, Object> createReq = buildCreateProductRequest("조회용 대출");
        mockMvc.perform(post(ADMIN_URL)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated());

        // Evict cache so list reflects DB state
        cacheManager.getCache("products").clear();

        // Get product list
        mockMvc.perform(get(PRODUCT_URL).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].productName").value("조회용 대출"));
    }

    // ──────────────── GET /api/v1/products/{productId} ────────────────

    @Test
    @DisplayName("상품 상세 조회 - 성공")
    void getProductDetail_success() throws Exception {
        // Create product first
        Map<String, Object> createReq = buildCreateProductRequest("상세 조회 대출");
        MvcResult createResult = mockMvc.perform(post(ADMIN_URL)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long productId = extractProductId(createResult);

        mockMvc.perform(get(PRODUCT_URL + "/" + productId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("상세 조회 대출"))
                .andExpect(jsonPath("$.data.descriptions").isArray())
                .andExpect(jsonPath("$.data.preferentialRates").isArray());
    }

    // ──────────────── PUT /api/v1/admin/products/{productId} ────────────────

    @Test
    @DisplayName("상품 수정 - 성공")
    void updateProduct_success() throws Exception {
        // Create product
        Map<String, Object> createReq = buildCreateProductRequest("원래 이름");
        MvcResult createResult = mockMvc.perform(post(ADMIN_URL)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();
        Long productId = extractProductId(createResult);

        // Update product
        Map<String, Object> updateReq = buildUpdateProductRequest("수정된 이름");
        mockMvc.perform(put(ADMIN_URL + "/" + productId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("수정된 이름"));
    }

    // ──────────────── DELETE /api/v1/admin/products/{productId} ────────────────

    @Test
    @DisplayName("상품 삭제 - 성공 (status DISCONTINUED)")
    void deleteProduct_success() throws Exception {
        // Create product
        Map<String, Object> createReq = buildCreateProductRequest("삭제 대상 대출");
        MvcResult createResult = mockMvc.perform(post(ADMIN_URL)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();
        Long productId = extractProductId(createResult);

        // Delete product
        mockMvc.perform(delete(ADMIN_URL + "/" + productId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify status is DISCONTINUED
        loanProductRepository.findById(productId).ifPresent(p ->
                org.assertj.core.api.Assertions.assertThat(p.getStatus()).isEqualTo("DISCONTINUED")
        );
    }

    // ──────────────── helpers ────────────────

    private MockHttpSession loginCustomer(String email, String simplePassword) throws Exception {
        Map<String, String> loginReq = Map.of("email", email, "simplePassword", simplePassword);
        MvcResult result = mockMvc.perform(post(AUTH_URL + "/login")
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
        map.put("signaturePassword", "654321");
        return map;
    }

    private Map<String, Object> buildCreateProductRequest(String productName) {
        Map<String, Object> desc = Map.of("attrKey", "LOAN_LIMIT", "attrValue", "최대 1억원", "sortOrder", 1);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("productName", productName);
        map.put("baseRate", 4.5);
        map.put("loanPeriod", "최대 5년");
        map.put("status", "SALE");
        map.put("descriptions", List.of(desc));
        return map;
    }

    private Map<String, Object> buildUpdateProductRequest(String productName) {
        Map<String, Object> desc = Map.of("attrKey", "LOAN_LIMIT", "attrValue", "최대 2억원", "sortOrder", 1);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("productName", productName);
        map.put("baseRate", 5.0);
        map.put("loanPeriod", "최대 10년");
        map.put("status", "SALE");
        map.put("descriptions", List.of(desc));
        return map;
    }

    @SuppressWarnings("unchecked")
    private Long extractProductId(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        Map<String, Object> body = objectMapper.readValue(content, Map.class);
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        return ((Number) data.get("productId")).longValue();
    }
}
