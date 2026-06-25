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

/** 상태머신이 추적하는 BE 서류 6종 */
export const DOCUMENT_TYPES = [
  'ADMIN_INFO_REQUEST',
  'PERSONAL_INFO_CONSENT',
  'MOBILE_AUTH_TERMS',
  'PRODUCT_TERMS',
  'PRODUCT_DESCRIPTION',
  'BOND_CONTRACT',
] as const;

/** 3-1) 공공마이데이터 이용 동의 (status 2→3) */
export const saveMydataConsent = (
  loanAccountNo: string,
  consents: { consentType: string; dataProvider: string }[],
) =>
  apiFetch<void>(
    `/api/v1/loans/applications/${loanAccountNo}/mydata-consent`,
    { method: 'POST', body: JSON.stringify({ consents }) },
  );

/** 3-2) 서류 열람 기록 */
export const viewDocument = (
  loanAccountNo: string,
  productId: number,
  documentType: string,
) =>
  apiFetch<unknown>(
    `/api/v1/loans/applications/${loanAccountNo}/documents/${documentType}/view?productId=${productId}`,
    { method: 'POST' },
  );

/** 3-3) 서류 동의 (열람 후) */
export const agreeDocument = (loanAccountNo: string, documentType: string) =>
  apiFetch<unknown>(
    `/api/v1/loans/applications/${loanAccountNo}/documents/${documentType}/agree`,
    { method: 'POST' },
  );

/** 4) 사전 절차 전자서명 (status 3→4). BE 가 mock 검증이라 placeholder 토큰 사용. */
export const signPreProcess = (loanAccountNo: string) =>
  apiFetch<{ signatureId: number }>(
    `/api/v1/loans/applications/${loanAccountNo}/signatures`,
    {
      method: 'POST',
      body: JSON.stringify({
        signStep: 'PRE_PROCESS',
        signType: 'SIMPLE_CERT',
        tokenId: `DEMO_${Date.now()}`,
        originalValue: '대출 사전절차 동의',
      }),
    },
  );

export interface ScreeningResult {
  maxLimitAmt: number;
  appliedBaseRate: number;
  result: 'APPROVED' | 'REJECTED';
}

/** 6-1) 대출 한도 산출 (status 5→6). MIN(연소득×0.5, 1억) */
export const runScreening = (loanAccountNo: string) =>
  apiFetch<ScreeningResult>(
    `/api/v1/loans/applications/${loanAccountNo}/screening`,
    { method: 'POST' },
  );

/** 6-2) 한도 조회 결과 재확인 (GET, 상태 무관) */
export const getScreening = (loanAccountNo: string) =>
  apiFetch<ScreeningResult>(
    `/api/v1/loans/applications/${loanAccountNo}/screening`,
  );

export interface IncomeRequest {
  companyName: string;
  jobType: string;
  employmentType: string;
  annualIncome: number;
}

// 소득구분(최상위)별 연소득 고정값 — 한도 산출(MIN(연소득×0.5, 1억))에 사용
const ANNUAL_INCOME_BY_ROOT: Record<string, number> = {
  급여소득자: 45_000_000,
  사업소득자: 60_000_000,
  기타소득자: 24_000_000,
};
export const annualIncomeFor = (root: string): number =>
  ANNUAL_INCOME_BY_ROOT[root] ?? 30_000_000;

/** 5) 직장·소득정보 입력 (status 4→5) */
export const saveIncome = (loanAccountNo: string, req: IncomeRequest) =>
  apiFetch<void>(
    `/api/v1/loans/applications/${loanAccountNo}/income`,
    { method: 'POST', body: JSON.stringify(req) },
  );

/** 대출한도조회 동의 묶음: 마이데이터 동의 + 서류 6종 열람/동의 (status 2→3) */
export async function completeLimitConsent(
  loanAccountNo: string,
  productId: number,
): Promise<void> {
  await saveMydataConsent(loanAccountNo, [
    { consentType: 'ADMIN_INFO', dataProvider: '행정안전부' },
    { consentType: 'MYDATA_USE', dataProvider: '공공마이데이터포털' },
  ]);
  for (const type of DOCUMENT_TYPES) {
    await viewDocument(loanAccountNo, productId, type);
    await agreeDocument(loanAccountNo, type);
  }
}
