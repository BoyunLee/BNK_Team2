# 부산은행 비대면 대출 신청 API 명세서

> **Base URL**: `http://localhost:8080`  
> **Content-Type**: `application/json`  
> **인증 방식**: HTTP Session (`SESSION_CUSTOMER_ID`)

---

## 공통 응답 형식

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 처리되었습니다.",
  "data": { }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `success` | boolean | 성공 여부 |
| `code` | string | 결과 코드 (`SUCCESS` 또는 에러 코드) |
| `message` | string | 결과 메시지 |
| `data` | object | 응답 데이터 (없으면 생략) |

**에러 공통 형식**
```json
{
  "success": false,
  "code": "AUTH004",
  "message": "아이디 또는 비밀번호가 올바르지 않습니다"
}
```

---

## 에러 코드

| 코드 | 메시지 | HTTP |
|------|--------|------|
| `C001` | 입력값이 유효하지 않습니다 | 400 |
| `C002` | 요청한 리소스를 찾을 수 없습니다 | 404 |
| `C003` | 서버 내부 오류가 발생했습니다 | 500 |
| `C004` | 인증이 필요합니다 | 401 |
| `C005` | 접근 권한이 없습니다 | 403 |
| `AUTH001` | 이미 가입된 이메일입니다 | 409 |
| `AUTH002` | 인증 코드가 유효하지 않거나 만료되었습니다 | 400 |
| `AUTH003` | 이메일 인증을 먼저 완료해주세요 | 400 |
| `AUTH004` | 아이디 또는 비밀번호가 올바르지 않습니다 | 401 |
| `AUTH005` | 세션이 만료되었습니다. 다시 로그인해주세요 | 401 |
| `CU001` | 고객 정보를 찾을 수 없습니다 | 404 |
| `CU002` | 계좌 정보를 찾을 수 없습니다 | 404 |
| `CU003` | 비활성 상태의 고객입니다 | 403 |
| `PRODUCT001` | 상품을 찾을 수 없습니다 | 404 |
| `PRODUCT002` | 판매 중이 아닌 상품입니다 | 400 |
| `PRODUCT003` | 약관 정보를 찾을 수 없습니다 | 404 |
| `LOAN001` | 대출 신청서를 찾을 수 없습니다 | 404 |
| `LOAN002` | 이미 진행 중인 대출 신청이 있습니다 | 409 |
| `LOAN003` | 만료된 대출 신청서입니다 | 400 |
| `LOAN004` | 현재 신청 단계에서 수행할 수 없는 요청입니다 | 400 |
| `LOAN005` | 서류 열람 후 동의할 수 있습니다 | 400 |
| `LOAN006` | 대출 실행에 실패했습니다 | 500 |
| `LOAN007` | 신청 금액이 승인 한도를 초과합니다 | 400 |
| `LOAN008` | 대출 심사 결과를 찾을 수 없습니다 | 404 |
| `LOAN009` | 대출 심사가 거절되었습니다 | 400 |
| `VERIFY001` | 비밀번호가 올바르지 않습니다 | 400 |
| `VERIFY002` | 전자서명 인증에 실패했습니다 | 400 |
| `VERIFY003` | 전자서명 토큰이 유효하지 않습니다 | 400 |
| `VERIFY004` | 이미 완료된 인증입니다 | 409 |

---

## 1. 인증 (Auth)

> 인증 불필요 경로 — 세션 없이 호출 가능

---

### 1.1 이메일 인증 코드 발송

```
POST /api/v1/auth/email/send
```

**Request**
```json
{ "email": "test@email.com" }
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "인증 코드가 발송되었습니다."
}
```

**Response** `409 Conflict` — 이미 가입된 이메일
```json
{
  "success": false,
  "code": "AUTH001",
  "message": "이미 가입된 이메일입니다"
}
```

**Response** `400 Bad Request` — 입력값 오류 (이메일 형식 불일치 등)
```json
{
  "success": false,
  "code": "C001",
  "message": "입력값이 유효하지 않습니다"
}
```

> **로컬 개발**: SMTP 인증 실패 시 메일 발송을 건너뛰고 서버 콘솔에 인증 코드를 출력합니다.  
> 실제 메일 수신이 필요하면 `application-local.yml`에 Gmail 앱 비밀번호를 설정하세요.

---

### 1.2 이메일 인증 코드 확인

```
POST /api/v1/auth/email/verify
```

**Request**
```json
{ "email": "test@email.com", "code": "123456" }
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "이메일 인증이 완료되었습니다."
}
```

**Response** `400 Bad Request` — 코드 불일치 또는 만료(5분)
```json
{
  "success": false,
  "code": "AUTH002",
  "message": "인증 코드가 유효하지 않거나 만료되었습니다"
}
```

---

### 1.3 회원가입

```
POST /api/v1/auth/register
```

이메일 인증 완료 후 호출해야 합니다. 계좌가 자동 생성됩니다.

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

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `name` | string | ✅ | 이름 |
| `phoneNo` | string | ✅ | 휴대전화번호 (예: `010-1234-5678`) |
| `birthDate` | string | ✅ | 생년월일 — **반드시 `yyyy-MM-dd` 형식** (예: `1990-01-01`) |
| `address` | string | ✅ | 주소 |
| `email` | string | ✅ | 이메일 |
| `simplePassword` | string | ✅ | 간편인증 비밀번호 6자리 (로그인·본인인증에 사용) |
| `accountPassword` | string | ✅ | 계좌 비밀번호 4자리 |
| `signaturePassword` | string | ✅ | 전자서명 비밀번호 6자리 |

> `birthDate`는 서버에서 `LocalDate.parse()`로 파싱합니다. 형식이 다르면 `C001 400` 에러가 반환됩니다.

**Response** `201 Created`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "회원가입이 완료되었습니다.",
  "data": {
    "customerId": 1,
    "accountNo": "110123456789"
  }
}
```

**Response** `400 Bad Request` — 이메일 인증 미완료
```json
{
  "success": false,
  "code": "AUTH003",
  "message": "이메일 인증을 먼저 완료해주세요"
}
```

**Response** `409 Conflict` — 중복 이메일
```json
{
  "success": false,
  "code": "AUTH001",
  "message": "이미 가입된 이메일입니다"
}
```

**Response** `400 Bad Request` — `birthDate` 형식 오류 등 입력값 오류
```json
{
  "success": false,
  "code": "C001",
  "message": "입력값이 유효하지 않습니다"
}
```

---

### 1.4 로그인

```
POST /api/v1/auth/login
```

**Request**
```json
{ "email": "test@email.com", "simplePassword": "123456" }
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "로그인되었습니다.",
  "data": {
    "customer": {
      "customerId": 1,
      "name": "홍**",
      "phoneNo": "010-****-5678",
      "birthDate": "1990-01-01",
      "address": "부산시 중구 ***",
      "email": "te**@email.com",
      "emailVerifiedYn": "Y",
      "status": "ACTIVE"
    },
    "accountNo": "110123456789"
  }
}
```

> 응답의 `name`, `phoneNo`, `address`, `email` 은 마스킹된 값입니다.

**Response** `401 Unauthorized` — 존재하지 않는 이메일 또는 비밀번호 불일치
```json
{
  "success": false,
  "code": "AUTH004",
  "message": "아이디 또는 비밀번호가 올바르지 않습니다"
}
```

**Response** `403 Forbidden` — 비활성 계정
```json
{
  "success": false,
  "code": "CU003",
  "message": "비활성 상태의 고객입니다"
}
```

---

### 1.5 로그아웃

```
POST /api/v1/auth/logout
```

세션을 무효화합니다.

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "로그아웃되었습니다."
}
```

**Response** `401 Unauthorized` — 세션 없음/만료
```json
{
  "success": false,
  "code": "AUTH005",
  "message": "세션이 만료되었습니다. 다시 로그인해주세요"
}
```

---

## 2. 상품 조회 (Product)

> 세션 인증 필요

---

### 2.1 판매 중인 상품 목록 조회

```
GET /api/v1/products
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 처리되었습니다.",
  "data": [
    {
      "productId": 1,
      "productName": "부산은행 직장인 신용대출",
      "baseRate": 4.5,
      "loanPeriod": "최대 5년",
      "status": "SALE"
    }
  ]
}
```

> `status = SALE` 상품만 반환. 결과는 10분간 캐싱됩니다.

**Response** `401 Unauthorized` — 세션 없음/만료
```json
{
  "success": false,
  "code": "AUTH005",
  "message": "세션이 만료되었습니다. 다시 로그인해주세요"
}
```

---

### 2.2 상품 상세 조회

```
GET /api/v1/products/{productId}
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 처리되었습니다.",
  "data": {
    "productId": 1,
    "productName": "부산은행 직장인 신용대출",
    "baseRate": 4.5,
    "loanPeriod": "최대 5년",
    "status": "SALE",
    "descriptions": [
      { "attrKey": "LOAN_LIMIT", "attrValue": "최대 1억원", "sortOrder": 1 },
      { "attrKey": "AI_SUMMARY", "attrValue": "이 상품은 ...", "sortOrder": 99 }
    ],
    "preferentialRates": [
      {
        "preferentialId": 1,
        "conditionCode": "SALARY_TRANSFER",
        "conditionName": "급여이체",
        "rateValue": 0.2,
        "description": "급여이체 실적 시 우대"
      }
    ]
  }
}
```

**Response** `404 Not Found` — 존재하지 않는 상품
```json
{
  "success": false,
  "code": "PRODUCT001",
  "message": "상품을 찾을 수 없습니다"
}
```

---

### 2.3 상품 중요사항 설명 조회

```
GET /api/v1/products/{productId}/descriptions
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 처리되었습니다.",
  "data": [
    { "attrKey": "RATE_INFO", "attrValue": "연 4.5% ~ 8.0%", "sortOrder": 1 },
    { "attrKey": "CAUTION",   "attrValue": "금리 상승 시 이자 부담 증가", "sortOrder": 2 }
  ]
}
```

**Response** `404 Not Found` — 존재하지 않는 상품
```json
{
  "success": false,
  "code": "PRODUCT001",
  "message": "상품을 찾을 수 없습니다"
}
```

---

## 3. 대출 신청 사전 절차

> 세션 인증 필요  
> `{loanAccountNo}` 형식: `BNK20260623000000001`

### 신청서 상태 코드

| 코드 | 상태 |
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

---

### 3.1 대출 신청서 생성

```
POST /api/v1/loans/applications
```

신청서 번호(채번)를 생성하고 신청서를 등록합니다. 신청서는 생성 후 1일 간 유효합니다.

**Request**
```json
{ "productId": 1 }
```

**Response** `201 Created`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "대출 신청서가 생성되었습니다.",
  "data": {
    "loanAccountNo": "BNK20260623000000001",
    "expireAt": "2026-06-24T00:00:00"
  }
}
```

**Response** `404 Not Found` — 존재하지 않는 상품
```json
{
  "success": false,
  "code": "PRODUCT001",
  "message": "상품을 찾을 수 없습니다"
}
```

**Response** `400 Bad Request` — 판매 중이 아닌 상품
```json
{
  "success": false,
  "code": "PRODUCT002",
  "message": "판매 중이 아닌 상품입니다"
}
```

**Response** `409 Conflict` — 이미 진행 중인 신청서 존재
```json
{
  "success": false,
  "code": "LOAN002",
  "message": "이미 진행 중인 대출 신청이 있습니다"
}
```

---

### 3.2 본인인증 (적합성·적정성 완료 처리)

```
POST /api/v1/loans/applications/{loanAccountNo}/verification/suitability
```

간편비밀번호로 본인인증을 수행하고, 완료 시 신청서 상태를 `2`로 변경합니다.

> 신청서 상태 `1` 필요

**Request**
```json
{ "simplePassword": "123456" }
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "본인인증이 완료되었습니다."
}
```

**Response** `400 Bad Request` — 비밀번호 불일치
```json
{
  "success": false,
  "code": "VERIFY001",
  "message": "비밀번호가 올바르지 않습니다"
}
```

**Response** `400 Bad Request` — 신청서 상태가 `1`이 아닌 경우
```json
{
  "success": false,
  "code": "LOAN004",
  "message": "현재 신청 단계에서 수행할 수 없는 요청입니다"
}
```

**Response** `404 Not Found` — 신청서 없음
```json
{
  "success": false,
  "code": "LOAN001",
  "message": "대출 신청서를 찾을 수 없습니다"
}
```

---

### 3.3 공공마이데이터 이용 동의

```
POST /api/v1/loans/applications/{loanAccountNo}/mydata-consent
```

> 신청서 상태 `2` 필요 → 완료 시 상태 `3`으로 변경

**Request**
```json
{
  "consents": [
    { "consentType": "ADMIN_INFO", "dataProvider": "행정안전부" },
    { "consentType": "MYDATA_USE", "dataProvider": "공공마이데이터포털" }
  ]
}
```

| `consentType` | 설명 |
|---------------|------|
| `ADMIN_INFO` | 본인 행정정보 제공 요구 동의 |
| `MYDATA_USE` | 공공마이데이터 활용 동의 |

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "공공마이데이터 이용 동의가 완료되었습니다."
}
```

**Response** `400 Bad Request` — 신청서 상태가 `2`가 아닌 경우
```json
{
  "success": false,
  "code": "LOAN004",
  "message": "현재 신청 단계에서 수행할 수 없는 요청입니다"
}
```

**Response** `404 Not Found` — 신청서 없음
```json
{
  "success": false,
  "code": "LOAN001",
  "message": "대출 신청서를 찾을 수 없습니다"
}
```

---

### 3.4 서류 열람 기록

```
POST /api/v1/loans/applications/{loanAccountNo}/documents/{documentType}/view?productId={productId}
```

**Path Variable** `documentType`

| 값 | 설명 |
|----|------|
| `ADMIN_INFO_REQUEST` | 본인 행정정보 제공 요구서 |
| `PERSONAL_INFO_CONSENT` | 개인(신용)정보 수집·이용·제공 동의서 |
| `MOBILE_AUTH_TERMS` | 휴대폰 본인인증 약관 |
| `PRODUCT_TERMS` | 상품 약관 |
| `PRODUCT_DESCRIPTION` | 금융상품 중요사항 설명서 |
| `BOND_CONTRACT` | 채권 전자약정서 |

**Query Parameter**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `productId` | Long | ✅ | 상품 ID (약관 버전 추적용) |

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "서류 열람 기록이 저장되었습니다.",
  "data": {
    "documentType": "PRODUCT_TERMS",
    "viewedAt": "2026-06-23T11:20:00"
  }
}
```

**Response** `404 Not Found` — 약관 정보 없음
```json
{
  "success": false,
  "code": "PRODUCT003",
  "message": "약관 정보를 찾을 수 없습니다"
}
```

**Response** `404 Not Found` — 신청서 없음
```json
{
  "success": false,
  "code": "LOAN001",
  "message": "대출 신청서를 찾을 수 없습니다"
}
```

---

### 3.5 서류 동의

```
POST /api/v1/loans/applications/{loanAccountNo}/documents/{documentType}/agree
```

열람(`view`) 후에만 동의 가능합니다.

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "서류 동의가 완료되었습니다.",
  "data": {
    "documentType": "PRODUCT_TERMS",
    "agreedAt": "2026-06-23T11:21:00"
  }
}
```

**Response** `400 Bad Request` — 열람 이력 없음
```json
{
  "success": false,
  "code": "LOAN005",
  "message": "서류 열람 후 동의할 수 있습니다"
}
```

**Response** `404 Not Found` — 신청서 없음
```json
{
  "success": false,
  "code": "LOAN001",
  "message": "대출 신청서를 찾을 수 없습니다"
}
```

---

### 3.6 전자서명 (사전 절차)

```
POST /api/v1/loans/applications/{loanAccountNo}/signatures
```

`signStep = PRE_PROCESS` 완료 시 신청서 상태를 `4`로 변경합니다.

**Request**
```json
{
  "signStep": "PRE_PROCESS",
  "signType": "COMMON_CERT",
  "tokenId": "TOKEN_ABC123",
  "originalValue": "서명 원문 내용..."
}
```

| `signStep` | 설명 |
|------------|------|
| `PRE_PROCESS` | 사전 절차 전자서명 |
| `LIMIT_INQUIRY` | 한도조회 전자서명 |
| `CONTRACT` | 약정 전자서명 |
| `FINAL_AUTH` | 최종 본인인증 서명 |

| `signType` | 설명 |
|------------|------|
| `COMMON_CERT` | 공동인증서 |
| `SIMPLE_CERT` | 간편인증 |

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "전자서명이 완료되었습니다.",
  "data": { "signatureId": 10 }
}
```

**Response** `400 Bad Request` — 전자서명 인증 실패
```json
{
  "success": false,
  "code": "VERIFY002",
  "message": "전자서명 인증에 실패했습니다"
}
```

**Response** `400 Bad Request` — 전자서명 토큰이 유효하지 않음
```json
{
  "success": false,
  "code": "VERIFY003",
  "message": "전자서명 토큰이 유효하지 않습니다"
}
```

**Response** `409 Conflict` — 이미 완료된 인증(동일 signStep 중복 요청)
```json
{
  "success": false,
  "code": "VERIFY004",
  "message": "이미 완료된 인증입니다"
}
```

---

## 4. 대출 한도 조회

> 세션 인증 필요

---

### 4.1 전자서명 토큰 발급

```
POST /api/v1/loans/applications/{loanAccountNo}/signatures/token
```

> 신청서 상태 `4` 필요

**Request**
```json
{ "signType": "COMMON_CERT" }
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "전자서명 토큰이 발급되었습니다.",
  "data": {
    "tokenId": "TOKEN_XYZ789",
    "expireAt": "2026-06-23T15:30:00"
  }
}
```

> 토큰 유효시간: 30분

**Response** `400 Bad Request` — 신청서 상태가 `4`가 아닌 경우
```json
{
  "success": false,
  "code": "LOAN004",
  "message": "현재 신청 단계에서 수행할 수 없는 요청입니다"
}
```

---

### 4.2 직장·소득정보 입력

```
POST /api/v1/loans/applications/{loanAccountNo}/income
```

> 신청서 상태 `4` 필요 → 완료 시 상태 `5`로 변경

**Request**
```json
{
  "companyName": "부산은행",
  "jobType": "금융업",
  "employmentType": "정규직",
  "annualIncome": 45000000
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `companyName` | string | 회사명 |
| `jobType` | string | 직업 유형 |
| `employmentType` | string | 고용 형태 |
| `annualIncome` | Long | 연간 소득 (원) |

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "직장·소득정보가 등록되었습니다."
}
```

**Response** `400 Bad Request` — 신청서 상태가 `4`가 아닌 경우
```json
{
  "success": false,
  "code": "LOAN004",
  "message": "현재 신청 단계에서 수행할 수 없는 요청입니다"
}
```

**Response** `400 Bad Request` — 입력값 오류 (`annualIncome` 음수 등)
```json
{
  "success": false,
  "code": "C001",
  "message": "입력값이 유효하지 않습니다"
}
```

---

### 4.3 공공마이데이터 조회

```
GET /api/v1/loans/applications/{loanAccountNo}/mydata
```

Mock 데이터를 반환합니다. 조회 결과는 DB에 저장되지 않습니다.

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 처리되었습니다.",
  "data": {
    "incomeVerified": true,
    "employmentVerified": true,
    "taxInfo": {
      "annualIncome": 45000000,
      "year": 2025
    },
    "nationalPension": {
      "monthlyPremium": 150000
    }
  }
}
```

**Response** `404 Not Found` — 신청서 없음
```json
{
  "success": false,
  "code": "LOAN001",
  "message": "대출 신청서를 찾을 수 없습니다"
}
```

---

### 4.4 대출 한도 산출

```
POST /api/v1/loans/applications/{loanAccountNo}/screening
```

> 신청서 상태 `5` 필요 → 완료 시 상태 `6`으로 변경

한도 산출 로직: `MIN(연간소득 × 0.5, 100,000,000)`

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "대출 한도 산출이 완료되었습니다.",
  "data": {
    "maxLimitAmt": 22500000,
    "appliedBaseRate": 4.5,
    "result": "APPROVED"
  }
}
```

**Response** `200 OK` — 심사 거절 (정상 응답이지만 결과가 거절인 경우)
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "대출 한도 산출이 완료되었습니다.",
  "data": {
    "maxLimitAmt": 0,
    "appliedBaseRate": 4.5,
    "result": "REJECTED"
  }
}
```

**Response** `400 Bad Request` — 신청서 상태가 `5`가 아닌 경우
```json
{
  "success": false,
  "code": "LOAN004",
  "message": "현재 신청 단계에서 수행할 수 없는 요청입니다"
}
```

---

### 4.5 대출 한도 조회 결과 확인

```
GET /api/v1/loans/applications/{loanAccountNo}/screening
```

**Response** `200 OK` — `POST /screening` 응답과 동일
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 처리되었습니다.",
  "data": {
    "maxLimitAmt": 22500000,
    "appliedBaseRate": 4.5,
    "result": "APPROVED"
  }
}
```

**Response** `404 Not Found` — 심사 결과 없음
```json
{
  "success": false,
  "code": "LOAN008",
  "message": "대출 심사 결과를 찾을 수 없습니다"
}
```

---

## 5. 대출 약정 및 실행

> 세션 인증 필요

---

### 5.1 약관 확인 및 전체 동의

```
POST /api/v1/loans/applications/{loanAccountNo}/contract/terms
```

> 신청서 상태 `6` 필요

**Request**
```json
{
  "documentTypes": ["PRODUCT_TERMS", "PRODUCT_DESCRIPTION", "BOND_CONTRACT"]
}
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "약관 동의가 완료되었습니다."
}
```

**Response** `400 Bad Request` — 신청서 상태가 `6`이 아닌 경우
```json
{
  "success": false,
  "code": "LOAN004",
  "message": "현재 신청 단계에서 수행할 수 없는 요청입니다"
}
```

**Response** `404 Not Found` — 약관 정보 없음
```json
{
  "success": false,
  "code": "PRODUCT003",
  "message": "약관 정보를 찾을 수 없습니다"
}
```

---

### 5.2 대출 조건 입력

```
POST /api/v1/loans/applications/{loanAccountNo}/contract/conditions
```

> 신청서 상태 `6` 필요 → 완료 시 상태 `7`로 변경

**Request**
```json
{
  "repaymentType": "원리금균등",
  "rateTypeCode": "V",
  "rateChangeCycle": "6개월",
  "loanPeriod": "36개월",
  "depositAccountNo": "110123456789",
  "fundPurpose": "생활비",
  "loanAmount": 20000000,
  "preferentialIds": [1, 2]
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `repaymentType` | string | 상환 방식 |
| `rateTypeCode` | string | `F` (고정금리) / `V` (변동금리) |
| `rateChangeCycle` | string | 리프라이싱 주기 (변동금리 시) |
| `loanPeriod` | string | 대출 기간 (예: `36개월`) |
| `depositAccountNo` | string | 입금 계좌번호 |
| `fundPurpose` | string | 자금 용도 |
| `loanAmount` | Long | 신청 금액 (원) |
| `preferentialIds` | array | 선택한 우대금리 ID 목록 |

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "대출 조건이 등록되었습니다.",
  "data": {
    "loanAmount": 20000000,
    "appliedBaseRate": 4.5,
    "totalPreferentialRate": 0.4,
    "finalRate": 4.1,
    "repaymentType": "원리금균등",
    "maturityDate": "2029-06-23"
  }
}
```

> `finalRate` = `appliedBaseRate - totalPreferentialRate`, 최소 0.1%

**Response** `400 Bad Request` — 신청 금액 > 승인 한도
```json
{
  "success": false,
  "code": "LOAN007",
  "message": "신청 금액이 승인 한도를 초과합니다"
}
```

**Response** `400 Bad Request` — 심사 결과 거절
```json
{
  "success": false,
  "code": "LOAN009",
  "message": "대출 심사가 거절되었습니다"
}
```

**Response** `404 Not Found` — 입금 계좌 없음
```json
{
  "success": false,
  "code": "CU002",
  "message": "계좌 정보를 찾을 수 없습니다"
}
```

---

### 5.3 신청정보 최종 확인

```
GET /api/v1/loans/applications/{loanAccountNo}/contract/confirm
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 처리되었습니다.",
  "data": {
    "productName": "부산은행 직장인 신용대출",
    "customerName": "홍길동",
    "loanAmount": 20000000,
    "finalRate": 4.1,
    "repaymentType": "원리금균등",
    "maturityDate": "2029-06-23",
    "depositAccountNo": "110123456789",
    "fundPurpose": "생활비",
    "preferentialRates": [
      { "conditionName": "급여이체", "rateValue": 0.2 },
      { "conditionName": "자동이체", "rateValue": 0.2 }
    ]
  }
}
```

**Response** `404 Not Found` — 신청서 없음
```json
{
  "success": false,
  "code": "LOAN001",
  "message": "대출 신청서를 찾을 수 없습니다"
}
```

---

### 5.4 약정 본인확인 (전자서명)

```
POST /api/v1/loans/applications/{loanAccountNo}/verification/contract
```

> 신청서 상태 `7` 필요 → 완료 시 상태 `8`로 변경

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
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "약정 전자서명이 완료되었습니다."
}
```

**Response** `400 Bad Request` — 전자서명 인증 실패
```json
{
  "success": false,
  "code": "VERIFY002",
  "message": "전자서명 인증에 실패했습니다"
}
```

**Response** `400 Bad Request` — 전자서명 토큰이 유효하지 않음
```json
{
  "success": false,
  "code": "VERIFY003",
  "message": "전자서명 토큰이 유효하지 않습니다"
}
```

**Response** `400 Bad Request` — 신청서 상태가 `7`이 아닌 경우
```json
{
  "success": false,
  "code": "LOAN004",
  "message": "현재 신청 단계에서 수행할 수 없는 요청입니다"
}
```

---

### 5.5 대출 실행

```
POST /api/v1/loans/applications/{loanAccountNo}/execute
```

> 신청서 상태 `8` 필요 → 완료 시 상태 `9`로 변경  
> 단일 트랜잭션: 대출 계약 확정 + 계좌 입금 + 신청서 상태 업데이트

**Request**
```json
{ "simplePassword": "123456" }
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "대출이 실행되었습니다.",
  "data": {
    "loanAccountNo": "BNK20260623000000001",
    "loanAmount": 20000000,
    "finalRate": 4.1,
    "maturityDate": "2029-06-23",
    "depositAccountNo": "110123456789",
    "executionDate": "2026-06-23T10:30:00"
  }
}
```

**Response** `400 Bad Request` — 비밀번호 불일치
```json
{
  "success": false,
  "code": "VERIFY001",
  "message": "비밀번호가 올바르지 않습니다"
}
```

**Response** `404 Not Found` — 입금 계좌 없음
```json
{
  "success": false,
  "code": "CU002",
  "message": "계좌 정보를 찾을 수 없습니다"
}
```

**Response** `500 Internal Server Error` — 대출 실행 실패 (트랜잭션 롤백)
```json
{
  "success": false,
  "code": "LOAN006",
  "message": "대출 실행에 실패했습니다"
}
```

---

## 6. 관리자 API (Admin)

> 세션 인증 필요  
> 별도의 관리자 권한 구분 없이 로그인한 사용자 모두 접근 가능 (내부 환경 전제)

---

### 6.1 전체 상품 목록 조회 (상태 무관)

```
GET /api/v1/admin/products
```

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 처리되었습니다.",
  "data": [
    { "productId": 1, "productName": "부산은행 직장인 신용대출", "baseRate": 4.5, "loanPeriod": "최대 5년", "status": "SALE" },
    { "productId": 2, "productName": "부산은행 소상공인 대출", "baseRate": 5.0, "loanPeriod": "최대 3년", "status": "DISCONTINUED" }
  ]
}
```

---

### 6.2 신규 상품 등록

```
POST /api/v1/admin/products
```

**Request**
```json
{
  "productName": "부산은행 직장인 신용대출",
  "baseRate": 4.5,
  "loanPeriod": "최대 5년",
  "status": "SALE",
  "descriptions": [
    { "attrKey": "LOAN_LIMIT", "attrValue": "최대 1억원", "sortOrder": 1 },
    { "attrKey": "RATE_INFO",  "attrValue": "연 4.5% ~ 8.0%", "sortOrder": 2 }
  ],
  "preferentialRates": [
    { "conditionCode": "SALARY_TRANSFER", "conditionName": "급여이체", "rateValue": 0.2, "description": "급여이체 실적 시 우대" }
  ],
  "terms": [
    { "termsType": "PRODUCT_TERMS", "termsPath": "/terms/product_terms_v1.pdf" }
  ]
}
```

| `attrKey` | 설명 |
|-----------|------|
| `LOAN_TYPE` | 대출 종류 |
| `ELIGIBLE` | 대출 대상 |
| `LOAN_LIMIT` | 대출 한도 |
| `RATE_INFO` | 금리 정보 |
| `REPAYMENT` | 상환 방법 |
| `FEE` | 수수료 |
| `CAUTION` | 유의사항 |
| `AI_SUMMARY` | AI 요약 (스케줄러 자동 적재) |

**Response** `201 Created` — 상품 상세 응답과 동일
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "상품이 등록되었습니다.",
  "data": {
    "productId": 4,
    "productName": "부산은행 직장인 신용대출",
    "baseRate": 4.5,
    "loanPeriod": "최대 5년",
    "status": "SALE",
    "descriptions": [
      { "attrKey": "LOAN_LIMIT", "attrValue": "최대 1억원", "sortOrder": 1 },
      { "attrKey": "RATE_INFO",  "attrValue": "연 4.5% ~ 8.0%", "sortOrder": 2 }
    ],
    "preferentialRates": [
      { "preferentialId": 5, "conditionCode": "SALARY_TRANSFER", "conditionName": "급여이체", "rateValue": 0.2, "description": "급여이체 실적 시 우대" }
    ]
  }
}
```

**Response** `400 Bad Request` — 입력값 오류
```json
{
  "success": false,
  "code": "C001",
  "message": "입력값이 유효하지 않습니다"
}
```

---

### 6.3 상품 수정

```
PUT /api/v1/admin/products/{productId}
```

기존 설명·우대금리를 전체 교체합니다. 약관은 이력 방식으로 추가됩니다.

**Request** — 등록 요청과 동일한 형식

**Response** `200 OK` — 상품 상세 응답과 동일
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "상품 정보가 수정되었습니다.",
  "data": {
    "productId": 1,
    "productName": "부산은행 직장인 신용대출",
    "baseRate": 4.3,
    "loanPeriod": "최대 5년",
    "status": "SALE",
    "descriptions": [
      { "attrKey": "LOAN_LIMIT", "attrValue": "최대 1억원", "sortOrder": 1 }
    ],
    "preferentialRates": [
      { "preferentialId": 1, "conditionCode": "SALARY_TRANSFER", "conditionName": "급여이체", "rateValue": 0.2, "description": "급여이체 실적 시 우대" }
    ]
  }
}
```

**Response** `404 Not Found` — 존재하지 않는 상품
```json
{
  "success": false,
  "code": "PRODUCT001",
  "message": "상품을 찾을 수 없습니다"
}
```

**Response** `400 Bad Request` — 입력값 오류
```json
{
  "success": false,
  "code": "C001",
  "message": "입력값이 유효하지 않습니다"
}
```

---

### 6.4 상품 삭제 (소프트)

```
DELETE /api/v1/admin/products/{productId}
```

상품을 삭제하지 않고 `status = DISCONTINUED`로 변경합니다.

**Response** `200 OK`
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "상품이 판매 중지되었습니다.",
  "data": {
    "productId": 2,
    "status": "DISCONTINUED"
  }
}
```

**Response** `404 Not Found` — 존재하지 않는 상품
```json
{
  "success": false,
  "code": "PRODUCT001",
  "message": "상품을 찾을 수 없습니다"
}
```

---

### 6.5 AI 요약 생성/갱신

```
POST /api/v1/admin/products/{productId}/ai-summary
```

**Request body 유무에 따라 동작이 달라집니다.**

| 상황 | 동작 |
|------|------|
| `{ "summary": "..." }` 제공 | 입력된 텍스트를 `AI_SUMMARY`로 수동 저장 |
| body 없음 또는 `summary` 미포함 | Claude API(`claude-opus-4-8`)를 호출하여 자동 생성 |

> 자동 생성은 `ANTHROPIC_API_KEY` 환경변수가 설정된 경우에만 동작합니다.

**Request (수동 저장)**
```json
{ "summary": "이 상품은 직장인을 대상으로 한 신용대출 상품으로..." }
```

**Request (자동 생성 — body 생략 또는 빈 body)**
```json
{}
```

**Response** `200 OK` — 수동 저장
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "AI 요약이 수동으로 갱신되었습니다.",
  "data": {
    "attrKey": "AI_SUMMARY",
    "attrValue": "이 상품은 직장인을 대상으로 한 신용대출 상품으로...",
    "sortOrder": 99
  }
}
```

**Response** `200 OK` — Claude 자동 생성
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "AI 요약이 자동 생성되었습니다.",
  "data": {
    "attrKey": "AI_SUMMARY",
    "attrValue": "부산은행 직장인 신용대출은 재직기간 1년 이상의 직장인을 위한 신용대출 상품으로, 최대 1억원까지 이용 가능합니다. 급여이체 등 우대 조건 충족 시 금리를 추가 인하받을 수 있습니다.",
    "sortOrder": 99
  }
}
```

**Response** `404 Not Found` — 존재하지 않는 상품
```json
{
  "success": false,
  "code": "PRODUCT001",
  "message": "상품을 찾을 수 없습니다"
}
```

**Response** `500 Internal Server Error` — `ANTHROPIC_API_KEY` 미설정 상태에서 자동 생성 요청
```json
{
  "success": false,
  "code": "C003",
  "message": "AI 요약 생성에 실패했습니다. ANTHROPIC_API_KEY가 설정되어 있는지 확인하세요."
}
```

---

## 7. 대출 신청 전체 플로우

```
[회원가입]
  1. POST /auth/email/send          이메일 인증 코드 발송
  2. POST /auth/email/verify         인증 코드 확인
  3. POST /auth/register             회원가입 + 계좌 자동 생성

[로그인]
  4. POST /auth/login                간편비밀번호 로그인 → 세션 발급

[대출 사전 절차] (status: → 1 → 2 → 3 → 4)
  5. GET  /products                  대출상품 목록 조회
  6. POST /loans/applications        신청서 생성 (채번)              → status 1
  7. POST /{no}/suitability          적합성·적정성 확인서 입력
  8. POST /{no}/verification/suitability  본인인증 (간편비밀번호)   → status 2
  9. POST /{no}/mydata-consent       공공마이데이터 이용 동의        → status 3
 10. POST /{no}/documents/ADMIN_INFO_REQUEST/view   서류 열람
 11. POST /{no}/documents/ADMIN_INFO_REQUEST/agree  서류 동의
 12. POST /{no}/signatures           전자서명 (PRE_PROCESS)          → status 4

[대출 한도 조회] (status: 4 → 5 → 6)
 13. POST /{no}/signatures/token     전자서명 토큰 발급
 14. POST /{no}/income               직장·소득정보 입력              → status 5
 15. GET  /{no}/mydata               공공마이데이터 조회
 16. POST /{no}/screening            대출 한도 산출                  → status 6
 17. GET  /products/{id}/descriptions  금융상품 중요사항 설명 조회

[대출 약정 및 실행] (status: 6 → 7 → 8 → 9)
 18. POST /{no}/contract/terms       약관 전체 동의
 19. POST /{no}/contract/conditions  대출 조건 입력                  → status 7
 20. GET  /{no}/contract/confirm     신청정보 최종 확인
 21. POST /{no}/verification/contract  약정 전자서명                 → status 8
 22. POST /{no}/execute              대출 실행 + 입금                 → status 9
```

---

## 8. 스케줄러

| 스케줄러 | 실행 시간 | 동작 |
|---------|----------|------|
| `LoanExpireScheduler` | 매일 01:00 | 만료된 신청서(`expire_at < now`) `status = X` 일괄 처리 |
| `AiSummaryScheduler` | 매일 02:00 | Claude API(`claude-opus-4-8`)로 전체 상품의 AI 요약을 생성하여 `AI_SUMMARY` 항목에 upsert |

**AiSummaryScheduler 동작 조건**

| 조건 | 동작 |
|------|------|
| `ANTHROPIC_API_KEY` 설정됨 | 상품별 설명(대출종류·대상·한도·금리 등)을 Claude에 전달하여 2~3문장 한국어 요약 자동 생성 |
| `ANTHROPIC_API_KEY` 미설정 | 해당 상품 건너뜀 (경고 로그 출력), 다른 기능에는 영향 없음 |

---

## 9. 기본 샘플 데이터 (`data.sql`)

서버 최초 실행 전 `data.sql`을 실행하면 아래 데이터가 생성됩니다.

### 대출 상품

| ID | 상품명 | 기준금리 | 대출기간 |
|----|--------|---------|---------|
| 1 | 부산은행 직장인 신용대출 | 4.50% | 최대 5년 |
| 2 | 부산은행 전문직 우대대출 | 3.90% | 최대 7년 |
| 3 | 부산은행 청년 희망대출 | 5.20% | 최대 3년 |

### 우대금리 항목

| 상품 | 조건 | 우대금리 |
|------|------|---------|
| 직장인 신용대출 | 급여이체 | -0.30% |
| 직장인 신용대출 | 자동이체 | -0.20% |
| 직장인 신용대출 | 카드 사용 | -0.20% |
| 직장인 신용대출 | 신규 고객 | -0.20% |
| 직장인 신용대출 | 디지털뱅킹 가입 | -0.10% |
| 전문직 우대대출 | 급여이체 | -0.30% |
| 전문직 우대대출 | 장기 거래 (5년↑) | -0.40% |
| 전문직 우대대출 | 자동이체 | -0.20% |
| 전문직 우대대출 | 신규 고객 | -0.30% |
| 청년 희망대출 | 급여이체 | -0.30% |
| 청년 희망대출 | 자동이체 | -0.20% |
| 청년 희망대출 | 신규 고객 (특별) | -0.50% |
| 청년 희망대출 | 디지털뱅킹 가입 | -0.30% |
| 청년 희망대출 | 청년 특별 우대 | -0.20% |

### 약관

상품별 6종 약관이 자동 등록됩니다 (ADMIN_INFO_REQUEST, PERSONAL_INFO_CONSENT, MOBILE_AUTH_TERMS, PRODUCT_TERMS, PRODUCT_DESCRIPTION, BOND_CONTRACT).

---

## 10. 구현 시 주요 변경 이력

| 항목 | 변경 내용 |
|------|----------|
| Java 버전 | 17 → **21** (설치된 JDK 기준) |
| `PasswordEncoder` 주입 | `BCryptPasswordEncoder` 구체 타입 → **`PasswordEncoder` 인터페이스** |
| `birthDate` 처리 | DTO에서 `LocalDate` 직접 역직렬화 → **`String`으로 수신 후 서비스에서 파싱** |
| `ProductDescription.attrValue` | `CLOB` → **`LONGTEXT`** (MySQL 호환) |
| 이메일 발송 실패 | 500 에러 전파 → **try-catch 후 콘솔 로그 출력** (로컬 개발 편의) |
| AES 키 | 31바이트 오류 → **32바이트 정상 키**로 수정 |


