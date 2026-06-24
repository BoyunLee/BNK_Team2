-- ============================================================
-- 부산은행 비대면 대출 신청 서비스 DDL
-- MySQL 8.x / charset: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS bnk3
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE bnk3;

-- ────────────────────────────────────────────────────────────
-- 1. CUSTOMER
--    개인정보(name, phone_no, address, email): AES-256-CBC 암호화 저장
--    비밀번호(password, simple_password): BCrypt 해시 저장
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS CUSTOMER (
    customer_id      BIGINT          NOT NULL AUTO_INCREMENT,
    name             VARCHAR(200)    NOT NULL COMMENT '이름 (AES 암호화)',
    phone_no         VARCHAR(100)    NOT NULL COMMENT '전화번호 (AES 암호화)',
    birth_date       DATE            NOT NULL COMMENT '생년월일',
    address          VARCHAR(500)    COMMENT '주소 (AES 암호화)',
    email            VARCHAR(300)    NOT NULL COMMENT '이메일 (AES 암호화)',
    email_verified_yn CHAR(1)        NOT NULL DEFAULT 'N' COMMENT '이메일 인증 여부',
    password         VARCHAR(200)    NOT NULL COMMENT '전자서명 비밀번호 (BCrypt)',
    simple_password  VARCHAR(200)    NOT NULL COMMENT '간편인증 비밀번호 (BCrypt)',
    status           VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE / INACTIVE',
    created_at       DATETIME        NOT NULL,
    updated_at       DATETIME        NOT NULL,
    PRIMARY KEY (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 2. ACCOUNT
--    account_no: AES 암호화 후 Base64 저장 (결정적 암호화)
--    account_password: BCrypt 해시
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS ACCOUNT (
    account_no       VARCHAR(100)    NOT NULL COMMENT '계좌번호 (AES 암호화, PK)',
    customer_id      BIGINT          NOT NULL COMMENT 'CUSTOMER.customer_id',
    balance          DECIMAL(15, 2)  NOT NULL DEFAULT 0.00 COMMENT '잔액',
    account_password VARCHAR(200)    NOT NULL COMMENT '계좌 비밀번호 (BCrypt)',
    status           VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    created_at       DATETIME        NOT NULL,
    PRIMARY KEY (account_no),
    INDEX idx_account_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 3. LOAN_PRODUCT
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS LOAN_PRODUCT (
    product_id   BIGINT         NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(200)   NOT NULL COMMENT '상품명',
    base_rate    DECIMAL(5, 2)  NOT NULL COMMENT '기준금리(%)',
    loan_period  VARCHAR(100)   COMMENT '대출 기간 (예: 최대 5년)',
    status       VARCHAR(20)    NOT NULL DEFAULT 'SALE' COMMENT 'SALE / DISCONTINUED / SUSPENDED',
    category     VARCHAR(20)    COMMENT '상품 분류 (신용대출/담보대출/서민금융/보증서대출)',
    mkpd_cd      VARCHAR(20)    COMMENT '프론트 크롤링 상품코드 (FE 매핑용)',
    catchphrase  VARCHAR(300)   COMMENT '한 줄 소개 문구',
    rate_min     VARCHAR(50)    COMMENT '최저금리 (숫자 또는 수식형 문자열)',
    rate_max     VARCHAR(50)    COMMENT '최고금리 (숫자 또는 수식형 문자열)',
    created_at   DATETIME       NOT NULL,
    updated_at   DATETIME       NOT NULL,
    PRIMARY KEY (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 4. PRODUCT_DESCRIPTION
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS PRODUCT_DESCRIPTION (
    description_id BIGINT       NOT NULL AUTO_INCREMENT,
    product_id     BIGINT       NOT NULL COMMENT 'LOAN_PRODUCT.product_id',
    attr_key       VARCHAR(100) NOT NULL COMMENT 'LOAN_TYPE / ELIGIBLE / LOAN_LIMIT / RATE_INFO / REPAYMENT / FEE / CAUTION / AI_SUMMARY',
    attr_value     LONGTEXT     COMMENT '항목 값',
    sort_order     INT          NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    created_at     DATETIME     NOT NULL,
    updated_at     DATETIME     NOT NULL,
    PRIMARY KEY (description_id),
    INDEX idx_desc_product (product_id),
    INDEX idx_desc_product_key (product_id, attr_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 5. PRODUCT_PREFERENTIAL_RATE
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS PRODUCT_PREFERENTIAL_RATE (
    preferential_id BIGINT        NOT NULL AUTO_INCREMENT,
    product_id      BIGINT        NOT NULL COMMENT 'LOAN_PRODUCT.product_id',
    condition_code  VARCHAR(100)  NOT NULL COMMENT '조건 코드 (예: SALARY_TRANSFER)',
    condition_name  VARCHAR(200)  NOT NULL COMMENT '조건명 (예: 급여이체)',
    rate_value      DECIMAL(5, 2) NOT NULL COMMENT '우대금리(%)',
    description     VARCHAR(500)  COMMENT '설명',
    created_at      DATETIME      NOT NULL,
    updated_at      DATETIME      NOT NULL,
    PRIMARY KEY (preferential_id),
    INDEX idx_pref_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 6. PRODUCT_TERMS_BASE
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS PRODUCT_TERMS_BASE (
    terms_id   BIGINT       NOT NULL AUTO_INCREMENT,
    product_id BIGINT       NOT NULL COMMENT 'LOAN_PRODUCT.product_id',
    terms_type VARCHAR(100) NOT NULL COMMENT 'ADMIN_INFO_REQUEST / PERSONAL_INFO_CONSENT / MOBILE_AUTH_TERMS / PRODUCT_TERMS / PRODUCT_DESCRIPTION / BOND_CONTRACT',
    terms_path VARCHAR(500) NOT NULL COMMENT '약관 파일 경로',
    active_yn  CHAR(1)      NOT NULL DEFAULT 'Y' COMMENT '사용 여부',
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    PRIMARY KEY (terms_id),
    INDEX idx_terms_product (product_id),
    UNIQUE KEY uq_terms_product_type (product_id, terms_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 7. PRODUCT_TERMS_HISTORY
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS PRODUCT_TERMS_HISTORY (
    history_id BIGINT       NOT NULL AUTO_INCREMENT,
    terms_id   BIGINT       NOT NULL COMMENT 'PRODUCT_TERMS_BASE.terms_id',
    terms_seq  INT          NOT NULL COMMENT '버전 순번',
    terms_path VARCHAR(500) NOT NULL COMMENT '약관 파일 경로 (버전별)',
    created_at DATETIME     NOT NULL,
    PRIMARY KEY (history_id),
    INDEX idx_terms_history (terms_id, terms_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 8. LOAN_APPLICATION
--    채번: BNK + yyyyMMdd + 9자리 순번 (예: BNK20260623000000001)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS LOAN_APPLICATION (
    loan_account_no VARCHAR(30)  NOT NULL COMMENT '대출 신청 번호 (채번)',
    customer_id     BIGINT       NOT NULL COMMENT 'CUSTOMER.customer_id',
    product_id      BIGINT       NOT NULL COMMENT 'LOAN_PRODUCT.product_id',
    status_code     CHAR(1)      NOT NULL DEFAULT '1' COMMENT '1~9 / X(만료) / R(취소)',
    expire_at       DATETIME     NOT NULL COMMENT '만료일시 (생성+1일)',
    applied_at      DATETIME     NOT NULL COMMENT '신청일시',
    updated_at      DATETIME     NOT NULL,
    PRIMARY KEY (loan_account_no),
    INDEX idx_loan_customer (customer_id),
    INDEX idx_loan_status (customer_id, status_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 9. SUITABILITY_RESPONSE
--    적합성·적정성 고객정보 확인서 응답
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS SUITABILITY_RESPONSE (
    response_id     BIGINT       NOT NULL AUTO_INCREMENT,
    loan_account_no VARCHAR(30)  NOT NULL COMMENT 'LOAN_APPLICATION.loan_account_no',
    question_code   VARCHAR(100) NOT NULL COMMENT '문항 코드',
    question        VARCHAR(500) NOT NULL COMMENT '질문 내용',
    answer          VARCHAR(500) NOT NULL COMMENT '응답 내용',
    created_at      DATETIME     NOT NULL,
    PRIMARY KEY (response_id),
    INDEX idx_suitability_loan (loan_account_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 10. CUSTOMER_VERIFICATION
--     본인인증 이력
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS CUSTOMER_VERIFICATION (
    verification_id BIGINT       NOT NULL AUTO_INCREMENT,
    loan_account_no VARCHAR(30)  NOT NULL COMMENT 'LOAN_APPLICATION.loan_account_no',
    customer_id     BIGINT       NOT NULL COMMENT 'CUSTOMER.customer_id',
    verify_step     VARCHAR(50)  NOT NULL COMMENT 'SUITABILITY / MYDATA_SIGN / LIMIT_SIGN / CONTRACT_SIGN / FINAL_AUTH',
    verify_method   VARCHAR(50)  NOT NULL COMMENT 'SIMPLE_PWD / SIGNATURE',
    result          VARCHAR(20)  COMMENT 'SUCCESS / FAIL',
    verified_at     DATETIME     COMMENT '인증 완료일시',
    created_at      DATETIME     NOT NULL,
    PRIMARY KEY (verification_id),
    INDEX idx_verify_loan (loan_account_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 11. MYDATA_CONSENT
--     공공마이데이터 이용 동의
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS MYDATA_CONSENT (
    consent_id      BIGINT       NOT NULL AUTO_INCREMENT,
    loan_account_no VARCHAR(30)  NOT NULL COMMENT 'LOAN_APPLICATION.loan_account_no',
    consent_type    VARCHAR(50)  NOT NULL COMMENT 'ADMIN_INFO / MYDATA_USE',
    data_provider   VARCHAR(200) COMMENT '제공 기관명',
    consent_yn      CHAR(1)      NOT NULL DEFAULT 'Y',
    consent_at      DATETIME     NOT NULL COMMENT '동의일시',
    created_at      DATETIME     NOT NULL,
    PRIMARY KEY (consent_id),
    INDEX idx_consent_loan (loan_account_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 12. APPLICATION_DOCUMENT_LOG
--     서류 열람/동의 이력
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS APPLICATION_DOCUMENT_LOG (
    log_id          BIGINT       NOT NULL AUTO_INCREMENT,
    loan_account_no VARCHAR(30)  NOT NULL COMMENT 'LOAN_APPLICATION.loan_account_no',
    document_type   VARCHAR(100) NOT NULL COMMENT 'ADMIN_INFO_REQUEST / PERSONAL_INFO_CONSENT / MOBILE_AUTH_TERMS / PRODUCT_TERMS / PRODUCT_DESCRIPTION / BOND_CONTRACT',
    terms_id        BIGINT       COMMENT 'PRODUCT_TERMS_BASE.terms_id',
    terms_seq       INT          COMMENT '약관 버전',
    viewed_yn       CHAR(1)      NOT NULL DEFAULT 'N' COMMENT '열람 여부',
    viewed_at       DATETIME     COMMENT '열람일시',
    agreed_yn       CHAR(1)      NOT NULL DEFAULT 'N' COMMENT '동의 여부',
    agreed_at       DATETIME     COMMENT '동의일시',
    created_at      DATETIME     NOT NULL,
    PRIMARY KEY (log_id),
    INDEX idx_doclog_loan (loan_account_no),
    UNIQUE KEY uq_doclog_loan_type (loan_account_no, document_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 13. SIGNATURE
--     전자서명 이력
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS SIGNATURE (
    signature_id    BIGINT       NOT NULL AUTO_INCREMENT,
    loan_account_no VARCHAR(30)  NOT NULL COMMENT 'LOAN_APPLICATION.loan_account_no',
    customer_id     BIGINT       NOT NULL COMMENT 'CUSTOMER.customer_id',
    sign_step       VARCHAR(50)  COMMENT 'PRE_PROCESS / LIMIT_INQUIRY / CONTRACT / FINAL_AUTH',
    sign_type       VARCHAR(50)  COMMENT 'COMMON_CERT / SIMPLE_CERT',
    token_id        VARCHAR(200) COMMENT '전자서명 토큰 ID',
    original_value  TEXT         COMMENT '서명 원문',
    result          VARCHAR(20)  COMMENT 'SUCCESS / FAIL (null=미완료)',
    signed_at       DATETIME     COMMENT '서명 완료일시',
    created_at      DATETIME     NOT NULL,
    PRIMARY KEY (signature_id),
    INDEX idx_sign_loan (loan_account_no),
    INDEX idx_sign_token (token_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 14. INCOME_INFO
--     직장·소득정보
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS INCOME_INFO (
    income_id       BIGINT       NOT NULL AUTO_INCREMENT,
    loan_account_no VARCHAR(30)  NOT NULL COMMENT 'LOAN_APPLICATION.loan_account_no',
    customer_id     BIGINT       NOT NULL COMMENT 'CUSTOMER.customer_id',
    company_name    VARCHAR(200) COMMENT '회사명',
    job_type        VARCHAR(100) COMMENT '직업 유형',
    employment_type VARCHAR(100) COMMENT '고용 형태',
    annual_income   BIGINT       COMMENT '연간 소득 (원)',
    created_at      DATETIME     NOT NULL,
    PRIMARY KEY (income_id),
    INDEX idx_income_loan (loan_account_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 15. LOAN_SCREENING
--     대출 심사 결과
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS LOAN_SCREENING (
    screening_id    BIGINT        NOT NULL AUTO_INCREMENT,
    loan_account_no VARCHAR(30)   NOT NULL COMMENT 'LOAN_APPLICATION.loan_account_no',
    max_limit_amt   BIGINT        COMMENT '최대 대출 한도 (원)',
    applied_base_rate DECIMAL(5,2) COMMENT '적용 기준금리(%)',
    result          VARCHAR(20)   COMMENT 'APPROVED / REJECTED',
    created_at      DATETIME      NOT NULL,
    PRIMARY KEY (screening_id),
    UNIQUE KEY uq_screening_loan (loan_account_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 16. LOAN_PREFERENTIAL_APPLIED
--     적용된 우대금리 내역
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS LOAN_PREFERENTIAL_APPLIED (
    applied_id        BIGINT        NOT NULL AUTO_INCREMENT,
    loan_account_no   VARCHAR(30)   NOT NULL COMMENT 'LOAN_APPLICATION.loan_account_no',
    preferential_id   BIGINT        NOT NULL COMMENT 'PRODUCT_PREFERENTIAL_RATE.preferential_id',
    applied_rate_value DECIMAL(5,2) NOT NULL COMMENT '적용 우대금리(%)',
    created_at        DATETIME      NOT NULL,
    PRIMARY KEY (applied_id),
    INDEX idx_pref_applied_loan (loan_account_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- 17. LOAN_CONTRACT
--     대출 약정 및 실행 정보
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS LOAN_CONTRACT (
    contract_id      BIGINT        NOT NULL AUTO_INCREMENT,
    loan_account_no  VARCHAR(30)   NOT NULL COMMENT 'LOAN_APPLICATION.loan_account_no',
    customer_id      BIGINT        NOT NULL COMMENT 'CUSTOMER.customer_id',
    loan_amount      BIGINT        COMMENT '대출 금액 (원)',
    rate_type_code   CHAR(1)       COMMENT 'F(고정) / V(변동)',
    final_rate       DECIMAL(5,2)  COMMENT '최종 적용금리(%)',
    repayment_type   VARCHAR(100)  COMMENT '상환 방식',
    rate_change_cycle VARCHAR(50)  COMMENT '리프라이싱 주기 (변동금리)',
    loan_period      VARCHAR(50)   COMMENT '대출 기간',
    maturity_date    DATE          COMMENT '만기일',
    deposit_account_no VARCHAR(100) COMMENT '입금 계좌번호',
    fund_purpose     VARCHAR(200)  COMMENT '자금 용도',
    status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / CONTRACTED',
    execution_date   DATETIME      COMMENT '대출 실행일시',
    created_at       DATETIME      NOT NULL,
    PRIMARY KEY (contract_id),
    UNIQUE KEY uq_contract_loan (loan_account_no),
    INDEX idx_contract_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
