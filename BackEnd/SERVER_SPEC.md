# 부산은행 비대면 대출 서버 구현 명세서

> TECHNICAL_SPEC.md의 기본 구조·패턴 원칙을 따른다.  
> 본 문서는 ERD와 요구사항 기반의 도메인·API·비즈니스로직 상세 스펙이다.

---

## 1. 도메인 패키지 구성

| 패키지 | 담당 테이블 |
|--------|-------------|
| `domain/customer` | CUSTOMER, ACCOUNT |
| `domain/product` | LOAN_PRODUCT, PRODUCT_DESCRIPTION, PRODUCT_PREFERENTIAL_RATE, PRODUCT_TERMS_BASE, PRODUCT_TERMS_HISTORY |
| `domain/loan` | LOAN_APPLICATION, LOAN_SCREENING, LOAN_CONTRACT, INCOME_INFO, SUITABILITY_RESPONSE, CUSTOMER_VERIFICATION, MYDATA_CONSENT, APPLICATION_DOCUMENT_LOG, LOAN_PREFERENTIAL_APPLIED, SIGNATURE |

```
domain/
├── customer/
│   ├── controller/   CustomerController.java
│   ├── service/      CustomerService.java, AccountService.java
│   ├── repository/   CustomerRepository.java, AccountRepository.java
│   ├── entity/       Customer.java, Account.java
│   └── dto/
│       ├── request/  RegisterRequest.java, LoginRequest.java
│       └── response/ CustomerResponse.java, AccountResponse.java
│
├── product/
│   ├── controller/   ProductController.java, AdminProductController.java
│   ├── service/      ProductService.java
│   ├── repository/   LoanProductRepository.java, ProductDescriptionRepository.java,
│   │                 ProductPreferentialRateRepository.java, ProductTermsBaseRepository.java,
│   │                 ProductTermsHistoryRepository.java
│   ├── entity/       LoanProduct.java, ProductDescription.java, ProductPreferentialRate.java,
│   │                 ProductTermsBase.java, ProductTermsHistory.java
│   └── dto/
│       ├── request/  CreateProductRequest.java, UpdateProductRequest.java
│       └── response/ ProductResponse.java, ProductDetailResponse.java, TermsResponse.java
│
└── loan/
    ├── controller/   LoanApplicationController.java, LoanScreeningController.java,
    │                 LoanContractController.java
    ├── service/      LoanApplicationService.java, SuitabilityService.java,
    │                 VerificationService.java, MydataService.java, DocumentService.java,
    │                 SignatureService.java, ScreeningService.java, ContractService.java
    ├── repository/   LoanApplicationRepository.java, LoanScreeningRepository.java,
    │                 LoanContractRepository.java, IncomeInfoRepository.java,
    │                 SuitabilityResponseRepository.java, CustomerVerificationRepository.java,
    │                 MydataConsentRepository.java, ApplicationDocumentLogRepository.java,
    │                 LoanPreferentialAppliedRepository.java, SignatureRepository.java
    ├── entity/       LoanApplication.java, LoanScreening.java, LoanContract.java,
    │                 IncomeInfo.java, SuitabilityResponse.java, CustomerVerification.java,
    │                 MydataConsent.java, ApplicationDocumentLog.java,
    │                 LoanPreferentialApplied.java, Signature.java
    └── dto/
        ├── request/  ...
        └── response/ ...
```

---

## 2. 코드값 / Enum 정의

### 2-1. LOAN_APPLICATION.status_code (CHAR(1))

| 코드 | 의미 |
|------|------|
| `1` | 신청서 생성 완료 |
| `2` | 적합성/적정성 + 본인인증 완료 |
| `3` | 마이데이터 동의 완료 |
| `4` | 서류열람/동의 + 전자서명(사전) 완료 |
| `5` | 직장·소득정보 입력 완료 |
| `6` | 대출한도 조회 완료 |
| `7` | 약관 동의 + 대출조건 입력 완료 |
| `8` | 약정 전자서명 완료 |
| `9` | 대출 실행 완료 |
| `X` | 만료 (생성 후 1일 경과) |
| `R` | 취소/거절 |

### 2-2. CUSTOMER_VERIFICATION.verify_step

| 코드 | 의미 |
|------|------|
| `SUITABILITY` | 적합성·적정성 본인인증 (간편비밀번호) |
| `MYDATA_SIGN` | 마이데이터 전자서명 |
| `LIMIT_SIGN` | 한도조회 전자서명 |
| `CONTRACT_SIGN` | 약정 본인확인 전자서명 |
| `FINAL_AUTH` | 최종 본인인증 |

### 2-3. CUSTOMER_VERIFICATION.verify_method

| 코드 | 의미 |
|------|------|
| `SIMPLE_PWD` | 간편비밀번호 |
| `SIGNATURE` | 전자서명 |

### 2-4. APPLICATION_DOCUMENT_LOG.document_type

| 코드 | 의미 |
|------|------|
| `ADMIN_INFO_REQUEST` | 본인 행정정보 제공 요구서 |
| `PERSONAL_INFO_CONSENT` | 개인(신용)정보 수집·이용·제공 동의서 |
| `MOBILE_AUTH_TERMS` | 휴대폰 본인인증 약관 |
| `PRODUCT_TERMS` | 상품 약관 |
| `PRODUCT_DESCRIPTION` | 금융상품 중요사항 설명서 |
| `BOND_CONTRACT` | 채권 전자약정서 |

### 2-5. MYDATA_CONSENT.consent_type

| 코드 | 의미 |
|------|------|
| `ADMIN_INFO` | 본인 행정정보 제공 요구 동의 |
| `MYDATA_USE` | 공공마이데이터 활용 동의 |

### 2-6. SIGNATURE.sign_step

| 코드 | 의미 |
|------|------|
| `PRE_PROCESS` | 사전 절차 전자서명 |
| `LIMIT_INQUIRY` | 한도조회 전자서명 |
| `CONTRACT` | 약정 전자서명 |
| `FINAL_AUTH` | 최종 본인인증 서명 |

### 2-7. SIGNATURE.sign_type

| 코드 | 의미 |
|------|------|
| `COMMON_CERT` | 공동인증서 |
| `SIMPLE_CERT` | 간편인증 |

### 2-8. PRODUCT_TERMS_BASE.terms_type

| 코드 | 의미 |
|------|------|
| `ADMIN_INFO_REQUEST` | 본인 행정정보 제공 요구서 |
| `PERSONAL_INFO_CONSENT` | 개인(신용)정보 동의서 |
| `MOBILE_AUTH_TERMS` | 휴대폰 본인인증 약관 |
| `PRODUCT_TERMS` | 상품 약관 |
| `PRODUCT_DESCRIPTION` | 중요사항 설명서 |
| `BOND_CONTRACT` | 채권 약정서 |

### 2-9. LOAN_CONTRACT.rate_type_code (CHAR(1))

| 코드 | 의미 |
|------|------|
| `F` | 고정금리 |
| `V` | 변동금리 |

### 2-10. PRODUCT_DESCRIPTION.attr_key (예시)

| 키 | 의미 |
|----|------|
| `LOAN_TYPE` | 대출 종류 |
| `ELIGIBLE` | 대출 대상 |
| `LOAN_LIMIT` | 대출 한도 |
| `RATE_INFO` | 금리 정보 |
| `REPAYMENT` | 상환 방법 |
| `FEE` | 수수료 |
| `CAUTION` | 유의사항 |
| `AI_SUMMARY` | AI 요약 (스케줄링 적재) |

---

## 3. 비즈니스 규칙

### 3-1. loan_account_no 채번 규칙
```
"BNK" + yyyyMMdd + 9자리 순번(0-padded)
예: BNK20260623000000001
```
- DB 시퀀스 또는 AtomicLong 기반 서버 내 채번
- 중복 방지: LOAN_APPLICATION PK 삽입 시 Unique 보장

### 3-2. 신청서 만료 처리
- `expire_at = applied_at + 1일`
- 스케줄러(@Scheduled, cron="0 0 1 * * *")로 만료 신청서 `status_code = 'X'` 일괄 처리
- API 요청 시점에도 `expire_at < now()` 이면 `BusinessException(LOAN_EXPIRED)` 반환

### 3-3. 패스워드 암호화
- `CUSTOMER.password`, `CUSTOMER.simple_password`, `CUSTOMER.simple_password`, `ACCOUNT.account_password`
  → **BCrypt** 해싱 (Spring Security `BCryptPasswordEncoder`)
- 전자서명 비밀번호 (`SIGNATURE` 관련): 별도 컬럼 없음. 로그인과 동일한 `simple_password` 재사용 또는 `CUSTOMER` 테이블에 별도 컬럼 추가 검토 필요 (현재 ERD상 단일 `simple_password` 컬럼 사용)

### 3-4. 세션 인증 (JWT 미사용)
- 로그인 성공 시 `HttpSession`에 `SESSION_CUSTOMER_ID` 저장
- 인증이 필요한 API는 `HandlerInterceptor`에서 세션 유효성 검사
- 세션 타임아웃: 30분 (application.yml `server.servlet.session.timeout=30m`)

### 3-5. 대출 한도 산출 로직 (LOAN_SCREENING)
- 입력: INCOME_INFO (연간소득, 고용형태) + 공공마이데이터 (외부 API Mock)
- 최대 한도: `MIN(연간소득 × 배수, 상품 최대한도)` — 실제 배수는 상품별 설정
- 적용 기준금리: `LOAN_PRODUCT.base_rate`
- 결과를 `LOAN_SCREENING` 저장 (`max_limit_amt`, `applied_base_rate`, `result`)

### 3-6. 우대금리 적용 (LOAN_PREFERENTIAL_APPLIED)
- 사용자가 선택한 우대금리 항목별로 `LOAN_PREFERENTIAL_APPLIED` 저장
- 최종 대출금리: `LOAN_SCREENING.applied_base_rate - SUM(LOAN_PREFERENTIAL_APPLIED.applied_rate_value)`
- 최저금리 제한(법정 최저금리) 적용

### 3-7. 대출 실행
- LOAN_CONTRACT 생성 후 ACCOUNT.balance += loan_amount (동일 트랜잭션)
- LOAN_APPLICATION.status_code = '9' 업데이트

### 3-8. 공공마이데이터 조회 (외부 연동)
- 실제 외부 API 대신 **MydataStubService** 구현 (Mock 데이터 반환)
- 인터페이스: `MydataService` → 추후 실 API 교체
- 조회 결과는 DB 저장하지 않고 응답 DTO로만 반환 (민감정보)

---

## 4. API 명세

> 모든 응답은 `ApiResponse<T>` 포맷  
> 인증 필요 API: 세션에 `SESSION_CUSTOMER_ID` 존재 여부 검사

### 4-1. 인증 (Auth)

#### POST /api/v1/auth/email/send
이메일 인증 코드 발송 (회원가입 1단계)

**Request**
```json
{ "email": "test@email.com" }
```
**Response** `200 OK`
```json
{ "success": true, "code": "SUCCESS", "message": "인증 코드가 발송되었습니다.", "data": null }
```
**비즈니스 로직**
- 6자리 랜덤 코드 생성 → 이메일 발송 (JavaMailSender)
- 코드를 인메모리 캐시(Caffeine, TTL 5분)에 email 키로 저장
- 이미 가입된 이메일이면 `DUPLICATE_EMAIL` 에러

---

#### POST /api/v1/auth/email/verify
이메일 인증 코드 확인 (회원가입 2단계)

**Request**
```json
{ "email": "test@email.com", "code": "123456" }
```
**Response** `200 OK`
```json
{ "success": true, "code": "SUCCESS", "message": "이메일 인증이 완료되었습니다.", "data": null }
```
**비즈니스 로직**
- 캐시에서 코드 조회 → 불일치 시 `INVALID_EMAIL_CODE` 에러
- 일치 시 캐시에서 삭제, 인증 완료 플래그를 세션 또는 별도 캐시에 저장 (TTL 10분)

---

#### POST /api/v1/auth/register
회원가입 (회원가입 3단계 — 이메일 인증 완료 후 호출)

**Request**
```json
{
  "name": "홍길동",
  "phoneNo": "010-1234-5678",
  "birthDate": "1990-01-01",
  "address": "부산시 중구",
  "email": "test@email.com",
  "simplePassword": "123456",
  "accountPassword": "1234",
  "signaturePassword": "654321"
}
```
**Response** `201 Created`
```json
{
  "data": {
    "customerId": 1,
    "accountNo": "110-123-456789"
  }
}
```
**비즈니스 로직**
1. 이메일 인증 완료 여부 확인 (캐시)
2. CUSTOMER 저장 (`email_verified_yn = 'Y'`, 비밀번호 BCrypt 해싱)
3. ACCOUNT 자동 생성 (account_no: "110" + 랜덤 9자리, balance=0, account_password BCrypt 해싱)
4. `signaturePassword`는 별도 저장 로직 또는 simple_password와 동일 처리 (구현팀 결정)

---

#### POST /api/v1/auth/login
로그인

**Request**
```json
{ "email": "test@email.com", "simplePassword": "123456" }
```
**Response** `200 OK`
```json
{
  "data": {
    "customerId": 1,
    "name": "홍길동",
    "accountNo": "110-123-456789"
  }
}
```
**비즈니스 로직**
- CUSTOMER 조회 → BCrypt 비밀번호 검증
- 성공 시 `HttpSession`에 `SESSION_CUSTOMER_ID = customerId` 저장
- 실패 시 `INVALID_CREDENTIALS` 에러

---

#### POST /api/v1/auth/logout
**Response** `200 OK` — 세션 무효화

---

### 4-2. 상품 조회 (Product) — 인증 필요

#### GET /api/v1/products
판매 중인 대출상품 목록 조회

**Response**
```json
{
  "data": [
    {
      "productId": 1,
      "productName": "부산은행 직장인 신용대출",
      "baseRate": 4.500,
      "loanPeriod": "최대 5년",
      "status": "SALE"
    }
  ]
}
```
- 조건: `status = 'SALE'`
- `@Cacheable("products")` 적용 (TTL 10분)

---

#### GET /api/v1/products/{productId}
대출상품 상세 조회 (LOAN_PRODUCT + PRODUCT_DESCRIPTION + PRODUCT_PREFERENTIAL_RATE)

**Response**
```json
{
  "data": {
    "productId": 1,
    "productName": "부산은행 직장인 신용대출",
    "baseRate": 4.500,
    "loanPeriod": "최대 5년",
    "descriptions": [
      { "attrKey": "LOAN_LIMIT", "attrValue": "최대 1억원", "sortOrder": 1 },
      { "attrKey": "AI_SUMMARY", "attrValue": "이 상품은 ...", "sortOrder": 99 }
    ],
    "preferentialRates": [
      {
        "preferentialId": 1,
        "conditionCode": "SALARY_TRANSFER",
        "conditionName": "급여이체",
        "rateValue": 0.200,
        "description": "급여이체 실적 시 우대"
      }
    ]
  }
}
```

---

### 4-3. 대출 신청 프로세스 (Loan) — 인증 필요

#### POST /api/v1/loans/applications
**[사전절차 Step 1]** 대출 신청서 생성

**Request**
```json
{ "productId": 1 }
```
**Response** `201 Created`
```json
{
  "data": {
    "loanAccountNo": "BNK20260623000000001",
    "expireAt": "2026-06-24T00:00:00"
  }
}
```
**비즈니스 로직**
- loan_account_no 채번 (서버 내 원자적 생성)
- `LOAN_APPLICATION` 저장 (`status_code='1'`, `expire_at = now + 1일`)
- 동일 고객의 진행 중(`status_code NOT IN ('9','X','R')`) 신청서가 있으면 `LOAN_ALREADY_IN_PROGRESS` 에러

---

#### POST /api/v1/loans/applications/{loanAccountNo}/suitability
**[사전절차 Step 2]** 적합성·적정성 고객정보 확인서 입력

**Request**
```json
{
  "responses": [
    { "questionCode": "LOAN_PURPOSE", "question": "대출 목적은?", "answer": "생활비" },
    { "questionCode": "REPAYMENT_METHOD", "question": "상환 방법은?", "answer": "원리금균등" },
    { "questionCode": "ASSET_SCALE", "question": "자산 규모는?", "answer": "1억 이하" },
    { "questionCode": "ANNUAL_INCOME", "question": "연간 소득은?", "answer": "3천만원 이하" }
  ]
}
```
**Response** `200 OK`
**비즈니스 로직**
- `SUITABILITY_RESPONSE` 다건 저장
- 신청서 상태 검증: `status_code = '1'`

---

#### POST /api/v1/loans/applications/{loanAccountNo}/verification/suitability
**[사전절차 Step 3]** 본인인증 (간편비밀번호) — 적합성/적정성 완료 처리

**Request**
```json
{ "simplePassword": "123456" }
```
**Response** `200 OK`
**비즈니스 로직**
1. 세션의 customerId로 CUSTOMER 조회 → BCrypt 비밀번호 검증
2. `CUSTOMER_VERIFICATION` 저장 (`verify_step='SUITABILITY'`, `verify_method='SIMPLE_PWD'`, `result='SUCCESS'`)
3. `LOAN_APPLICATION.status_code = '2'` 업데이트

---

#### POST /api/v1/loans/applications/{loanAccountNo}/mydata-consent
**[사전절차 Step 4]** 공공마이데이터 이용 동의

**Request**
```json
{
  "consents": [
    { "consentType": "ADMIN_INFO", "dataProvider": "행정안전부" },
    { "consentType": "MYDATA_USE", "dataProvider": "공공마이데이터포털" }
  ]
}
```
**Response** `200 OK`
**비즈니스 로직**
- `MYDATA_CONSENT` 다건 저장
- `LOAN_APPLICATION.status_code = '3'` 업데이트

---

#### POST /api/v1/loans/applications/{loanAccountNo}/documents/{documentType}/view
**[사전절차 Step 5]** 서류 열람 기록

**Path Variable**: `documentType` — ADMIN_INFO_REQUEST, PERSONAL_INFO_CONSENT 등

**Response** `200 OK`
**비즈니스 로직**
- `APPLICATION_DOCUMENT_LOG` 조회 or 생성 후 `viewed_yn='Y'`, `viewed_at=now()` 업데이트
- `terms_id`, `terms_seq`: `PRODUCT_TERMS_BASE` / `PRODUCT_TERMS_HISTORY`에서 해당 상품·문서 타입의 최신 버전 조회하여 저장

---

#### POST /api/v1/loans/applications/{loanAccountNo}/documents/{documentType}/agree
**[사전절차 Step 5]** 서류 동의

**비즈니스 로직**
- `viewed_yn='Y'` 여부 검증 (열람 전 동의 불가)
- `agreed_yn='Y'`, `agreed_at=now()` 업데이트

---

#### POST /api/v1/loans/applications/{loanAccountNo}/signatures
**[사전절차 Step 6]** 전자서명

**Request**
```json
{
  "signStep": "PRE_PROCESS",
  "signType": "COMMON_CERT",
  "tokenId": "TOKEN_ABC123",
  "originalValue": "서명 원문 내용..."
}
```
**Response** `200 OK`
```json
{ "data": { "signatureId": 10 } }
```
**비즈니스 로직**
- `SIGNATURE` 저장 (`result='SUCCESS'`, `signed_at=now()`)
- `signStep='PRE_PROCESS'` 완료 시 `LOAN_APPLICATION.status_code = '4'`

---

#### POST /api/v1/loans/applications/{loanAccountNo}/signatures/token
**[한도조회 Step 1]** 전자서명 토큰 발급

**Request**
```json
{ "signType": "COMMON_CERT" }
```
**Response** `200 OK`
```json
{ "data": { "tokenId": "TOKEN_XYZ789", "expireAt": "2026-06-23T15:30:00" } }
```
**비즈니스 로직**
- 외부 전자서명 시스템 연동 (Mock: UUID 기반 token 생성)
- `SIGNATURE` 저장 (`sign_step='LIMIT_INQUIRY'`, `token_id`, `result=null` — 아직 미완료)

---

#### POST /api/v1/loans/applications/{loanAccountNo}/income
**[한도조회 Step 2]** 직장·소득정보 입력

**Request**
```json
{
  "companyName": "부산은행",
  "jobType": "금융업",
  "employmentType": "정규직",
  "annualIncome": 45000000
}
```
**Response** `200 OK`
**비즈니스 로직**
- `INCOME_INFO` 저장
- `LOAN_APPLICATION.status_code = '5'` 업데이트

---

#### GET /api/v1/loans/applications/{loanAccountNo}/mydata
**[한도조회 Step 3]** 공공마이데이터 조회 결과 확인

**Response**
```json
{
  "data": {
    "incomeVerified": true,
    "employmentVerified": true,
    "taxInfo": { "annualIncome": 45000000, "year": 2025 },
    "nationalPension": { "monthlyPremium": 150000 }
  }
}
```
**비즈니스 로직**
- `MydataService.fetchPublicData(loanAccountNo)` 호출 (Mock 구현)
- 조회 결과는 DB 저장 안 함, 응답만 반환

---

#### POST /api/v1/loans/applications/{loanAccountNo}/screening
**[한도조회 Step 4]** 대출한도 산출

**Response**
```json
{
  "data": {
    "maxLimitAmt": 30000000,
    "appliedBaseRate": 4.500,
    "result": "APPROVED"
  }
}
```
**비즈니스 로직**
1. `INCOME_INFO` 조회
2. 마이데이터 재조회 또는 이전 세션 정보 활용
3. 한도 산출: `MIN(연간소득 × 0.5, 1억)` (임시 로직, 실 로직은 별도 정의)
4. `LOAN_SCREENING` 저장
5. `LOAN_APPLICATION.status_code = '6'` 업데이트

---

#### GET /api/v1/loans/applications/{loanAccountNo}/screening
**[한도조회 Step 4]** 대출한도 조회 결과 확인

**Response**: 위 POST 응답과 동일 (LOAN_SCREENING 조회)

---

#### GET /api/v1/products/{productId}/descriptions
**[한도조회 Step 5]** 금융상품 중요사항 설명 조회

**Response**
```json
{
  "data": {
    "productId": 1,
    "productName": "부산은행 직장인 신용대출",
    "descriptions": [
      { "attrKey": "RATE_INFO", "attrValue": "연 4.5% ~ 8.0%", "sortOrder": 1 },
      { "attrKey": "CAUTION", "attrValue": "금리 상승 시 이자 부담 증가", "sortOrder": 2 }
    ]
  }
}
```

---

#### POST /api/v1/loans/applications/{loanAccountNo}/contract/terms
**[약정 Step 1]** 약관 확인 및 전체 동의

**Request**
```json
{
  "documentTypes": ["PRODUCT_TERMS", "PRODUCT_DESCRIPTION", "BOND_CONTRACT"]
}
```
**Response** `200 OK`
**비즈니스 로직**
- 각 documentType에 대해 `APPLICATION_DOCUMENT_LOG` 저장 (`viewed_yn='Y'`, `agreed_yn='Y'`)

---

#### POST /api/v1/loans/applications/{loanAccountNo}/contract/conditions
**[약정 Step 2]** 대출 조건 입력

**Request**
```json
{
  "repaymentType": "원리금균등",
  "preferentialIds": [1, 2],
  "loanAmount": 20000000,
  "rateTypeCode": "V",
  "rateChangeCycle": "6개월",
  "loanPeriod": "36개월",
  "depositAccountNo": "110-123-456789",
  "fundPurpose": "생활비"
}
```
**Response** `200 OK`
```json
{
  "data": {
    "loanAmount": 20000000,
    "appliedBaseRate": 4.500,
    "totalPreferentialRate": 0.400,
    "finalRate": 4.100,
    "repaymentType": "원리금균등",
    "maturityDate": "2029-06-23"
  }
}
```
**비즈니스 로직**
1. `LOAN_PREFERENTIAL_APPLIED` 다건 저장 (선택된 우대금리)
2. 최종금리 계산: `applied_base_rate - SUM(rate_value)`, 최저 0.1% 이상
3. `maturity_date` = `now() + 대출기간`
4. LOAN_CONTRACT Draft 생성 (`status='CONTRACTED'` 아직 아님, 임시 저장 또는 메모리)
5. `LOAN_APPLICATION.status_code = '7'` 업데이트

---

#### GET /api/v1/loans/applications/{loanAccountNo}/contract/confirm
**[약정 Step 3]** 신청정보 최종 확인

**Response**
```json
{
  "data": {
    "productName": "부산은행 직장인 신용대출",
    "customerName": "홍길동",
    "loanAmount": 20000000,
    "finalRate": 4.100,
    "repaymentType": "원리금균등",
    "maturityDate": "2029-06-23",
    "depositAccountNo": "110-123-456789",
    "fundPurpose": "생활비",
    "preferentialRates": [
      { "conditionName": "급여이체", "rateValue": 0.200 },
      { "conditionName": "자동이체", "rateValue": 0.200 }
    ]
  }
}
```

---

#### POST /api/v1/loans/applications/{loanAccountNo}/verification/contract
**[약정 Step 4]** 약정 본인확인 (전자서명)

**Request**
```json
{
  "signStep": "CONTRACT",
  "signType": "COMMON_CERT",
  "tokenId": "TOKEN_XYZ789",
  "originalValue": "약정서 원문..."
}
```
**Response** `200 OK`
**비즈니스 로직**
- `SIGNATURE` 저장 (`sign_step='CONTRACT'`, `result='SUCCESS'`)
- `CUSTOMER_VERIFICATION` 저장 (`verify_step='CONTRACT_SIGN'`, `result='SUCCESS'`)
- `LOAN_APPLICATION.status_code = '8'` 업데이트

---

#### POST /api/v1/loans/applications/{loanAccountNo}/execute
**[약정 Step 5]** 대출 실행

**Request**
```json
{ "simplePassword": "123456" }
```
**Response** `200 OK`
```json
{
  "data": {
    "loanAccountNo": "BNK20260623000000001",
    "loanAmount": 20000000,
    "finalRate": 4.100,
    "maturityDate": "2029-06-23",
    "depositAccountNo": "110-123-456789",
    "executionDate": "2026-06-23T10:30:00"
  }
}
```
**비즈니스 로직** (단일 @Transactional)
1. 세션 고객 최종 비밀번호 검증
2. `CUSTOMER_VERIFICATION` 저장 (`verify_step='FINAL_AUTH'`)
3. `LOAN_CONTRACT` 저장 (`status='CONTRACTED'`, `execution_date=now()`)
4. `ACCOUNT.balance += loanAmount` (입금계좌 조회 → 잔액 업데이트)
5. `LOAN_APPLICATION.status_code = '9'` 업데이트

---

### 4-4. 관리자 API (Admin) — 별도 인증 처리 필요

#### GET /api/v1/admin/products
상품 목록 전체 조회 (status 무관)

#### POST /api/v1/admin/products
신규 상품 등록

**Request**
```json
{
  "productName": "부산은행 직장인 신용대출",
  "baseRate": 4.500,
  "loanPeriod": "최대 5년",
  "status": "SALE",
  "descriptions": [
    { "attrKey": "LOAN_LIMIT", "attrValue": "최대 1억원", "sortOrder": 1 }
  ],
  "preferentialRates": [
    { "conditionCode": "SALARY_TRANSFER", "conditionName": "급여이체", "rateValue": 0.200 }
  ],
  "terms": [
    { "termsType": "PRODUCT_TERMS", "termsPath": "/terms/product_terms_v1.pdf" }
  ]
}
```

#### PUT /api/v1/admin/products/{productId}
상품 수정 (기존 설명/우대금리 전체 교체)

#### DELETE /api/v1/admin/products/{productId}
상품 삭제 → `status = 'DISCONTINUED'` (소프트 삭제)

#### POST /api/v1/admin/products/{productId}/ai-summary
AI 요약 수동 트리거 (스케줄러와 동일한 로직 즉시 실행)

**비즈니스 로직**: AI API 호출 → 결과를 `PRODUCT_DESCRIPTION`의 `attr_key='AI_SUMMARY'` 레코드에 upsert

---

### 4-5. AI 요약 스케줄러

```java
@Scheduled(cron = "0 0 2 * * *")  // 매일 새벽 2시
public void summaryScheduler() {
    // LOAN_PRODUCT 전체 조회
    // 각 상품의 PRODUCT_DESCRIPTION(AI_SUMMARY 제외) 조회
    // Claude API 호출하여 요약 생성
    // PRODUCT_DESCRIPTION.attr_key='AI_SUMMARY' upsert
    // 실패 건 로그 기록
}
```

---

## 5. 에러 코드 전체 목록

| 코드 | 메시지 | HTTP |
|------|--------|------|
| `C001` | 입력값이 유효하지 않습니다 | 400 |
| `C002` | 요청한 리소스를 찾을 수 없습니다 | 404 |
| `C003` | 서버 내부 오류가 발생했습니다 | 500 |
| `C004` | 인증이 필요합니다 | 401 |
| `AUTH001` | 이미 가입된 이메일입니다 | 409 |
| `AUTH002` | 인증 코드가 유효하지 않거나 만료되었습니다 | 400 |
| `AUTH003` | 이메일 인증을 먼저 완료해주세요 | 400 |
| `AUTH004` | 아이디 또는 비밀번호가 올바르지 않습니다 | 401 |
| `LOAN001` | 대출 신청서를 찾을 수 없습니다 | 404 |
| `LOAN002` | 이미 진행 중인 대출 신청이 있습니다 | 409 |
| `LOAN003` | 만료된 대출 신청서입니다 | 400 |
| `LOAN004` | 신청 단계가 올바르지 않습니다 | 400 |
| `LOAN005` | 서류 열람 후 동의할 수 있습니다 | 400 |
| `LOAN006` | 대출 실행에 실패했습니다 | 500 |
| `PRODUCT001` | 상품을 찾을 수 없습니다 | 404 |
| `VERIFY001` | 비밀번호가 올바르지 않습니다 | 400 |
| `VERIFY002` | 전자서명 인증에 실패했습니다 | 400 |

---

## 6. 세션 인터셉터

```java
// global/config/WebMvcConfig.java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor)
            .addPathPatterns("/api/v1/**")
            .excludePathPatterns(
                "/api/v1/auth/email/**",
                "/api/v1/auth/register",
                "/api/v1/auth/login"
            );
}
```

```java
// global/interceptor/AuthInterceptor.java
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, ...) {
        Long customerId = (Long) request.getSession(false) != null
                ? request.getSession().getAttribute("SESSION_CUSTOMER_ID")
                : null;
        if (customerId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return true;
    }
}
```

---

## 7. 신청 단계 검증 유틸리티

각 API 진입 시 신청서의 현재 `status_code`가 해당 단계 직전 상태인지 검증.

```java
// loan/service/LoanApplicationService.java
public void validateStep(String loanAccountNo, String expectedStatusCode) {
    LoanApplication app = findByLoanAccountNo(loanAccountNo);
    if (app.isExpired()) throw new BusinessException(ErrorCode.LOAN_EXPIRED);
    if (!app.getStatusCode().equals(expectedStatusCode)) {
        throw new BusinessException(ErrorCode.INVALID_STEP);
    }
}
```

---

## 8. 스케줄러 목록

| 스케줄러 | cron | 동작 |
|----------|------|------|
| `LoanExpireScheduler` | `0 0 1 * * *` | 매일 새벽 1시, expire_at 경과 신청서 status_code='X' 일괄 처리 |
| `AiSummaryScheduler` | `0 0 2 * * *` | 매일 새벽 2시, 상품 설명 AI 요약 생성 및 DB 적재 |

---

## 9. 구현 우선순위 (개발 순서 권장)

1. **기반 작업**: global(에러처리, 세션 인터셉터, 공통 응답), BaseTimeEntity, DB 연결
2. **Customer 도메인**: 회원가입, 이메일 인증, 로그인
3. **Product 도메인**: 상품 CRUD (관리자), 상품 목록·상세 조회
4. **Loan 도메인 - 사전절차**: 신청서 생성 → 적합성 → 본인인증 → 마이데이터 동의 → 서류열람/동의 → 전자서명
5. **Loan 도메인 - 한도조회**: 소득입력 → 마이데이터 조회(Mock) → 한도산출
6. **Loan 도메인 - 약정/실행**: 약관동의 → 조건입력 → 본인확인 → 대출실행
7. **스케줄러**: 만료처리, AI 요약
