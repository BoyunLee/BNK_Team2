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
