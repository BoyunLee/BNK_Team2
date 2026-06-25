import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useApply } from '../../auth/ApplyContext';
import { useAuth } from '../../auth/AuthContext';
import { useApplyExit } from './useApplyExit';
import {
  runScreening,
  getScreening,
  type ScreeningResult,
} from '../../lib/loan';
import { ApiError } from '../../lib/api';
import '../../styles/shell.css';
import './apply.css';
import './loan.css';

type Tab = '일반신용' | '마이너스통장';

/** 한도/금리 조회 — BE screening 산출(status 5→6) 후 결과 표시. */
export function LoanResultPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const { loanAccountNo, screening: savedScreening, setScreening } = useApply();
  const { customer } = useAuth();
  const productCd = mkpdCd ?? '';

  // 한도조회 결과는 보존 — 나가도 신청서를 취소하지 않고, 재진입 시 이어볼 수 있다.
  const { requestExit: leave, exitModal: leaveModal } = useApplyExit(productCd, {
    preserve: true,
  });

  // 이미 조회한 결과가 있으면(이탈 후 재진입) 로딩 없이 결과로 이어본다
  const [progress, setProgress] = useState(savedScreening ? 100 : 0);
  const [tab, setTab] = useState<Tab>('일반신용');
  const [screening, setLocalScreening] = useState<ScreeningResult | null>(savedScreening);
  const [error, setError] = useState('');

  useEffect(() => {
    if (savedScreening) return; // 저장된 결과로 이어보기 — 애니메이션 생략
    const iv = setInterval(() => {
      setProgress((p) => {
        if (p >= 100) {
          clearInterval(iv);
          return 100;
        }
        return p + 2;
      });
    }, 45);
    return () => clearInterval(iv);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // 한도 산출 호출 (이미 산출된 상태면 GET 으로 폴백)
  // StrictMode 이중 실행/재진입 시 중복 산출 방지 — 한 번만 호출
  const ranRef = useRef(false);
  useEffect(() => {
    if (savedScreening) return; // 저장된 결과 사용 — 재호출 안 함
    if (!loanAccountNo) {
      setError('신청서 정보가 없습니다. 처음부터 다시 진행해주세요.');
      return;
    }
    if (ranRef.current) return;
    ranRef.current = true;
    (async () => {
      try {
        let r: ScreeningResult;
        try {
          r = await runScreening(loanAccountNo);
        } catch (e) {
          if (e instanceof ApiError && e.code === 'LOAN004') {
            r = await getScreening(loanAccountNo); // 이미 산출됨
          } else throw e;
        }
        setLocalScreening(r);
        setScreening(r);
      } catch (e) {
        setError(e instanceof ApiError ? e.message : '한도 조회에 실패했습니다.');
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loanAccountNo]);

  if (error) {
    return (
      <div className="app-shell">
        <header className="flow-head flow-head--col">
          <button type="button" className="flow-head__back" onClick={leave}>
            ‹ 뒤로가기
          </button>
        </header>
        <h1 className="page-title">대출한도조회</h1>
        <div className="list-error" style={{ padding: '48px 24px' }}>
          <h2>한도 조회 실패</h2>
          <p>{error}</p>
        </div>
        {leaveModal}
      </div>
    );
  }

  if (progress < 100 || !screening) {
    const phase1 = progress < 50;
    return (
      <div className="app-shell">
        <header className="flow-head flow-head--col">
          <button type="button" className="flow-head__back" onClick={leave}>
            ‹ 뒤로가기
          </button>
        </header>
        <h1 className="page-title">대출한도조회</h1>
        <div className="loan-loading">
          <h1 className="loan-loading__title">
            {phase1 ? (
              <>
                <strong>1만원이라도 높은 한도</strong>의
                <br />
                조건을 찾고있습니다.
              </>
            ) : (
              <>
                <strong>0.01%라도 낮은 금리</strong>의
                <br />
                조건을 찾고있습니다..
              </>
            )}
          </h1>
          <div className="loan-loading__art" aria-hidden="true">
            {phase1 ? '💰' : '📉'}
          </div>
          <div className="loan-loading__pct">{progress}%</div>
          <div className="loan-loading__msg">
            {phase1 ? '한도 올리는 중 ···' : '금리 내리는 중 ···'}
          </div>
        </div>
        {leaveModal}
      </div>
    );
  }

  // 심사 거절
  if (screening.result === 'REJECTED') {
    return (
      <div className="app-shell">
        <header className="flow-head flow-head--col">
          <button type="button" className="flow-head__back" onClick={leave}>
            ‹ 뒤로가기
          </button>
        </header>
        <h1 className="page-title">대출한도조회</h1>
        <div className="flow-body" style={{ paddingTop: 40, textAlign: 'center' }}>
          <div style={{ fontSize: 64, marginBottom: 20 }}>😢</div>
          <h1 className="loan-result__lead">
            아쉽지만 이번에는
            <br />
            대출이 어려워요.
          </h1>
          <p style={{ color: 'var(--ink-soft)', marginTop: 16 }}>
            심사 기준에 따라 한도가 산출되지 않았습니다.
          </p>
        </div>
        {leaveModal}
      </div>
    );
  }

  const limitMan = Math.floor(screening.maxLimitAmt / 10000).toLocaleString('ko-KR');
  const rate = tab === '마이너스통장' ? screening.appliedBaseRate + 0.5 : screening.appliedBaseRate;
  const interest = Math.round((screening.maxLimitAmt * rate) / 1200).toLocaleString('ko-KR');

  return (
    <div className="app-shell">
      <header className="flow-head flow-head--col">
        <button type="button" className="flow-head__back" onClick={leave}>
          ‹ 뒤로가기
        </button>
      </header>
      <h1 className="page-title">대출한도조회</h1>

      <div className="flow-body" style={{ paddingBottom: 32 }}>
        <h1 className="loan-result__lead">
          {customer?.name?.trim() || '고객'}님을 위한
          <br />
          대출 조건을 알려드립니다!
        </h1>

        <div className="loan-tabs" role="tablist">
          {(['일반신용', '마이너스통장'] as Tab[]).map((t) => (
            <button
              key={t}
              role="tab"
              aria-selected={tab === t}
              className="loan-tab"
              onClick={() => setTab(t)}
            >
              {t}
            </button>
          ))}
        </div>

        <div className="loan-card">
          <div className="loan-card__row">
            <div className="loan-card__col">
              <div className="loan-card__k">한도</div>
              <div className="loan-card__limit">
                {limitMan}
                <span>만원</span>
              </div>
            </div>
            <div className="loan-card__col">
              <div className="loan-card__k">금리</div>
              <div className="loan-card__rate">
                {rate.toFixed(2)}
                <span>%</span>
              </div>
            </div>
          </div>
          <div className="loan-card__interest">
            {limitMan}만원 한달 평균 대출이자는?
            <br />약 {interest}원
          </div>
          <button
            type="button"
            className="loan-card__apply"
            onClick={() => navigate(`/apply/${encodeURIComponent(productCd)}/notice`)}
          >
            대출 신청하기
          </button>
        </div>

        <div className="flow-info" style={{ marginTop: 16 }}>
          <div className="flow-info__head">유의사항</div>
          <p>해당금리는 예상 금리로 상환방식, 기간 등에 따라 최종 진행화면에서 금리가 재산출됩니다.</p>
          <p>우대금리 적용 전 금리예요. 다음 단계에서 거래 실적(우대조건)을 선택하면 금리가 더 낮아집니다.</p>
        </div>
      </div>
      {leaveModal}
    </div>
  );
}
