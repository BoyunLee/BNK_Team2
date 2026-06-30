# 도메인(bnk.nextrplue.dev) + HTTPS 배포 가이드

구성: **브라우저 → Cloudflare(엣지 TLS) → GCP VM nginx(443, Origin Certificate) → backend(8080)**
SSL 모드: **Full (strict)** — 종단간 암호화, Origin Certificate 는 15년 유효(자동 갱신 불필요).

프론트엔드는 운영 빌드에서 `/api` **상대경로**로 호출하므로 도메인이 바뀌어도 코드 수정이 없습니다.

---

## 1. GCP VM 고정(static) 외부 IP 확보

DNS 가 가리킬 IP 가 재부팅·재생성 시 바뀌면 안 되므로 ephemeral IP 를 static 으로 승격합니다.

```bash
# 프로젝트/리전/존, VM 이름은 본인 환경에 맞게
gcloud compute instances list           # VM 이름·존·현재 외부 IP 확인

# 현재 VM 에 붙은 ephemeral IP 를 static 으로 승격 (가장 간단, IP 안 바뀜)
gcloud compute addresses create bnk-ip \
  --addresses=<현재_외부_IP> --region=<리전>

# 확인
gcloud compute addresses describe bnk-ip --region=<리전> --format='value(address)'
```

> VM 콘솔에서 해도 됩니다: VPC 네트워크 → IP 주소 → 해당 IP 의 "유형"을 임시→고정 으로 변경.

확보한 **고정 IP** 를 메모해 둡니다. (이하 `<VM_IP>`)

## 2. GCP 방화벽: 443 허용

```bash
# 443 허용 (가능하면 Cloudflare 대역으로 source 제한 — 아래 5번 참고)
gcloud compute firewall-rules create allow-https \
  --direction=INGRESS --action=ALLOW --rules=tcp:443 \
  --source-ranges=0.0.0.0/0 --network=default
```

기존 80 규칙은 80→443 리다이렉트용으로 유지(또는 Cloudflare 대역으로 제한)합니다.

## 3. Cloudflare 에 도메인 등록 + 네임서버 변경

1. Cloudflare 가입 → **Add a site** → `nextrplue.dev` 입력 → Free 플랜. (사이트는 루트 도메인 단위로 추가하고, 실제 서비스는 아래 `bnk` 서브도메인만 사용. 이미 Cloudflare 에 등록돼 있다면 이 단계 생략)
2. Cloudflare 가 알려주는 **네임서버 2개**(예: `xxx.ns.cloudflare.com`)를 도메인 등록업체(레지스트라)의 네임서버 설정에 입력.
3. 전파(보통 수분~수시간) 후 Cloudflare 대시보드에 "Active" 표시.

## 4. DNS 레코드 추가 (프록시 ON)

Cloudflare → **DNS → Records**:

| Type | Name | Content | Proxy |
|------|------|---------|-------|
| A | `bnk` | `<VM_IP>` | **Proxied(주황 구름)** |

> `bnk` 만 추가합니다. 이미 사용 중인 `cloud`·apex(`nextrplue.dev`) 레코드는 건드리지 않습니다. 최종 주소는 `https://bnk.nextrplue.dev`.

> 주황 구름(Proxied)이어야 Cloudflare 가 TLS 종단·캐시·DDoS 보호를 합니다. 회색이면 그냥 DNS 만.

## 5. Origin Certificate 발급 → 서버에 설치

Cloudflare → **SSL/TLS → Origin Server → Create Certificate**
- Private key type: RSA (또는 ECC)
- Hostnames: `bnk.nextrplue.dev` (또는 `*.nextrplue.dev` 와일드카드로 한 번에)
- 발급되면 **Origin Certificate** 와 **Private Key** 두 블록이 나옵니다.

> 기존 서비스(cloud 등)용으로 이미 `*.nextrplue.dev` Origin Certificate 가 있다면 그 인증서를 재사용해도 됩니다(와일드카드가 `bnk` 도 포함).

VM(프로젝트 루트)에서:

```bash
mkdir -p certs
nano certs/origin.pem   # Origin Certificate 블록 붙여넣기
nano certs/origin.key   # Private Key 블록 붙여넣기
chmod 600 certs/origin.key
```

> `certs/` 는 `.gitignore` 에 등록되어 있어 절대 커밋되지 않습니다.

(권장) GCP 방화벽 443/80 source 를 **Cloudflare IP 대역**으로만 제한하면 origin 직접 접근을 차단할 수 있습니다. 대역: https://www.cloudflare.com/ips/

## 6. Cloudflare SSL 모드 설정

Cloudflare → **SSL/TLS → Overview → Full (strict)** 선택.
→ **Edge Certificates → Always Use HTTPS: ON** (http 접근을 https 로 리다이렉트).

## 7. 서버에서 운영 구성으로 기동

```bash
# 프로젝트 루트 (.env, certs/origin.pem, certs/origin.key 준비된 상태)
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build
```

이 오버레이가 적용하는 것:
- frontend: 443 포트 노출, `nginx.prod.conf`(TLS+리다이렉트) 와 `certs/` 마운트
- backend: `SESSION_COOKIE_SECURE=true` → 세션 쿠키 Secure 속성(HTTPS 전용)

## 8. 확인

```bash
curl -I https://bnk.nextrplue.dev        # 200, server: cloudflare
curl -I http://bnk.nextrplue.dev         # 301 → https
```

브라우저에서 `https://bnk.nextrplue.dev` 접속 → 자물쇠 표시, 로그인/상품/챗봇 정상 동작 확인.

---

## 변경된 파일
- `BackEnd/.../application.yml` — 세션 쿠키 `secure: ${SESSION_COOKIE_SECURE:false}`
- `BackEnd/.../WebMvcConfig.java` — CORS 에 `nextrplue.dev` 추가
- `FrontEnd/nginx.prod.conf` — 운영용 TLS nginx 설정(신규)
- `docker-compose.prod.yml` — 운영 오버레이(신규)
- `.gitignore` — `certs/`, `*.pem`, `*.key` 제외

## 로컬 개발은 그대로
기존 `docker compose up -d` (베이스 compose, 평문 80)는 변동 없습니다. 운영 HTTPS 는 `-f docker-compose.prod.yml` 오버레이를 추가할 때만 적용됩니다.

## 대안: Flexible 모드 (서버 변경 최소, 비권장)
인증서 설치 없이 빠르게 하려면 Cloudflare SSL 모드를 **Flexible** 로 두면 됩니다(브라우저↔CF 만 HTTPS, CF↔origin 은 평문 80). 단 origin 구간이 암호화되지 않고 origin:80 이 공개 노출되므로, 금융성 데모에는 위 **Full (strict)** 를 권장합니다.
