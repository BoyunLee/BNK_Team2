import { useEffect, useState } from 'react';
import { Link, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { fetchLoanDetail, type LoanDetail } from '../lib/account';
import { computeSchedule, type ScheduleRow } from '../lib/loanCalc';
import { ApiError } from '../lib/api';
import '../styles/shell.css';
import './MyLoansPage.css';

const won = (n: number) => Math.round(n).toLocaleString('ko-KR');
const rateTypeLabel = (c?: string | null) =>
  c === 'F' ? '고정금리' : c === 'V' ? '변동금리' : '-';
const ymd = (s?: string | null) => (s ? s.slice(0, 10) : '-');

/** 대출기간 문자열("36개월"·"5년" 등)에서 개월 수 파싱. 실패 시 만기-실행일로 계산. */
function loanMonths(period?: string | null, exec?: string | null, maturity?: string | null): number {
  let m = 0;
  const yr = period?.match(/(\d+)\s*년/);
  const mo = period?.match(/(\d+)\s*개월/);
  if (yr) m += Number(yr[1]) * 12;
  if (mo) m += Number(mo[1]);
  if (m > 0) return m;
  if (exec && maturity) {
    const s = new Date(exec);
    const e = new Date(maturity);
    return Math.max(0, (e.getFullYear() - s.getFullYear()) * 12 + (e.getMonth() - s.getMonth()));
  }
  return 0;
}

/** 대출 상세 — 내 대출 목록에서 진입. getLoanDetail(/customers/me/loans/{no}). 로그인 필요. */
export function MyLoanDetailPage() {
  const location = useLocation();
  // 대출 신청번호는 URL이 아닌 라우터 state로 전달(경로에 노출되지 않음)
  const loanAccountNo = (location.state as { loanAccountNo?: string } | null)
    ?.loanAccountNo;
  const { isLoggedIn } = useAuth();
  const [detail, setDetail] = useState<LoanDetail | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isLoggedIn || !loanAccountNo) return;
    fetchLoanDetail(loanAccountNo)
      .then(setDetail)
      .catch((e) =>
        setError(e instanceof ApiError ? e.message : '대출 정보를 불러오지 못했습니다.'),
      );
  }, [isLoggedIn, loanAccountNo]);

  // 비로그인 또는 직접 접근(state 없음) 시 목록으로
  if (!isLoggedIn || !loanAccountNo) {
    return <Navigate to="/my-loans" replace />;
  }

  const c = detail?.contract ?? null;

  // 상환 스케줄(약정 확정 시)
  const months = c
    ? loanMonths(c.loanPeriod, c.executionDate, c.maturityDate)
    : 0;
  const schedule =
    c && c.loanAmount != null && c.finalRate != null && months > 0
      ? computeSchedule(c.loanAmount, c.finalRate, c.repaymentType ?? '', months)
      : null;
  // 표 미리보기: 8회차 이하 전체, 그 이상은 앞 6 + 마지막
  const schedulePreview: (ScheduleRow | null)[] =
    schedule == null
      ? []
      : schedule.rows.length <= 8
        ? schedule.rows
        : [...schedule.rows.slice(0, 6), null, schedule.rows[schedule.rows.length - 1]];

  return (
    <div className="app-shell my-loans">
      <div className="topbar">
        <Link className="topbar__back topbar__back--red" to="/my-loans" aria-label="내 대출로">
          ‹ 내 대출
        </Link>
      </div>
      <h1 className="page-title">대출 상세</h1>

      <main className="ml-main">
        {error ? (
          <p className="ml-empty">{error}</p>
        ) : !detail ? (
          <p className="ml-empty">불러오는 중…</p>
        ) : (
          <>
            <div className="ml-card">
              <div className="ml-card__head">
                <strong>{detail.productName}</strong>
                <span
                  className={`ml-badge${detail.statusCode === '9' ? ' ml-badge--done' : ''}`}
                >
                  {detail.statusName}
                </span>
              </div>
              <dl className="ml-card__rows">
                {c?.loanAmount != null && (
                  <div>
                    <dt>대출금액</dt>
                    <dd>{won(c.loanAmount)}원</dd>
                  </div>
                )}
                {c?.finalRate != null && (
                  <div>
                    <dt>대출금리</dt>
                    <dd>연 {c.finalRate}%</dd>
                  </div>
                )}
                {c?.repaymentType && (
                  <div>
                    <dt>상환방식</dt>
                    <dd>{c.repaymentType}</dd>
                  </div>
                )}
                {c?.rateTypeCode && (
                  <div>
                    <dt>금리유형</dt>
                    <dd>{rateTypeLabel(c.rateTypeCode)}</dd>
                  </div>
                )}
                {c?.loanPeriod && (
                  <div>
                    <dt>대출기간</dt>
                    <dd>{c.loanPeriod}</dd>
                  </div>
                )}
                {c?.maturityDate && (
                  <div>
                    <dt>만기일</dt>
                    <dd>{c.maturityDate}</dd>
                  </div>
                )}
                {c?.executionDate && (
                  <div>
                    <dt>실행일</dt>
                    <dd>{ymd(c.executionDate)}</dd>
                  </div>
                )}
                {c?.loanDepositAccountNo && (
                  <div>
                    <dt>대출계좌</dt>
                    <dd>{c.loanDepositAccountNo}</dd>
                  </div>
                )}
                {c?.depositAccountNo && (
                  <div>
                    <dt>입금계좌</dt>
                    <dd>{c.depositAccountNo}</dd>
                  </div>
                )}
                {c?.fundPurpose && (
                  <div>
                    <dt>자금용도</dt>
                    <dd>{c.fundPurpose}</dd>
                  </div>
                )}
                <div>
                  <dt>신청번호</dt>
                  <dd>{detail.loanAccountNo}</dd>
                </div>
                <div>
                  <dt>신청일</dt>
                  <dd>{ymd(detail.appliedAt)}</dd>
                </div>
              </dl>
            </div>

            {c && c.preferentialRates.length > 0 && (
              <div className="ml-card">
                <div className="ml-card__head">
                  <strong>우대금리</strong>
                </div>
                <dl className="ml-card__rows">
                  {c.preferentialRates.map((p, i) => (
                    <div key={`${p.conditionName}-${i}`}>
                      <dt>{p.conditionName}</dt>
                      <dd>−{p.rateValue}%</dd>
                    </div>
                  ))}
                </dl>
              </div>
            )}

            {schedule && (
              <div className="ml-card">
                <div className="ml-card__head">
                  <strong>상환 스케줄</strong>
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
                    {schedulePreview.map((r, i) =>
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
              </div>
            )}

            {!c && (
              <p className="ml-empty">
                아직 약정 전 단계입니다 (상태: {detail.statusName}).
              </p>
            )}
          </>
        )}
      </main>
    </div>
  );
}
