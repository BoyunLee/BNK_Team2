// 대출 신청 9단계 상태머신 API. 페이지별 연동을 진행하며 함수를 점진적으로 추가한다.
import { apiFetch } from './api';

export interface CreateApplicationResult {
  loanAccountNo: string;
  expireAt: string;
}

/** 1) 신청서 생성 (productId) → loanAccountNo (status 1) */
export const createApplication = (productId: number) =>
  apiFetch<CreateApplicationResult>('/api/v1/loans/applications', {
    method: 'POST',
    body: JSON.stringify({ productId }),
  });

/** 진행 중 신청서 취소 (재진입/재시작용) */
export const cancelApplication = (loanAccountNo: string) =>
  apiFetch<void>(
    `/api/v1/loans/applications/${loanAccountNo}/cancel`,
    { method: 'PATCH' },
  );

/** 2) 본인인증 (적합성·적정성 완료 처리) — 간편비밀번호 (status 1→2) */
export const verifySuitability = (loanAccountNo: string, simplePassword: string) =>
  apiFetch<void>(
    `/api/v1/loans/applications/${loanAccountNo}/verification/suitability`,
    { method: 'POST', body: JSON.stringify({ simplePassword }) },
  );
