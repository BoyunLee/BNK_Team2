# 부산은행 대출 프로세스 서버 기술 명세서

> 본 문서는 구현된 코드를 기준으로 작성된 실제 스펙이다.  
> 도메인·API 상세는 `SERVER_SPEC.md`를 참고한다.

---

## 1. 기술 스택

| 분류 | 기술 | 버전 |
|------|------|------|
| Language | Java | 21 |
| Framework | Spring Boot | 3.3.4 |
| ORM | Spring Data JPA (Hibernate 6) | 3.3.4 |
| DB | MySQL | 8.x |
| Build | Gradle (Kotlin DSL) | 8.x |
| Connection Pool | HikariCP | (Spring Boot 내장) |
| Caching | Spring Cache + Caffeine | - |
| Logging | SLF4J + Logback | - |
| AOP | Spring AOP (AspectJ) | - |
| Validation | Jakarta Validation | - |
| Security | Spring Security (BCrypt 전용) | 6.x |
| Mail | Spring Boot Mail (JavaMailSender) | - |
| API Docs | SpringDoc OpenAPI (Swagger UI) | 2.6.0 |
| AI | Anthropic Java SDK (Claude API) | 2.34.0 |

> **인증 방식**: JWT 미사용. `HttpSession` + `AuthInterceptor` 세션 기반 인증.  
> **Spring Security**: 자체 인증 체계 비활성화, `PasswordEncoder` 빈(`BCryptPasswordEncoder` 구현체) 목적으로만 사용.  
> **주의**: 서비스 주입 시 구체 타입(`BCryptPasswordEncoder`) 아닌 인터페이스(`PasswordEncoder`)로 선언해야 Spring 빈 매칭이 정상 동작함.

---

## 2. 프로젝트 패키지 구조

```
src/main/java/com/busanbank/loan/
├── BnkApplication.java                  @SpringBootApplication, @EnableScheduling
│
├── domain/
│   ├── customer/
│   │   ├── controller/                  CustomerController.java, AccountController.java
│   │   ├── service/                     CustomerService.java, EmailService.java, AccountService.java
│   │   ├── repository/                  CustomerRepository.java, AccountRepository.java
│   │   ├── entity/                      Customer.java, Account.java
│   │   └── dto/
│   │       ├── request/
│   │       └── response/                CustomerResponse.java (full/masked), AccountResponse.java,
│   │                                    MyAccountResponse.java, LoanSummaryResponse.java, LoanDetailResponse.java
│   │
│   ├── product/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   │
│   └── loan/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       └── dto/
│
└── global/
    ├── audit/
    │   └── BaseTimeEntity.java
    ├── config/
    │   ├── AsyncConfig.java
    │   ├── CacheConfig.java
    │   ├── JpaConfig.java
    │   ├── SecurityConfig.java
    │   └── WebMvcConfig.java
    ├── crypto/
    │   ├── AesEncryptor.java
    │   ├── CryptoConfig.java
    │   ├── CryptoException.java
    │   ├── CryptoProperties.java
    │   ├── EncryptedStringConverter.java
    │   └── EncryptionKeyHolder.java
    ├── error/
    │   ├── code/
    │   │   └── ErrorCode.java
    │   ├── exception/
    │   │   └── BusinessException.java
    │   └── handler/
    │       └── GlobalExceptionHandler.java
    ├── interceptor/
    │   └── AuthInterceptor.java
    ├── logging/
    │   ├── aspect/
    │   │   └── TransactionLoggingAspect.java
    │   └── filter/
    │       └── MdcLoggingFilter.java
    ├── response/
    │   └── ApiResponse.java
    └── util/
        ├── MaskingUtil.java
        └── SessionUtil.java
```

---

## 3. 도메인 패키지 내부 규칙

### 3-1. 레이어 흐름
```
Controller → Service → Repository → Entity
```
- **Controller**: HTTP 요청/응답 처리, DTO 변환. 비즈니스 로직 없이 얇게 유지
- **Service**: 비즈니스 로직, 트랜잭션 경계 관리
- **Repository**: Spring Data JPA 인터페이스. 복잡 쿼리는 `@Query` JPQL 사용
- **Entity**: DB 테이블 매핑. 도메인 행위 메서드 포함 가능. Setter 비공개

### 3-2. DTO 규칙
- `request/` : 클라이언트 → 서버 입력값. `@Valid` 검증 어노테이션 포함
- `response/` : 서버 → 클라이언트 출력값. **Entity 직접 반환 절대 금지**
- 변환 메서드: DTO 내부 static factory method (`full()`, `masked()`, `from()`)

```java
// 노출 레벨을 static 팩토리로 명시적으로 분리
CustomerResponse.full(customer)    // 평문 전체 (본인확인 후 마이페이지)
CustomerResponse.masked(customer)  // 마스킹 (목록·요약 화면)
```

### 3-3. Entity 규칙
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` — JPA 기본 생성자 보호
- `@Builder`로 생성, Setter 미노출
- PK: `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- 공통 시각 필드: `BaseTimeEntity` 상속 (`createdAt`, `updatedAt`)
- 개인정보 필드: `@Convert(converter = EncryptedStringConverter.class)` 적용

### 3-4. Service 규칙
- 읽기: `@Transactional(readOnly = true)`
- 쓰기: `@Transactional`
- Repository 결과 처리: `.orElseThrow(() -> new BusinessException(ErrorCode.XXX))`
- 타 도메인 Service 직접 주입 지양 → 필요 시 이벤트(Spring Event) 사용

---

## 4. global 패키지 상세

### 4-1. 공통 API 응답 포맷 (`global/response/`)

모든 API 응답은 `ApiResponse<T>` 포맷을 따른다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 처리되었습니다.",
  "data": { ... }
}
```

| 메서드 | 용도 |
|--------|------|
| `ApiResponse.ok(data)` | 200 성공 응답 |
| `ApiResponse.ok()` | 200 성공 (data 없음) |
| `ApiResponse.created(data)` | 201 생성 성공 |
| `ApiResponse.error(code, message)` | 에러 응답 |

- `data` 필드: null일 때 직렬화에서 제외 (`@JsonInclude(NON_NULL)`)

---

### 4-2. 에러 처리 (`global/error/`)

#### ErrorCode (전체 목록)

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

#### BusinessException

```java
throw new BusinessException(ErrorCode.LOAN_EXPIRED);
// 또는 커스텀 메시지 추가 시
throw new BusinessException(ErrorCode.INVALID_INPUT, "신청금액: 1원 이상이어야 합니다");
```

#### GlobalExceptionHandler 처리 목록

| 예외 타입 | 처리 |
|-----------|------|
| `BusinessException` | `WARN` 로그, 해당 ErrorCode HTTP 상태 반환 |
| `MethodArgumentNotValidException` | `@Valid` 실패, 필드별 메시지 조합, 400 |
| `BindException` | ModelAttribute 바인딩 실패, 400 |
| `ConstraintViolationException` | `@Validated` 파라미터 검증 실패, 400 |
| `HttpMessageNotReadableException` | 요청 본문 파싱 실패, 400 |
| `MissingServletRequestParameterException` | 필수 파라미터 누락, 400 |
| `MethodArgumentTypeMismatchException` | 파라미터 타입 불일치, 400 |
| `HttpRequestMethodNotSupportedException` | 지원하지 않는 HTTP 메서드, 405 |
| `Exception` (그 외 전체) | `ERROR` 로그 + 스택트레이스, 500 |

---

### 4-3. 개인정보 암호화 (`global/crypto/`)

#### 설계 원칙

| 대상 | 방식 | 이유 |
|------|------|------|
| 이름, 전화번호, 주소, 이메일, 계좌번호 | AES-256-CBC (결정적) | DB 등호 조건 쿼리 지원 필요 |
| 로그인 비밀번호, 간편비밀번호, 계좌비밀번호 | BCrypt (단방향) | 복호화 불필요, 검증만 수행 |

#### 암복호화 알고리즘

- **알고리즘**: AES-256-CBC / PKCS5Padding
- **키 길이**: 32바이트 (256비트)
- **IV**: 키 앞 16바이트로 고정 (결정적 암호화 — 동일 평문 → 동일 암호문)
- **저장 포맷**: `Base64(암호문)` 문자열

#### 초기화 흐름

```
application.yml (crypto.secret-key)
    → CryptoProperties (@ConfigurationProperties)
    → CryptoConfig (@PostConstruct)
    → EncryptionKeyHolder (static 보관)
    → AesEncryptor (암복호화 유틸)
    → EncryptedStringConverter (JPA AttributeConverter)
```

> JPA Converter는 Spring 컨텍스트 외부에서 인스턴스화되므로 static 홀더 패턴을 사용한다.  
> `@PostConstruct`가 JPA 초기화보다 먼저 실행되어 키 누락 없이 동작한다.

#### Entity 적용 방법

```java
// 개인정보 필드: @Convert 1개로 자동 암복호화
@Convert(converter = EncryptedStringConverter.class)
@Column(name = "phone_no", length = 100)   // 암호문이 원문보다 길므로 컬럼 길이 여유 확보
private String phoneNo;

// 비밀번호 필드: BCrypt, @Convert 미사용
@Column(name = "simple_password", length = 200)
private String simplePassword;  // PasswordEncoder.encode() 결과 저장 (BCrypt)
```

#### 암호화 대상 컬럼

| 테이블 | 컬럼 | 방식 |
|--------|------|------|
| CUSTOMER | name, phone_no, address, email | AES-256 |
| CUSTOMER | password, simple_password | BCrypt |
| ACCOUNT | account_no | AES-256 |
| ACCOUNT | account_password | BCrypt |

#### 키 관리 규칙

- 로컬: `application-local.yml`의 `crypto.secret-key` (개발용 고정 키)
- 운영: 환경변수 `CRYPTO_SECRET_KEY` 주입 (코드·설정파일에 절대 커밋 금지)
- 키 생성: `openssl rand -base64 32`

#### DB 컬럼 길이 주의사항

AES-CBC 암호화 후 Base64 인코딩 시 원문보다 약 1.4배 길어진다.  
ERD의 컬럼 길이가 부족하면 실행 시 `DataTruncationException` 발생.

| 원문 예상 최대 길이 | 권장 DB 컬럼 길이 |
|---------------------|-------------------|
| 50자 (이름) | 200 |
| 20자 (전화번호) | 100 |
| 100자 (이메일) | 300 |
| 200자 (주소) | 500 |
| 30자 (계좌번호) | 100 |

---

### 4-4. 로깅 (`global/logging/`)

#### MdcLoggingFilter (HTTP 레벨)

- 모든 요청에 `traceId` 생성 (`X-Trace-Id` 헤더 있으면 재사용)
- MDC에 `traceId`, `customerId`, `requestIp` 등록 → 모든 로그에 자동 포함
- 요청 처리 완료 후 HTTP 로그 출력 후 `MDC.clear()`

```
[HTTP] method=POST uri=/api/v1/loans/applications status=201 duration=234ms
```

#### TransactionLoggingAspect (서비스 레벨)

- `@Pointcut`: `domain` 하위 패키지의 `@Service` 클래스 전체 메서드
- `@Around`로 진입/종료/예외 3단계 로그 기록
- 파라미터: JSON 직렬화 후 민감정보 자동 마스킹 적용

```
[TX_ENTER]   traceId=a1b2c3 customerId=1001 action=LoanApplicationService.create params={...}
[TX_EXIT]    traceId=a1b2c3 customerId=1001 action=LoanApplicationService.create duration=210ms result=SUCCESS
[TX_BIZ_FAIL] traceId=a1b2c3 customerId=1001 action=... errorCode=LOAN003 message=만료된 신청서
[TX_SYS_FAIL] traceId=a1b2c3 customerId=1001 action=... error=... (스택트레이스 포함)
```

#### Logback 설정 (`logback-spring.xml`)

| 프로파일 | 출력 | 특징 |
|----------|------|------|
| `local` | 컬러 콘솔 | 가독성 중심 |
| `dev`, `prod` | 파일 3종 분리 | 비동기 Appender |

파일 분리 (dev/prod):

| 파일 | 내용 | 보존 |
|------|------|------|
| `application.log` | 전체 로그 | 30일, 100MB 롤링 |
| `transaction.log` | TX_* + HTTP 로그만 | 90일 (거래 추적 보존) |
| `error.log` | ERROR 레벨만 | 90일 |

MDC 포함 로그 패턴:
```
HH:mm:ss.SSS LEVEL [traceId | cid=customerId | requestIp] Logger - message
```

---

### 4-5. 세션 인증 (`global/interceptor/`, `global/util/`)

#### 세션 흐름

```
로그인 성공 → SessionUtil.setCurrentCustomerId(request, customerId)
               → HttpSession에 "SESSION_CUSTOMER_ID" 저장

보호 API 요청 → AuthInterceptor.preHandle()
               → 세션 없음: BusinessException(SESSION_EXPIRED)
               → customerId 없음: BusinessException(UNAUTHORIZED)
               → 정상: MDC customerId 보강 후 통과
```

#### 인증 제외 경로 (WebMvcConfig)

```
/api/v1/auth/email/**   이메일 인증 코드 발송·확인
/api/v1/auth/register   회원가입
/api/v1/auth/login      로그인
/swagger-ui/**          API 문서
/v3/api-docs/**         OpenAPI JSON
```

#### Service에서 현재 고객 조회

```java
Long customerId = SessionUtil.getCurrentCustomerId();
```

#### 세션 설정

| 항목 | 값 |
|------|----|
| 타임아웃 | 30분 |
| 쿠키 HttpOnly | true |
| 쿠키 Secure | false (로컬), true (운영) |

---

### 4-6. 캐시 (`global/config/CacheConfig`)

| 캐시명 | TTL | 용도 |
|--------|-----|------|
| `products` | 10분 | 판매 중 상품 목록 |
| `productDetail` | 10분 | 상품 상세 (설명 + 우대금리) |
| `emailCode` | 5분 | 이메일 인증 코드 |
| `emailVerified` | 10분 | 이메일 인증 완료 여부 |

- 구현체: Caffeine (로컬 인메모리)
- 분산 환경 전환 시: `CacheManager` 빈을 `RedisCacheManager`로 교체 (서비스 코드 무변경)

---

### 4-7. 민감정보 마스킹 (`global/util/MaskingUtil`)

화면 응답 DTO 생성 시 아래 규칙을 적용한다.

| 항목 | 원문 | 마스킹 결과 |
|------|------|-------------|
| 이름 | 홍길동 | 홍** |
| 전화번호 | 010-1234-5678 | 010-****-5678 |
| 이메일 | test@email.com | te**@email.com |
| 주소 | 부산시 중구 어딘가 | 부산시 중구 *** |
| 계좌번호 | 110123456789 | 110-***-***789 |
| 비밀번호 (로그) | 123456 | **** |

MaskingUtil은 로그 마스킹(`maskSensitiveJson`)과 화면 마스킹(`maskPhone` 등) 두 가지 용도로 사용된다.

---

### 4-8. Spring 설정 (`global/config/`)

| 파일 | 역할 |
|------|------|
| `JpaConfig.java` | `@EnableJpaAuditing` (BaseTimeEntity 자동 시각 주입) |
| `SecurityConfig.java` | Spring Security 인증 비활성화, `BCryptPasswordEncoder` 빈 등록 |
| `WebMvcConfig.java` | CORS 설정, `AuthInterceptor` 등록 및 제외 경로 설정 |
| `AsyncConfig.java` | `@EnableAsync`, `asyncExecutor`(범용) + `notificationExecutor`(이메일·알림) |
| `CacheConfig.java` | `@EnableCaching`, 캐시별 Caffeine 설정 등록 |
| `CryptoConfig.java` | `@PostConstruct`로 암호화 키 `EncryptionKeyHolder` 초기화 |

---

## 5. DB 네이밍 규칙

| 항목 | 규칙 | 예시 |
|------|------|------|
| 테이블명 | UPPER_SNAKE_CASE | `LOAN_APPLICATION` |
| 컬럼명 | snake_case | `applied_at`, `customer_id` |
| PK | 테이블명 기반 (`{단수}_id`) | `customer_id`, `product_id` |
| FK | `{참조테이블 단수형}_id` | `customer_id` |
| 생성/수정 시각 | `created_at`, `updated_at` | - |
| 암호화 컬럼 | 원래 컬럼명 유지, 길이만 늘림 | `name VARCHAR(200)` |

---

## 6. API URL 규칙

```
/api/v1/{도메인}/{리소스}
```

| HTTP Method | URL 예시 | 의미 |
|-------------|----------|------|
| GET | `/api/v1/loans/applications` | 목록 조회 |
| GET | `/api/v1/loans/applications/{id}` | 단건 조회 |
| POST | `/api/v1/loans/applications` | 신규 생성 |
| PATCH | `/api/v1/loans/applications/{id}` | 부분 수정 |
| DELETE | `/api/v1/loans/applications/{id}` | 삭제 (소프트) |

---

## 7. 환경 설정 파일

```
src/main/resources/
├── application.yml          공통 설정 (환경변수 참조)
├── application-local.yml    로컬 개발 (DB localhost, 로그 DEBUG)
├── application-dev.yml      개발 서버
├── application-prod.yml     운영 서버
└── logback-spring.xml       프로파일별 로그 설정
```

**주요 환경변수**

| 변수명 | 설명 |
|--------|------|
| `DB_URL` | MySQL JDBC URL |
| `DB_USERNAME` | DB 계정 |
| `DB_PASSWORD` | DB 비밀번호 |
| `CRYPTO_SECRET_KEY` | AES-256 키 (Base64, 32바이트) |
| `MAIL_HOST` | SMTP 호스트 |
| `MAIL_USERNAME` | 발신 이메일 계정 |
| `MAIL_PASSWORD` | 이메일 비밀번호 |

---

## 8. 코드 작성 원칙

1. **Controller는 얇게**: 요청 수신 → Service 위임 → 응답 반환만 담당, 비즈니스 로직 없음
2. **Entity 직접 노출 금지**: 반드시 DTO로 변환. `CustomerResponse.full()` / `.masked()` 레벨 선택
3. **생성자 주입**: `@Autowired` 필드 주입 금지. `@RequiredArgsConstructor` + `private final` 사용
4. **예외는 중앙에서**: Service에서 `try-catch` 금지. `BusinessException` 던지면 `GlobalExceptionHandler`가 처리
5. **주석 최소화**: WHY가 비자명한 경우에만 한 줄 주석. WHAT 설명 주석 작성 금지
6. **민감정보 보호**: 로그에 개인정보 직접 출력 금지. `MaskingUtil.maskSensitiveJson()` 경유
7. **암호화 컬럼 쿼리**: `@Convert` 필드는 JPQL 등호 조건 사용 가능 (결정적 암호화). LIKE 조건 불가

---

## 9. 도메인 목록

| 패키지 | 담당 테이블 | 주요 API | 상태 |
|--------|-------------|---------|------|
| `customer` | CUSTOMER, ACCOUNT | 회원가입, 로그인, 계좌·대출 조회 | 구현 완료 |
| `product` | LOAN_PRODUCT, PRODUCT_DESCRIPTION, PRODUCT_PREFERENTIAL_RATE, PRODUCT_TERMS_BASE, PRODUCT_TERMS_HISTORY | 상품 목록·상세, 관리자 CRUD | 구현 완료 |
| `loan` | LOAN_APPLICATION, LOAN_SCREENING, LOAN_CONTRACT, INCOME_INFO, SUITABILITY_RESPONSE, CUSTOMER_VERIFICATION, MYDATA_CONSENT, APPLICATION_DOCUMENT_LOG, LOAN_PREFERENTIAL_APPLIED, SIGNATURE | 대출 신청 프로세스, 한도 조회, 약정·실행, **신청서 취소** | 구현 완료 |

### 계좌·대출 조회 API 설계 (`customer` 패키지 확장)

```
GET /api/v1/customers/me/account           내 계좌 잔액 조회
GET /api/v1/customers/me/loans             내 대출 목록 (신청서 전체)
GET /api/v1/customers/me/loans/{no}        대출 계약 상세
```

**AccountController** (`/api/v1/customers/me`)
- 세션에서 `customerId` 추출 → `AccountService` 위임
- 계좌 잔액, 대출 현황, 계약 상세를 각각 DTO로 반환

**AccountService**
- `getMyAccount(Long customerId)` → `MyAccountResponse`
- `getMyLoans(Long customerId)` → `List<LoanSummaryResponse>` (최신순)
- `getLoanDetail(Long customerId, String loanAccountNo)` → `LoanDetailResponse`

**Response DTO 설계**

| DTO | 구성 |
|-----|------|
| `MyAccountResponse` | `accountNo`(마스킹), `balance`, `status`, `customerName`(마스킹) |
| `LoanSummaryResponse` | `loanAccountNo`, **`productId`**, `productName`, `statusCode`, `statusName`, `loanAmount`, `finalRate`, `maturityDate`, `appliedAt` |
| `LoanDetailResponse` | `LoanSummaryResponse` 필드 + `expireAt` + `contract`(nullable) |
| `LoanDetailResponse.ContractInfo` | `loanAmount`, `finalRate`, `repaymentType`, `rateTypeCode`, `loanPeriod`, `maturityDate`, `depositAccountNo`(마스킹), `fundPurpose`, `executionDate`, `preferentialRates` |

> `LoanSummaryResponse.productId` 추가 이유: 이어하기 시 클라이언트가 `GET /api/v1/products/{productId}`로 상품 정보를 복원해야 하므로 ID 노출 필요

**statusName 매핑**

| statusCode | statusName |
|-----------|-----------|
| `1` | 신청서 작성 중 |
| `2` | 적합성 확인 완료 |
| `3` | 마이데이터 동의 완료 |
| `4` | 사전 전자서명 완료 |
| `5` | 소득 정보 입력 완료 |
| `6` | 한도 조회 완료 |
| `7` | 대출 조건 입력 완료 |
| `8` | 약정 전자서명 완료 |
| `9` | 대출 실행 완료 |
| `X` | 만료 |
| `R` | 취소/거절 |

### 신청서 취소 및 이어하기 설계

#### 신청서 취소 API

```
PATCH /api/v1/loans/applications/{loanAccountNo}/cancel
```

**LoanApplicationService.cancelApplication(String loanAccountNo, Long customerId)**
1. `findByLoanAccountNo` → 없으면 `LOAN_NOT_FOUND`
2. `customerId` 불일치 → `LOAN_NOT_FOUND` (타인 신청서 노출 방지)
3. `statusCode` in `['9','X','R']` → `INVALID_STEP` (이미 종료된 신청서)
4. `application.updateStatus("R")`

**Controller**: `LoanApplicationController`에 추가
```java
@PatchMapping("/{loanAccountNo}/cancel")
public ApiResponse<Void> cancelApplication(@PathVariable String loanAccountNo) {
    Long customerId = SessionUtil.getCurrentCustomerId();
    loanApplicationService.cancelApplication(loanAccountNo, customerId);
    return ApiResponse.ok();
}
```

#### 이어하기 UX 흐름

**신규 신청 시 진행 중 신청서 충돌 처리 (클라이언트)**

```
POST /loans/applications
  → 409 LOAN002 수신
  → GET /customers/me/loans 로 진행 중 신청서 조회
  → 팝업 표시:
      "이미 진행 중인 대출 신청이 있습니다."
      [이어서 작성하기] [새로 시작하기]
  → 이어서 작성하기: resumeLoan(inProgressLoan)
  → 새로 시작하기:
      PATCH /loans/applications/{no}/cancel
      → POST /loans/applications (재시도)
```

**statusCode → 재진입 화면 매핑 (클라이언트 로직)**

| statusCode | 재진입 화면 | 추가 API 호출 |
|-----------|-----------|--------------|
| `1` | `SUITABILITY_FORM` | `GET /products/{productId}` |
| `2` | `MYDATA_CONSENT` | `GET /products/{productId}` |
| `3` | `DOC_ADMIN_VIEW` | `GET /products/{productId}` |
| `4` | `MOBILE_AUTH_TERMS` | `GET /products/{productId}` |
| `5` | `MYDATA_VIEW` | `GET /products/{productId}` + `GET /applications/{no}/mydata` |
| `6` | `PRODUCT_DESC_1` | `GET /products/{productId}` + `GET /applications/{no}/screening` |
| `7` | `CONFIRM_INFO` | `GET /products/{productId}` + `GET /applications/{no}/contract/confirm` |
| `8` | `BOND_CONTRACT` | `GET /products/{productId}` |

**대출 목록(MY_LOANS)에서 이어하기**
- `statusCode` in `['1','2','3','4','5','6','7','8']` → **"이어서 작성하기"** 버튼 표시
- 버튼 클릭 → `resumeLoan(loan)` 함수 호출 → 위 매핑에 따라 화면 이동

---

## 10. 구현 시 변경 이력 (초기 설계 대비 실제 반영 사항)

| 항목 | 초기 설계 | 실제 구현 | 사유 |
|------|-----------|-----------|------|
| Java 버전 | 17 | **21** | 설치된 JDK가 21 LTS |
| `PasswordEncoder` 주입 타입 | `BCryptPasswordEncoder` | **`PasswordEncoder` 인터페이스** | `SecurityConfig` 빈이 인터페이스 타입으로 등록되어 구체 타입 주입 시 `UnsatisfiedDependencyException` 발생 |
| `ProductDescription.attrValue` | `CLOB` | **`LONGTEXT`** | MySQL은 CLOB 타입 미지원 |
| `RegisterRequest.birthDate` | `LocalDate` | **`String` (`"yyyy-MM-dd"`)** | Jackson의 `LocalDate` 역직렬화 파싱 오류 방지; 서비스에서 `LocalDate.parse()` 처리 |
| `LoanSummaryResponse.productId` | 미포함 | **추가** | 이어하기 기능에서 상품 정보 복원 시 `GET /products/{productId}` 호출에 필요 |
| 신청서 취소 API | 미설계 | **`PATCH /loans/applications/{no}/cancel`** | 이어하기 기능 구현을 위해 신규 신청 전 기존 진행 중 신청서 취소 필요 |
| `AiSummaryScheduler` | 설명 문자열 단순 연결 (Mock) | **Claude API(`claude-opus-4-8`) 호출** | 실제 AI 요약 생성 |
| `AI_SUMMARY` 관리자 엔드포인트 | 수동 텍스트 저장 | **body 없으면 Claude 자동 생성, body 있으면 수동 저장** | UX 개선 |
| 이메일 발송 실패 처리 | 예외 전파 → 500 | **try-catch 후 콘솔 로그** | 로컬 개발 시 SMTP 인증 없이도 테스트 가능하도록 |
| DB 포트 | 3306 (기본) | **3306** | `application-local.yml`에서 설정 |
| AES 암호화 키 길이 | 32바이트 | **32바이트** | `application-local.yml`의 키가 31바이트였던 버그 수정 (`dGVzdGtleTF0ZXN0a2V5MnRlc3RrZXkzdGVzdGtleTQ=`) |

---

## 11. 로컬 개발 실행 방법

### 사전 요건
- Java 21 JDK
- MySQL 8.x (포트 3306)

### DB 초기화
```bash
# 1. 데이터베이스 및 테이블 생성
mysql -h 127.0.0.1 -P 3306 -u root -p -e "source schema.sql"

# 2. 샘플 데이터 입력 (상품 3종 + 약관)
mysql -h 127.0.0.1 -P 3306 -u root -p -e "source data.sql"
```

### 서버 실행
```bash
.\gradlew.bat bootRun --no-daemon
```

### 테스트 UI
- 대출 신청 UI: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`

### 이메일 인증 (로컬)
메일 발송 실패 시 서버 콘솔에 인증 코드 출력:
```
========================================
  이메일 인증 코드: test@email.com → 381924
========================================
```
실제 메일 발송이 필요한 경우 `application-local.yml`에 Gmail 앱 비밀번호 설정:
```yaml
spring:
  mail:
    username: 본인계정@gmail.com
    password: xxxx-xxxx-xxxx-xxxx  # Google 계정 > 보안 > 앱 비밀번호
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
```

### AI 요약 기능 (선택)

`ANTHROPIC_API_KEY` 환경변수를 설정하면 Claude API를 통한 상품 요약 자동 생성이 활성화됩니다.

```yaml
# application-local.yml
anthropic:
  api-key: sk-ant-...   # https://console.anthropic.com 에서 발급
```

또는 환경변수로 설정:
```bash
set ANTHROPIC_API_KEY=sk-ant-...
```

**미설정 시:** AI 요약 기능만 비활성화되고 나머지 기능(회원가입, 로그인, 대출 신청 등)은 정상 동작합니다.

**활성화 시 동작:**
- `AiSummaryScheduler` — 매일 02:00에 전체 상품 요약 자동 생성
- `POST /admin/products/{id}/ai-summary` — body 없이 호출하면 즉시 Claude로 자동 생성

---

## 12. 의존성 (build.gradle.kts)

```kotlin
dependencies {
    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    // JPA + MySQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Cache (Caffeine)
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    // AOP (거래 로깅)
    implementation("org.springframework.boot:spring-boot-starter-aop")
    // Security (BCrypt만 사용)
    implementation("org.springframework.boot:spring-boot-starter-security")
    // Mail (이메일 인증)
    implementation("org.springframework.boot:spring-boot-starter-mail")
    // API 문서
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // Anthropic Claude SDK (AI 상품 요약)
    implementation("com.anthropic:anthropic-java:2.34.0")
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
```

