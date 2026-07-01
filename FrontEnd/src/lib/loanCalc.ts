// 대출 금리·상환 계산 공유 모듈 — 조건폼(LoanApplyFormPage)과 시뮬레이터(LoanSimulator)가 함께 사용.

/** 상품별 상환방법 + 가능 기간범위(개월). DB OPT_REPAYMENTS(JSON) 매핑 */
export type RepaymentOpt = {
  method: string;
  minM: number;
  maxM: number;
  minIncl: boolean;
};

/** COFIX 기준금리 값(연 %, 2026-06-23 고시). 기준금리 선택에 따라 적용금리가 변동된다. */
export const COFIX_VALUES: Record<string, number> = {
  '신잔액기준 COFIX': 2.5,
  '신규취급액기준 COFIX': 2.9,
};

/** LOAN_LIMIT 파싱 불가(예: LTV %만) 시 시뮬레이터 상한 폴백 */
export const DEFAULT_MAX = 500_000_000;

/** LOAN_LIMIT 텍스트에서 한도 금액(원) 파싱. 조건/최소 괄호는 제외하고 최대 금액을 취함. 없으면 null. */
export function parseLimitWon(raw: string): number | null {
  if (!raw) return null;
  const s = raw.split('(')[0]; // "35백만원(최소 1백만원)" → "35백만원"
  let max = 0;
  const scan = (re: RegExp, unit: number) => {
    for (const m of s.matchAll(re)) max = Math.max(max, parseFloat(m[1]) * unit);
  };
  scan(/(\d+(?:\.\d+)?)\s*억/g, 100_000_000);
  scan(/(\d+(?:\.\d+)?)\s*천만/g, 10_000_000);
  scan(/(\d+(?:\.\d+)?)\s*백만/g, 1_000_000);
  if (max === 0) scan(/(\d+(?:\.\d+)?)\s*만/g, 10_000);
  return max > 0 ? max : null;
}

/** 선택한 상환방법의 가능 대출기간(개월) 범위. minIncl=false → 하한 '초과'(min+1) */
export function rangeForMethod(
  reps: RepaymentOpt[],
  method: string,
  fb: { min: number; max: number },
): { min: number; max: number } {
  const r = reps.find((x) => x.method === method);
  if (!r) return fb;
  return { min: r.minIncl ? r.minM : r.minM + 1, max: r.maxM };
}

/**
 * 기준금리 선택 보정값(delta, %p). base_rate 는 기본(첫) 기준금리 기준이므로
 * 다른 COFIX 선택 시 그 차이만큼 가감. 둘 다 COFIX 값이 있을 때만 적용.
 */
export function cofixDelta(selectedType: string, defaultType: string): number {
  const sel = COFIX_VALUES[selectedType];
  const def = COFIX_VALUES[defaultType];
  return sel != null && def != null ? sel - def : 0;
}

export interface ScheduleRow {
  month: number;
  payment: number; // 해당 회차 총 상환액
  principal: number; // 원금
  interest: number; // 이자
  balance: number; // 상환 후 잔액
}

export interface ScheduleResult {
  rows: ScheduleRow[];
  totalPayment: number;
  totalInterest: number;
  totalPrincipal: number;
  firstPayment: number;
}

const EMPTY: ScheduleResult = {
  rows: [],
  totalPayment: 0,
  totalInterest: 0,
  totalPrincipal: 0,
  firstPayment: 0,
};

/** 상환방식별 월 상환 스케줄 계산 */
export function computeSchedule(
  principal: number,
  annualRatePct: number,
  method: string,
  months: number,
): ScheduleResult {
  const r = annualRatePct / 100 / 12; // 월 이자율
  if (principal <= 0 || months <= 0) return EMPTY;

  const isBullet =
    method.includes('만기일시') ||
    method.includes('종합통장') ||
    method.includes('마이너스');
  const isEqualPrincipal = method.includes('원금균등');
  const annuity =
    r === 0 ? principal / months : (principal * r) / (1 - Math.pow(1 + r, -months));
  const flatPrincipal = principal / months;

  const rows: ScheduleRow[] = [];
  let balance = principal;
  for (let m = 1; m <= months; m += 1) {
    const interest = Math.round(balance * r);
    let princ: number;
    let payment: number;
    if (isBullet) {
      princ = m === months ? balance : 0; // 이자만, 만기에 원금 일시
      payment = interest + princ;
    } else if (isEqualPrincipal) {
      princ = m === months ? balance : Math.round(flatPrincipal);
      payment = princ + interest;
    } else {
      // 원리금균등
      payment = Math.round(annuity);
      princ = m === months ? balance : payment - interest;
      if (m === months) payment = princ + interest;
    }
    balance = Math.max(0, balance - princ);
    rows.push({ month: m, payment, principal: princ, interest, balance });
  }

  const totalInterest = rows.reduce((a, b) => a + b.interest, 0);
  const totalPrincipal = rows.reduce((a, b) => a + b.principal, 0);
  return {
    rows,
    totalInterest,
    totalPrincipal,
    totalPayment: totalInterest + totalPrincipal,
    firstPayment: rows[0]?.payment ?? 0,
  };
}
