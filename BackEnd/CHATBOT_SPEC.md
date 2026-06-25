# 부산은행 대출 상품 상담 챗봇 명세서

> 본 문서는 대출 상품 안내 챗봇의 설계 초안이다.
> `TECHNICAL_SPEC.md`의 기본 구조·패턴 원칙을 따른다.
> 일부 항목은 미확정(TODO) 상태이며, 결정되는 대로 갱신한다.

---

## 1. 목표 / 범위

- **목표**: 사용자의 자연어 질문에 대해 부산은행 대출 상품 정보를 근거로 답변하는 RAG 기반 상담 챗봇
- **범위**: 대출 상품 안내 중심 (상품 종류·금리·한도·대상·상환방법 등)
- **방식**: 멀티턴 대화, 벡터 검색(RAG) 기반 근거 답변
- **비범위(초안 기준)**: 실제 대출 신청 처리, 개인 맞춤 한도/승인 심사 (상담사 연결로 안내)

---

## 2. 기술 스택

| 분류 | 기술 | 비고 |
|------|------|------|
| 임베딩 | Gemini API (embedding model) | 질문 / 상품 정보 벡터화 |
| 응답 생성 | Gemini API (generation model) | 검색 결과 기반 답변 생성 |
| 벡터 DB | Qdrant | 코사인 유사도 검색 |
| 대화 이력 | MySQL (JPA) | 추후 Redis 전환 검토 |
| 백엔드 | Spring Boot 3.3.4 / Java 21 | 기존 프로젝트 동일 |

> 기존 프로젝트의 Anthropic(Claude) 연동은 "AI 상품 요약"용으로 유지되며,
> 챗봇은 별도로 Gemini 기반으로 구성한다.

---

## 3. 전체 처리 흐름

```
① 사용자 입력 (질문)
        │
        ▼
② 질문 임베딩            ──▶ Gemini Embedding API
        │
        ▼
③ 벡터 검색              ──▶ Qdrant (코사인, top-k=3)
   유사한 대출 상품 정보 검색
        │
        ▼
④ 프롬프트 조립
   System Prompt + 검색 결과(근거) + 대화 이력 + 질문
        │
        ▼
⑤ 답변 생성              ──▶ Gemini Generation API
        │
        ▼
⑥ 후처리 / 저장
   민감정보 마스킹 · 금지표현 필터 · 로그 저장
        │
        ▼
⑦ 사용자에게 응답
```

---

## 4. 데이터 처리 (인덱싱)

### 4.1 청킹
- **청킹 단위**: 대출 상품 1개 = 1 청크
- 상품의 설명(`PRODUCT_DESCRIPTION`) 항목들을 하나의 텍스트로 합쳐 임베딩

### 4.2 메타데이터 설계 (Qdrant payload)

> `LOAN_PRODUCT` 엔티티 실제 필드 기준. 검색 후 필터링·출처 표기·원본 조회에 사용한다.

| 필드 | 출처 (엔티티 필드) | 용도 |
|------|-------------------|------|
| `productId` | `LoanProduct.productId` | 원본 상품 식별 / DB 재조회 키 |
| `productCode` | `LoanProduct.mkpdCd` | 상품 코드 (응답 출처 표기) |
| `productName` | `LoanProduct.productName` | 상품명 |
| `category` | `LoanProduct.category` | 카테고리 (신용 / 담보 / 전세 등) — **필터링** |
| `status` | `LoanProduct.status` | 판매 상태 (`SALE` / `DISCONTINUED`) — **판매중만 검색** |
| `baseRate` | `LoanProduct.baseRate` | 기준금리 |
| `rateMin` / `rateMax` | `LoanProduct.rateMin` / `rateMax` | 최저~최고 금리 |
| `loanPeriod` | `LoanProduct.loanPeriod` | 대출 기간 |
| `catchphrase` | `LoanProduct.catchphrase` | 캐치프레이즈 (요약 표기용) |
| `updatedAt` | `BaseTimeEntity.updatedAt` | 갱신 일자 |

> **참고**
> - 최초 정리안의 `minCreditScore`(최소 신용점수)는 **현재 스키마에 존재하지 않음** → 필드 추가 시 반영하거나 제외.
> - `LOAN_LIMIT`(대출한도)·`TARGET`(대출대상) 등은 `PRODUCT_DESCRIPTION`에 텍스트로 존재하며, 임베딩 본문에 포함된다. 필터가 필요하면 메타데이터로 승격 검토.
> - `status = 'SALE'` 조건은 **검색 단계 필터(Qdrant payload filter)** 로 적용하여 판매 종료 상품이 답변에 노출되지 않도록 한다.

---

## 5. 검색 (Retrieval)

| 항목 | 값 |
|------|-----|
| 검색 결과 개수 (top-k) | 3 |
| 유사도 거리 함수 | 코사인 (Cosine) |
| 최소 유사도 임계값 (초기값) | **0.70** |

- Qdrant 코사인 score는 **클수록 유사**(범위 -1.0 ~ 1.0, 정규화 임베딩에서는 사실상 0 ~ 1.0).
- top-3 중 **최상위 score가 0.70 미만**이면 관련 상품이 없다고 판단 → 결과를 버리고 **"관련 상품 없음"** 처리 → 상담사 연결 안내.
- 0.70은 초기 기준값이며, 실제 데이터로 다음과 같이 튜닝한다(권장 범위 **0.65 ~ 0.80**):
  - 무관한 질문에도 상품이 끌려오면(오검색 多) → 임계값 **상향**
  - 관련 질문인데 "관련 상품 없음"이 자주 뜨면(누락 多) → 임계값 **하향**
- 임계값은 코드 상수가 아닌 **설정값(`application.yml`)** 으로 두어 운영 중 조정 가능하게 한다.

---

## 6. 대화 관리

### 6.1 이력 저장
- **저장소**: MySQL (`ChatMessage` 엔티티) — 추후 Redis 전환
- 세션 키: `sessionId` (로그인 고객은 `customerId` 연계 가능)

### 6.2 멀티턴 컨텍스트 처리
- 답변 생성 시 **이전 대화 이력을 프롬프트에 포함**하여 후속 질문의 주어 생략 대응
  - 예) "그럼 금리는?" → 직전 대화의 상품 주제 유지
- 검토 필요(TODO):
  - 이력 전체 vs 최근 N턴만 포함 (토큰/비용 관리)
  - 후속 질문 임베딩 시, **이전 주제를 합쳐 재구성(query rewriting)** 할지 여부

### 6.3 데이터 모델 (초안)

| 필드 | 설명 |
|------|------|
| `id` | PK |
| `sessionId` | 대화 세션 식별자 |
| `role` | `USER` / `ASSISTANT` |
| `content` | 메시지 본문 |
| `createdAt` | 생성 시각 |

---

## 7. 응답 생성

### 7.1 System Prompt 설계 (요지)
- **페르소나**: 부산은행 대출 상담원
- **근거 규칙**: 제공된 "검색 결과(상품 데이터)"에 있는 내용만 근거로 답변, 없으면 모른다고 안내
- **말투**: 정중하고 친절한 존댓말
- **금지 표현**: 확정적 승인/금리 약속, 단정적 보장 표현 금지 (5장 참고)

### 7.2 검색 결과 없을 때
- "관련 상품 없음" 안내 → **상담사 연결 안내** 멘트 반환

### 7.3 응답 형식
- 자유 텍스트

---

## 8. 컴플라이언스 / 로깅 (은행 특화)

| 항목 | 내용 |
|------|------|
| 질문-응답 로그 | **모든 질문/응답 저장** (분쟁 대응용, 6장 이력과 통합 가능) |
| 민감정보 마스킹 | 개인정보(주민번호·계좌·연락처 등) 마스킹 — 기존 `MaskingUtil` 재사용 검토 |
| 금지 표현 필터 | 확정적 **승인/금리 약속** 표현 자동 탐지·필터링 |

- 마스킹/필터 적용 시점(TODO): 저장 전 / 응답 반환 전 각각 정의 필요

---

## 9. 운영 / 보안

> 상품 데이터 적재/재임베딩(상품 CRUD 연동)은 **관리자 API 명세 문서**에서 별도로 다룬다. 본 문서는 챗봇의 조회·응답 흐름만 다룬다.

- **API 키 관리**: Gemini API 키는 환경변수/시크릿으로 관리
  - 기존 `anthropic.api-key` 패턴과 동일하게 `application.yml` + 환경변수(`${GEMINI_API_KEY:}`) 구성
  - 키 미설정 시 챗봇 기능 비활성화 처리 (기존 `@ConditionalOnProperty` 패턴 참고)

---

## 10. API 명세 (초안)

### POST `/api/chat`

**Request**
```json
{
  "sessionId": "string",
  "message": "직장인 신용대출 한도가 얼마야?"
}
```

**성공 Response** (`ApiResponse<T>` 래퍼 사용)
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 처리되었습니다.",
  "data": {
    "sessionId": "string",
    "answer": "string",
    "referencedProducts": ["상품코드..."],
    "fallback": false
  }
}
```

> `fallback: true` = 관련 상품 없음 / 상담사 연결 안내 케이스. (검색 결과가 임계값 미만일 때도 **HTTP 200 + fallback 답변**으로 반환하며, 에러로 처리하지 않는다.)

### 실패 Response

`GlobalExceptionHandler`가 `ApiResponse.error(code, message)` 형태로 반환한다.

```json
{
  "success": false,
  "code": "C001",
  "message": "입력값이 유효하지 않습니다.",
  "data": null
}
```

| 상황 | HTTP | `code` | 비고 |
|------|------|--------|------|
| `message` 누락/공백, 길이 초과 | 400 | `C001` (`INVALID_INPUT`) | `@Valid` 검증 실패 |
| `sessionId` 형식 오류 | 400 | `C001` (`INVALID_INPUT`) | |
| 미인증 사용자 (인증 필요 정책 시) | 401 | `C004` (`UNAUTHORIZED`) | 비로그인도 허용한다면 해당 없음 |
| Gemini API 호출 실패/타임아웃 | 503 | `CHAT001` (`CHAT_LLM_UNAVAILABLE`) | **신규 ErrorCode** — 외부 LLM 장애 |
| Qdrant 검색 실패/연결 불가 | 503 | `CHAT002` (`CHAT_SEARCH_UNAVAILABLE`) | **신규 ErrorCode** — 벡터DB 장애 |
| Gemini API 키 미설정 (챗봇 비활성) | 503 | `CHAT003` (`CHAT_DISABLED`) | `@ConditionalOnProperty` 패턴 |
| 처리 한도 초과 (rate limit) | 429 | `CHAT004` (`CHAT_RATE_LIMITED`) | (선택) 남용 방지 |
| 그 외 내부 오류 | 500 | `C003` (`INTERNAL_SERVER_ERROR`) | |

> **추가할 `ErrorCode` 항목 (Chat 도메인)**
> ```
> CHAT_LLM_UNAVAILABLE   ("CHAT001", "AI 응답 생성에 실패했습니다. 잠시 후 다시 시도해주세요.", SERVICE_UNAVAILABLE)
> CHAT_SEARCH_UNAVAILABLE("CHAT002", "상품 정보 검색에 실패했습니다. 잠시 후 다시 시도해주세요.", SERVICE_UNAVAILABLE)
> CHAT_DISABLED          ("CHAT003", "현재 상담 기능을 사용할 수 없습니다.", SERVICE_UNAVAILABLE)
> CHAT_RATE_LIMITED      ("CHAT004", "요청이 많아 잠시 후 다시 시도해주세요.", TOO_MANY_REQUESTS)
> ```
>
> **설계 원칙**: "관련 상품 없음"은 **정상 흐름(200 + fallback)** 이고, 외부 시스템(Gemini/Qdrant) 장애만 **5xx 에러**로 처리한다. 사용자 입력 문제는 4xx.

---

## 11. 미결정 / TODO

- [ ] 최소 유사도 임계값 결정 (5장)
- [ ] 멀티턴 query rewriting 적용 여부 (6.2)
- [ ] 이력 포함 범위 (전체 vs 최근 N턴)
- [ ] 마스킹/금지표현 필터 적용 시점
- [ ] 응답 형식 구조화 여부 (자유 텍스트 vs 카드형)
- [ ] **상품 수가 적을 경우 벡터DB 없이 전체 주입 방식 대비 비용/정확도 재검토**
- [ ] Qdrant 운영 환경 (로컬 / Docker / 클라우드)

---

## 12. 신규 컴포넌트 (예상)

| 컴포넌트 | 역할 | 비고 |
|----------|------|------|
| `ChatController` | `POST /api/chat` | 신규 |
| `ChatService` | 흐름 ②~⑦ 오케스트레이션 | 신규 |
| `EmbeddingService` | Gemini 임베딩 호출 | 신규 |
| `VectorSearchService` | Qdrant 검색 | 신규 |
| `GeminiClient` / config | Gemini API 연동 | 신규 (`ClaudeConfig` 패턴 참고) |
| `ChatMessage` (entity/repository) | 대화 이력 | 신규 |

> 상품 → 벡터 적재/재임베딩 컴포넌트는 **관리자 API 명세 문서**에서 정의한다.
