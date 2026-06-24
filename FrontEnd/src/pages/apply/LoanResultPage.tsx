import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../../styles/shell.css';
import './apply.css';
import './loan.css';

type Tab = '일반신용' | '마이너스통장';

const COND: Record<Tab, { rate: string; interest: string }> = {
  일반신용: { rate: '7.1', interest: '177,000' },
  마이너스통장: { rate: '7.7', interest: '192,000' },
};
const LIMIT = '3,000';

/** 한도/금리 조회 로딩 후 대출 조건 결과(일반신용/마이너스통장). */
export function LoanResultPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const productCd = mkpdCd ?? '';

  const [progress, setProgress] = useState(0);
  const [tab, setTab] = useState<Tab>('일반신용');

  useEffect(() => {
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
  }, []);

  if (progress < 100) {
    const phase1 = progress < 50;
    return (
      <div className="app-shell">
        <header className="flow-head">
          <button type="button" className="flow-head__back" onClick={() => navigate(-1)}>
            ‹ 뒤로가기
          </button>
        </header>
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
      </div>
    );
  }

  const c = COND[tab];
  return (
    <div className="app-shell">
      <header className="flow-head">
        <button type="button" className="flow-head__back" onClick={() => navigate(-1)}>
          ‹ 뒤로가기
        </button>
      </header>

      <div className="flow-body" style={{ paddingBottom: 32 }}>
        <h1 className="loan-result__lead">
          고객님을 위한
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
                {LIMIT}
                <span>만원</span>
              </div>
            </div>
            <div className="loan-card__col">
              <div className="loan-card__k">금리</div>
              <div className="loan-card__rate">
                {c.rate}
                <span>%</span>
              </div>
            </div>
          </div>
          <div className="loan-card__interest">
            {LIMIT}만원 한달 평균 대출이자는?
            <br />약 {c.interest}원
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
          <p>최대 0.8% 우대가 포함된 금리에요. 다음 단계에서 거래 실적 조건을 선택하면 이 금리를 모두 받을 수 있어요.</p>
        </div>
      </div>
    </div>
  );
}
