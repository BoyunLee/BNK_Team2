// 백엔드(Spring Boot) 호출 공용 클라이언트.
// - 세션 쿠키 기반 인증이므로 모든 요청에 credentials: 'include'.
// - 공통 응답 봉투 { success, code, message, data } 를 풀어 data 만 반환한다.
// - 실패 시 ApiError(code, status, message) 를 던진다.

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080';

export interface ApiEnvelope<T> {
  success: boolean;
  code: string;
  message: string;
  data?: T;
}

export class ApiError extends Error {
  code: string;
  status: number;
  constructor(message: string, code: string, status: number) {
    super(message);
    this.name = 'ApiError';
    this.code = code;
    this.status = status;
  }
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  let res: Response;
  try {
    res = await fetch(`${API_BASE}${path}`, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json', ...(options.headers ?? {}) },
      ...options,
    });
  } catch {
    throw new ApiError(
      '서버에 연결할 수 없습니다. 백엔드 실행 여부를 확인해주세요.',
      'NETWORK',
      0,
    );
  }

  let body: ApiEnvelope<T> | null = null;
  try {
    body = (await res.json()) as ApiEnvelope<T>;
  } catch {
    // 본문이 비어있는 정상 응답(예: 204)도 있으므로 무시
  }

  if (!res.ok || !body?.success) {
    const code = body?.code ?? 'C003';
    // 세션 만료/인증 필요 → 로컬 세션 정리 후 로그인으로. (로그인 실패 AUTH004 는 제외)
    if (res.status === 401 && (code === 'AUTH005' || code === 'C004')) {
      localStorage.removeItem('bnk.auth'); // AuthContext STORAGE_KEY 와 동일
      if (!location.pathname.startsWith('/login')) {
        location.assign('/login');
      }
    }
    throw new ApiError(
      body?.message ?? `요청에 실패했습니다 (HTTP ${res.status})`,
      code,
      res.status,
    );
  }
  return body.data as T;
}
