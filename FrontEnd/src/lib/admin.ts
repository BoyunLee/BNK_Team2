// 관리자(상품 결재) API — 백엔드 /api/v1/admin/** 연동.
// 타입/함수 시그니처는 기존(목업)과 동일하게 유지하여 페이지 변경을 최소화한다.
// 인증은 관리자 세션 쿠키(SESSION_ADMIN_ID) 기반 → credentials: 'include'.

import { ApiError, type ApiEnvelope } from './api';

// ===== 타입 (BE 계약과 동일) =====

export type AdminRole = 'DRAFTER' | 'APPROVER';

export interface AdminUser {
  id: number;
  loginId: string;
  name: string;
  role: AdminRole;
  department: string;
}

export type ChangeType = 'CREATE' | 'UPDATE' | 'DISCONTINUE';

export type ChangeStatus =
  | 'DRAFT'
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED'
  | 'DEPLOYED'
  | 'CANCELLED';

export interface ProductSnapshot {
  productName: string;
  category: string;
  baseRate: string;
  rateMin: string;
  rateMax: string;
  loanPeriod: string;
  catchphrase: string;
  target: string;
  loanLimit: string;
  repayment: string;
  summary: string;
}

export interface ProductChangeRequest {
  id: number;
  changeType: ChangeType;
  productId: number | null;
  title: string;
  asis: ProductSnapshot | null;
  tobe: ProductSnapshot;
  status: ChangeStatus;
  drafterId: number;
  drafterName: string;
  approverId: number | null;
  approverName: string | null;
  decisionComment: string | null;
  scheduledDeployAt: string | null;
  createdAt: string;
  submittedAt: string | null;
  decidedAt: string | null;
  deployedAt: string | null;
}

export interface LiveProduct {
  productId: number;
  snapshot: ProductSnapshot;
  status: 'SALE' | 'DISCONTINUED';
  version: number;
  updatedAt: string;
}

// ===== 라벨 (표시용) =====

export const STATUS_LABEL: Record<ChangeStatus, string> = {
  DRAFT: '작성중',
  PENDING: '결재대기',
  APPROVED: '배포예약',
  REJECTED: '반려',
  DEPLOYED: '배포완료',
  CANCELLED: '취소',
};

export const TYPE_LABEL: Record<ChangeType, string> = {
  CREATE: '신규등록',
  UPDATE: '변경',
  DISCONTINUE: '판매중지',
};

export function emptySnapshot(): ProductSnapshot {
  return {
    productName: '',
    category: '신용',
    baseRate: '',
    rateMin: '',
    rateMax: '',
    loanPeriod: '',
    catchphrase: '',
    target: '',
    loanLimit: '',
    repayment: '',
    summary: '',
  };
}

// ===== 관리자 전용 fetch (고객 apiFetch 와 401 처리 분리) =====

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080';
const ADMIN_SESSION_KEY = 'bnk.admin.session'; // AdminAuthContext STORAGE_KEY 와 동일

async function adminFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
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
    // 본문 없는 정상 응답 무시
  }

  if (!res.ok || !body?.success) {
    const code = body?.code ?? 'C003';
    // 관리자 세션 만료/미인증 → 로컬 미러 정리 후 관리자 로그인으로
    if (res.status === 401) {
      localStorage.removeItem(ADMIN_SESSION_KEY);
      if (!location.pathname.startsWith('/admin/login')) {
        location.assign('/admin/login');
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

const jsonBody = (data: unknown): RequestInit => ({
  method: 'POST',
  body: JSON.stringify(data),
});

// ===== 인증 =====

/** 관리자 로그인 — 세션 쿠키 설정 후 프로필 반환 */
export function adminLogin(loginId: string, password: string): Promise<AdminUser> {
  return adminFetch<AdminUser>('/api/v1/admin/auth/login', jsonBody({ loginId, password }));
}

export function adminLogout(): Promise<void> {
  return adminFetch<void>('/api/v1/admin/auth/logout', { method: 'POST' });
}

export function fetchAdminMe(): Promise<AdminUser> {
  return adminFetch<AdminUser>('/api/v1/admin/auth/me');
}

// ===== 참조 데이터 =====

/** 책임자 목록 */
export function listApprovers(): Promise<AdminUser[]> {
  return adminFetch<AdminUser[]>('/api/v1/admin/approvers');
}

/** 라이브(AS-IS) 상품 목록 */
export function listLiveProducts(): Promise<LiveProduct[]> {
  return adminFetch<LiveProduct[]>('/api/v1/admin/products');
}

// ===== 신청서 =====

export function listChangeRequests(filter?: {
  status?: ChangeStatus;
  approverId?: number;
}): Promise<ProductChangeRequest[]> {
  const qs = new URLSearchParams();
  if (filter?.status) qs.set('status', filter.status);
  if (filter?.approverId != null) qs.set('approverId', String(filter.approverId));
  const q = qs.toString();
  return adminFetch<ProductChangeRequest[]>(
    `/api/v1/admin/change-requests${q ? `?${q}` : ''}`,
  );
}

export function getChangeRequest(id: number): Promise<ProductChangeRequest> {
  return adminFetch<ProductChangeRequest>(`/api/v1/admin/change-requests/${id}`);
}

/** 신청서 작성(DRAFT) — 담당자는 서버 세션에서 결정 */
export function createDraft(input: {
  changeType: ChangeType;
  productId?: number | null;
  title: string;
  tobe: ProductSnapshot;
}): Promise<ProductChangeRequest> {
  return adminFetch<ProductChangeRequest>(
    '/api/v1/admin/change-requests',
    jsonBody({
      changeType: input.changeType,
      productId: input.productId ?? null,
      title: input.title,
      tobe: input.tobe,
    }),
  );
}

/** 작성중/반려 신청서 수정 */
export function updateDraft(
  id: number,
  patch: { title: string; tobe: ProductSnapshot },
): Promise<ProductChangeRequest> {
  return adminFetch<ProductChangeRequest>(`/api/v1/admin/change-requests/${id}`, {
    method: 'PUT',
    body: JSON.stringify(patch),
  });
}

/** 결재상신 — 책임자 지정 */
export function submitForApproval(
  id: number,
  approver: AdminUser,
): Promise<ProductChangeRequest> {
  return adminFetch<ProductChangeRequest>(
    `/api/v1/admin/change-requests/${id}/submit`,
    jsonBody({ approverId: approver.id }),
  );
}

/** 승인 — 배포예약시각 지정 */
export function approve(
  id: number,
  scheduledDeployAt: string,
  comment?: string,
): Promise<ProductChangeRequest> {
  return adminFetch<ProductChangeRequest>(
    `/api/v1/admin/change-requests/${id}/approve`,
    jsonBody({ scheduledDeployAt, comment: comment ?? null }),
  );
}

/** 반려 */
export function reject(id: number, comment: string): Promise<ProductChangeRequest> {
  return adminFetch<ProductChangeRequest>(
    `/api/v1/admin/change-requests/${id}/reject`,
    jsonBody({ comment }),
  );
}

/** 신청 취소 */
export function cancelRequest(id: number): Promise<ProductChangeRequest> {
  return adminFetch<ProductChangeRequest>(
    `/api/v1/admin/change-requests/${id}/cancel`,
    { method: 'POST' },
  );
}

/** 데모용: 즉시 배포(형상이행 강제) */
export function deployNow(id: number): Promise<ProductChangeRequest> {
  return adminFetch<ProductChangeRequest>(
    `/api/v1/admin/change-requests/${id}/deploy-now`,
    { method: 'POST' },
  );
}
