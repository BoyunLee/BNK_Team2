import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { computeSchedule, type ScheduleRow } from '../lib/loanCalc';
import '../styles/shell.css';
import './MyLoansPage.css';
import './LoanCalculatorPage.css';

const won = (n: number) => Math.round(n).toLocaleString('ko-KR');

const METHODS = ['원리금균등상환', '원금균등상환', '만기일시상환'] as const;

/**
 * 대출계산기 — 상품과 무관하게 대출금액·기간·금리·상환방법을 직접 입력받아
 * 월 상환 스케줄을 계산한다. (공유 모듈 computeSchedule 재사용)
 */
export function LoanCalculatorPage() {
  const [amountStr, setAmountStr] = useState(''); // 숫자만(최대 12자리)
  const [monthsStr, setMonthsStr] = useState(''); // 개월(최대 3자리)
  const [rateStr, setRateStr] = useState('');
  const [method, setMethod] = useState<string>(METHODS[0]);
  const [showAll, setShowAll] = useState(false);

  const amount = Number(amountStr || 0);
  const months = Number(monthsStr || 0);
  const rate = Number(rateStr || 0);

  const schedule = useMemo(
    () =>
      amount > 0 && months > 0 && rate >= 0
        ? computeSchedule(amount, rate, method, months)
        : null,
    [amount, months, rate, method],
  );

  const rows = schedule?.rows ?? [];
  // 8회차 이하는 전체, 초과 시 앞 6 + 마지막(‘전체 보기’로 펼침)
  const preview: (ScheduleRow | null)[] =
    showAll || rows.length <= 8
      ? rows
      : [...rows.slice(0, 6), null, rows[rows.length - 1]];

  const onAmount = (e: React.ChangeEvent<HTMLInputElement>) =>
    setAmountStr(e.target.value.replace(/\D/g, '').slice(0, 12));
  const onMonths = (e: React.ChangeEvent<HTMLInputElement>) =>
    setMonthsStr(e.target.value.replace(/\D/g, '').slice(0, 3));
  const onRate = (e: React.ChangeEvent<HTMLInputElement>) => {
    // 숫자·소수점만, 소수점 1개로 제한
    const cleaned = e.target.value.replace(/[^\d.]/g, '');
    const parts = cleaned.split('.');
    setRateStr(parts.length > 2 ? `${parts[0]}.${parts.slice(1).join('')}` : cleaned);
  };

  return (
    <div className="app-shell my-loans">
      <div className="topbar">
        <Link className="topbar__back topbar__back--red" to="/" aria-label="홈으로">
          ‹ 홈
        </Link>
      </div>
      <h1 className="page-title">대출계산기</h1>

      <main className="ml-main">
        <div className="ml-card">
          <div className="calc-field">
            <label className="calc-label" htmlFor="calc-amount">
              대출금액
            </label>
            <div className="calc-input">
              <input
                id="calc-amount"
                inputMode="numeric"
                placeholder="0"
                value={amountStr ? Number(amountStr).toLocaleString('ko-KR') : ''}
                onChange={onAmount}
              />
              <span className="calc-unit">원</span>
            </div>
          </div>

          <div className="calc-field">
            <label className="calc-label" htmlFor="calc-months">
              대출기간
            </label>
            <div className="calc-input">
              <input
                id="calc-months"
                inputMode="numeric"
                placeholder="0"
                value={monthsStr}
                onChange={onMonths}
              />
              <span className="calc-unit">개월</span>
            </div>
          </div>

          <div className="calc-field">
            <label className="calc-label" htmlFor="calc-rate">
              대출금리
            </label>
            <div className="calc-input">
              <input
                id="calc-rate"
                inputMode="decimal"
                placeholder="0.0"
                value={rateStr}
                onChange={onRate}
              />
              <span className="calc-unit">%</span>
            </div>
          </div>

          <div className="calc-field">
            <span className="calc-label">상환방법</span>
            <div className="calc-methods" role="radiogroup" aria-label="상환방법">
              {METHODS.map((m) => (
                <button
                  key={m}
                  type="button"
                  role="radio"
                  aria-checked={method === m}
                  className={`calc-method${method === m ? ' is-active' : ''}`}
                  onClick={() => setMethod(m)}
                >
                  {m.replace('상환', '')}
                </button>
              ))}
            </div>
          </div>
        </div>

        {schedule ? (
          <div className="ml-card">
            <div className="ml-card__head">
              <strong>상환 결과</strong>
            </div>
            <dl className="ml-card__rows">
              <div>
                <dt>총 상환액</dt>
                <dd>{won(schedule.totalPayment)}원</dd>
              </div>
              <div>
                <dt>총 이자</dt>
                <dd>{won(schedule.totalInterest)}원</dd>
              </div>
              <div>
                <dt>1회차 상환액</dt>
                <dd>{won(schedule.firstPayment)}원</dd>
              </div>
            </dl>

            <table className="ml-table">
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
                    <tr key={`gap-${i}`} className="ml-table__gap">
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

            {rows.length > 8 && (
              <button
                type="button"
                className="calc-toggle"
                onClick={() => setShowAll((v) => !v)}
              >
                {showAll ? '접기' : `전체 ${rows.length}회차 보기`}
              </button>
            )}
          </div>
        ) : (
          <p className="ml-empty">
            대출금액과 기간, 금리를 입력하면 상환 결과가 표시됩니다.
          </p>
        )}
      </main>
    </div>
  );
}
