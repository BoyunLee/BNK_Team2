import { useNavigate, useParams } from 'react-router-dom';
import '../../styles/shell.css';
import './apply.css';

/** 본인 인증 완료(한도조회 진행) 바텀시트 오버레이. 배경 위에 떠서 표시된다. */
export function LimitDoneSheet({ onConfirm }: { onConfirm: () => void }) {
  return (
    <div className="result-overlay" role="dialog" aria-label="본인 인증 완료">
      <div className="result-overlay__dim" />
      <div className="result-sheet">
        <div className="result__art" aria-hidden="true">
          ✅
        </div>
        <h1 className="result__title">
          본인 인증이 <em>완료</em>되었습니다
          <br />
          대출한도조회를 진행합니다
        </h1>
        <p className="result__desc">
          제출하신 정보와 동의 내역을 바탕으로
          <br />
          대출 가능 한도를 조회합니다.
          <br />
          실제 대출 승인여부는 다를 수 있어요
        </p>
        <button type="button" className="result__confirm" onClick={onConfirm}>
          확인
        </button>
      </div>
    </div>
  );
}

/**
 * 대출한도조회 완료(본인 인증 후). 바텀시트 형식.
 * (현재 플로우는 LoanLimitConsentPage 위 오버레이로 표시하며, 이 라우트는 직접 진입 대비 유지)
 */
export function LimitDonePage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const productCd = mkpdCd ?? '';
  return (
    <LimitDoneSheet
      onConfirm={() => navigate(`/apply/${encodeURIComponent(productCd)}/loan`)}
    />
  );
}
