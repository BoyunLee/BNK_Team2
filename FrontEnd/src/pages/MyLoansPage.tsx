import { useEffect, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { fetchMyLoans, type LoanSummary } from '../lib/account';
import '../styles/shell.css';
import './MyLoansPage.css';

const won = (n: number) => Math.round(n).toLocaleString('ko-KR');

/**
 * 내 대출 현황 — 홈 "이번달 상환관리" 진입점. 로그인 필요.
 * getMyLoans(/customers/me/loans)로 신청/실행 대출을 목록으로 보여준다.
 */
export function MyLoansPage() {
  const { isLoggedIn } = useAuth();
  const [loans, setLoans] = useState<LoanSummary[] | null>(null);

  useEffect(() => {
    if (!isLoggedIn) return;
    fetchMyLoans()
      .then(setLoans)
      .catch(() => setLoans([]));
  }, [isLoggedIn]);

  // 비로그인 시 로그인으로(로그인 후 이 페이지로 복귀)
  if (!isLoggedIn) {
    return <Navigate to="/login" state={{ from: '/my-loans' }} replace />;
  }

  return (
    <div className="app-shell my-loans">
      <div className="topbar">
        <Link className="topbar__back topbar__back--red" to="/" aria-label="홈으로">
          ‹ 홈
        </Link>
      </div>
      <h1 className="page-title">내 대출</h1>

      <main className="ml-main">
        {loans == null ? (
          <p className="ml-empty">불러오는 중…</p>
        ) : loans.length === 0 ? (
          <p className="ml-empty">신청한 대출이 없습니다.</p>
        ) : (
          <ul className="ml-list">
            {[...loans]
              .sort(
                (a, b) =>
                  (b.statusCode === '9' ? 1 : 0) - (a.statusCode === '9' ? 1 : 0),
              )
              .map((l) => (
              <li key={l.loanAccountNo}>
                <Link
                  className="ml-card"
                  to="/my-loans/detail"
                  state={{ loanAccountNo: l.loanAccountNo }}
                >
                <div className="ml-card__head">
                  <strong>{l.productName}</strong>
                  <span
                    className={`ml-badge${l.statusCode === '9' ? ' ml-badge--done' : ''}`}
                  >
                    {l.statusName}
                  </span>
                </div>
                <dl className="ml-card__rows">
                  {l.loanAmount != null && (
                    <div>
                      <dt>대출금액</dt>
                      <dd>{won(l.loanAmount)}원</dd>
                    </div>
                  )}
                  {l.finalRate != null && (
                    <div>
                      <dt>대출금리</dt>
                      <dd>연 {l.finalRate}%</dd>
                    </div>
                  )}
                  {l.maturityDate && (
                    <div>
                      <dt>만기일</dt>
                      <dd>{l.maturityDate}</dd>
                    </div>
                  )}
                  {l.loanDepositAccountNo && (
                    <div>
                      <dt>대출계좌</dt>
                      <dd>{l.loanDepositAccountNo}</dd>
                    </div>
                  )}
                  <div>
                    <dt>신청번호</dt>
                    <dd>{l.loanAccountNo}</dd>
                  </div>
                </dl>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </main>
    </div>
  );
}
