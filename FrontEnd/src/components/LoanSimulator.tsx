import { useEffect, useMemo, useState } from 'react';
import type { BeProductDetail } from '../lib/products';
import {
  cofixDelta,
  computeSchedule,
  rangeForMethod,
  parseLimitWon,
  DEFAULT_MAX,
  type RepaymentOpt,
} from '../lib/loanCalc';
// 신청폼과 동일한 디자인 토큰/클래스(lf-*, field) 재사용 → 상세페이지에서도 로드되도록 명시 import
import '../pages/apply/apply.css';
import '../pages/apply/loanform.css';
import './LoanSimulator.css';

const won = (n: number) => Math.round(n).toLocaleString('ko-KR');
const PRESETS = [10_000_000, 30_000_000, 50_000_000, 100_000_000];
const presetLabel = (p: number) =>
  p >= 100_000_000 ? `${p / 100_000_000}억` : `${p / 10_000_000}천만`;

/**
 * 상품 상세페이지 "시뮬레이터" 탭. 실제 상품 데이터(상환방법별 기간범위·기준금리(COFIX)·
 * 우대금리·base_rate)로 최종금리·월상환액·총이자·상환스케줄을 계산한다. (조회/계산 전용)
 * 기존 대출신청 폼과 동일한 디자인 클래스(lf-*)를 재사용해 페이지와 일관성을 맞춘다.
 */
export function LoanSimulator({ detail }: { detail: BeProductDetail }) {
  const byKey = (k: string) =>
    detail.descriptions.find((x) => x.attrKey === k)?.attrValue ?? '';

  const repayments: RepaymentOpt[] = useMemo(() => {
    try {
      return JSON.parse(byKey('OPT_REPAYMENTS') || '[]');
    } catch {
      return [];
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [detail]);
  const rateTypes = useMemo(
    () =>
      byKey('OPT_RATE_TYPE')
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [detail],
  );
  const prefRates = detail.preferentialRates ?? [];
  const baseRate = detail.baseRate ?? 0; // 상품 기본(최고) 금리 — 기본 기준금리 기준
  const methods = repayments.length
    ? repayments.map((r) => r.method)
    : ['원리금균등상환'];
  // 상품 한도(LOAN_LIMIT) 연동 상한
  const cap = useMemo(
    () => parseLimitWon(byKey('LOAN_LIMIT')) ?? DEFAULT_MAX,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [detail],
  );

  const [method, setMethod] = useState(methods[0]);
  const [baseRateType, setBaseRateType] = useState(rateTypes[0] ?? '');
  const [prefs, setPrefs] = useState<number[]>([]);
  const [amount, setAmount] = useState(() => Math.min(30_000_000, cap));

  const range = rangeForMethod(repayments, method, { min: 6, max: 60 });
  const [months, setMonths] = useState(range.min);

  useEffect(() => {
    setMethod(methods[0]);
    setBaseRateType(rateTypes[0] ?? '');
    setPrefs([]);
    setAmount(Math.min(30_000_000, cap));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [detail]);

  useEffect(() => {
    setMonths((m) => Math.min(range.max, Math.max(range.min, m)));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [range.min, range.max]);

  const prefSum = prefRates
    .filter((p) => prefs.includes(p.preferentialId))
    .reduce((a, b) => a + b.rateValue, 0);
  const delta = cofixDelta(baseRateType, rateTypes[0] ?? '');
  const finalRate = Math.max(0.1, baseRate + delta - prefSum);

  const schedule = useMemo(
    () => computeSchedule(amount, finalRate, method, months),
    [amount, finalRate, method, months],
  );

  const togglePref = (id: number) =>
    setPrefs((p) => (p.includes(id) ? p.filter((x) => x !== id) : [...p, id]));
  const clampMonths = (m: number) =>
    setMonths(Math.min(range.max, Math.max(range.min, m)));

  const principalPct = schedule.totalPayment
    ? (schedule.totalPrincipal / schedule.totalPayment) * 100
    : 0;
  const fillPct =
    range.max > range.min
      ? ((months - range.min) / (range.max - range.min)) * 100
      : 100;

  const preview: (typeof schedule.rows[number] | null)[] =
    schedule.rows.length <= 8
      ? schedule.rows
      : [...schedule.rows.slice(0, 6), null, schedule.rows[schedule.rows.length - 1]];

  return (
    <div className="sim">
      <p className="lf-help">
        조건을 바꿔가며 예상 <b>월 상환액</b>과 <b>총 이자</b>를 확인해보세요.
      </p>

      {/* 대출금액 */}
      <div className="field">
        <div className="field__label">대출금액</div>
        <div className="lf-amount">
          <input
            className="lf-amount__input"
            inputMode="numeric"
            value={won(amount)}
            onChange={(e) =>
              setAmount(Math.min(cap, Number(e.target.value.replace(/[^\d]/g, '')) || 0))
            }
          />
          <span className="lf-amount__unit">원</span>
        </div>
        <div className="sim-chips">
          {PRESETS.filter((p) => p < cap).map((p) => (
            <button
              key={p}
              type="button"
              className={`sim-chip${amount === p ? ' is-on' : ''}`}
              onClick={() => setAmount(p)}
            >
              {presetLabel(p)}
            </button>
          ))}
          <button
            type="button"
            className={`sim-chip${amount === cap ? ' is-on' : ''}`}
            onClick={() => setAmount(cap)}
          >
            최대
          </button>
        </div>
        <p className="sim-caphint">상품 한도 최대 {won(cap)}원</p>
      </div>

      {/* 상환방법 */}
      <div className="field">
        <div className="field__label">상환방법</div>
        <ul className="lf-radio">
          {methods.map((m) => (
            <li key={m}>
              <button
                type="button"
                className="lf-radio__btn"
                aria-pressed={method === m}
                onClick={() => setMethod(m)}
              >
                <span className="lf-radio__mark" aria-hidden="true">
                  ✓
                </span>
                {m}
              </button>
            </li>
          ))}
        </ul>
      </div>

      {/* 대출기간 — 1개월 단위 슬라이더 */}
      <div className="field">
        <div className="field__label">대출기간</div>
        <div className="sim-slider">
          <div className="sim-slider__head">
            <input
              className="sim-slider__input"
              inputMode="numeric"
              value={months}
              onChange={(e) =>
                setMonths(
                  Math.min(range.max, Number(e.target.value.replace(/[^\d]/g, '')) || 0),
                )
              }
              onBlur={() => clampMonths(months)}
              aria-label="대출기간(개월)"
            />
            <span className="sim-slider__unit">개월</span>
          </div>
          <input
            className="sim-slider__range"
            type="range"
            min={range.min}
            max={range.max}
            step={1}
            value={months}
            onChange={(e) => setMonths(Number(e.target.value))}
            style={{
              background: `linear-gradient(to right, var(--bnk-red) ${fillPct}%, var(--line-strong) ${fillPct}%)`,
            }}
          />
          <div className="sim-slider__ticks">
            <span>{range.min}개월</span>
            <span>{range.max}개월</span>
          </div>
        </div>
      </div>

      {/* 기준금리 */}
      {rateTypes.length > 0 && (
        <div className="field">
          <div className="field__label">기준금리</div>
          <ul className="lf-radio">
            {rateTypes.map((rt) => {
              const d = cofixDelta(rt, rateTypes[0]);
              return (
                <li key={rt}>
                  <button
                    type="button"
                    className="lf-radio__btn"
                    aria-pressed={baseRateType === rt}
                    onClick={() => setBaseRateType(rt)}
                  >
                    <span className="lf-radio__mark" aria-hidden="true">
                      ✓
                    </span>
                    {rt}
                    {d !== 0 && (
                      <em className="sim-delta">
                        {d > 0 ? '+' : ''}
                        {d.toFixed(2)}%p
                      </em>
                    )}
                  </button>
                </li>
              );
            })}
          </ul>
        </div>
      )}

      {/* 우대금리 */}
      <div className="field">
        <div className="lf-prefsum">
          <span>우대금리 합계</span>
          <b>−{prefSum.toFixed(2)}%</b>
        </div>
        {prefRates.length === 0 ? (
          <p className="lf-help">선택 가능한 우대금리가 없는 상품입니다.</p>
        ) : (
          <ul className="lf-check">
            {prefRates.map((p) => (
              <li key={p.preferentialId}>
                <button
                  type="button"
                  className="lf-check__btn"
                  aria-pressed={prefs.includes(p.preferentialId)}
                  onClick={() => togglePref(p.preferentialId)}
                >
                  <span className="lf-check__mark" aria-hidden="true">
                    ✓
                  </span>
                  <span className="lf-check__label">
                    {p.conditionName}{' '}
                    <b className="lf-rate">−{p.rateValue.toFixed(2)}%</b>
                  </span>
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>

      {/* 예상 적용금리 */}
      <div className="lf-summary">
        <div className="lf-summary__row">
          <span className="lf-summary__k">예상 적용금리</span>
          <span className="lf-summary__v">
            연 <b className="lf-rate">{finalRate.toFixed(2)}</b>%
          </span>
        </div>
        <div className="lf-summary__row lf-summary__row--sub">
          <span />
          <span className="lf-summary__sub">
            {baseRateType || '기준금리'} {baseRate.toFixed(2)}%
            {delta !== 0 && ` (${delta > 0 ? '+' : ''}${delta.toFixed(2)})`} − 우대{' '}
            {prefSum.toFixed(2)}%
          </span>
        </div>
      </div>

      {/* 예상 상환 요약 */}
      <div className="lf-deposit">
        <div className="lf-deposit__top">
          <div className="lf-deposit__k">월 상환액 (1회차)</div>
          <div className="lf-deposit__amt">
            <b>{won(schedule.firstPayment)}</b> 원
          </div>
        </div>
        <dl className="lf-deposit__list">
          <div>
            <dt>총 상환액</dt>
            <dd>{won(schedule.totalPayment)} 원</dd>
          </div>
          <div>
            <dt>원금</dt>
            <dd>{won(schedule.totalPrincipal)} 원</dd>
          </div>
          <div>
            <dt>총 이자</dt>
            <dd>{won(schedule.totalInterest)} 원</dd>
          </div>
        </dl>
        <div className="sim-bar" aria-hidden="true">
          <span className="sim-bar__p" style={{ width: `${principalPct}%` }} />
          <span className="sim-bar__i" style={{ width: `${100 - principalPct}%` }} />
        </div>
        <div className="sim-bar__legend">
          <span>
            <i className="sim-dot sim-dot--p" /> 원금
          </span>
          <span>
            <i className="sim-dot sim-dot--i" /> 이자
          </span>
        </div>
      </div>

      {/* 월별 상환 스케줄 */}
      {schedule.rows.length > 0 && (
        <div className="field">
          <div className="field__label">월별 상환 스케줄</div>
          <table className="sim-table">
            <thead>
              <tr>
                <th>회차</th>
                <th>상환액</th>
                <th>원금</th>
                <th>이자</th>
                <th>잔액</th>
              </tr>
            </thead>
            <tbody>
              {preview.map((r, i) =>
                r == null ? (
                  <tr key={`gap-${i}`} className="sim-table__gap">
                    <td colSpan={5}>⋯</td>
                  </tr>
                ) : (
                  <tr key={r.month}>
                    <td>{r.month}</td>
                    <td>{won(r.payment)}</td>
                    <td>{won(r.principal)}</td>
                    <td>{won(r.interest)}</td>
                    <td>{won(r.balance)}</td>
                  </tr>
                ),
              )}
            </tbody>
          </table>
        </div>
      )}

      <p className="lf-help sim-note">
        ※ 실제 금리·상환액은 심사 결과 및 시장금리 변동에 따라 달라질 수 있는 참고용 계산입니다.
      </p>
    </div>
  );
}
