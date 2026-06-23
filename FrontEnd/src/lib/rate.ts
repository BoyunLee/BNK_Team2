// 금리 값 표기 헬퍼 — 헤더/목록 공용.
// rateMin/rateMax 는 숫자 문자열("6", "4.9") 또는 수식형("수신금리+1.30") 또는 null.

/** 순수 숫자 금리인지(소수 포함). */
export function isNumericRate(s: string): boolean {
  return /^\d+(\.\d+)?$/.test(s.trim());
}

/**
 * 단일 금리 값 표기.
 *  - 숫자: "연 6%"
 *  - 수식형("수신금리+1.30"): "수신금리+1.30%" (연 접두 없음, % 보강)
 */
export function formatRate(value: string): string {
  const v = value.trim();
  if (isNumericRate(v)) return `연 ${v}%`;
  return v.endsWith('%') ? v : `${v}%`;
}

/**
 * 최저~최고 범위 표기(목록 카드용).
 *  - 둘 다 없음: "금리 정보 없음"
 *  - 한쪽만/동일값: 단일 표기
 *  - 서로 다름: "연 4.9% ~ 연 9.8%"
 */
export function formatRateRange(
  min: string | null,
  max: string | null,
): string {
  if (min == null && max == null) return '금리 정보 없음';
  if (min != null && max != null)
    return min === max
      ? formatRate(min)
      : `${formatRate(min)} ~ ${formatRate(max)}`;
  return formatRate((min ?? max) as string);
}
