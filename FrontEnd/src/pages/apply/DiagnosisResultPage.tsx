import { useNavigate, useParams } from 'react-router-dom';
import '../../styles/shell.css';
import './apply.css';

/** 진단결과(적합) 바텀시트 오버레이. 배경 위에 떠서 표시된다. */
export function DiagnosisResultSheet({ onConfirm }: { onConfirm: () => void }) {
  return (
    <div className="result-overlay" role="dialog" aria-label="진단결과">
      <div className="result-overlay__dim" />
      <div className="result-sheet">
        <div className="result__art" aria-hidden="true">
          👍
        </div>
        <h1 className="result__title">
          적정성·적합성 진단결과 <em>적합</em>으로
          <br />
          대출을 진행합니다
        </h1>
        <p className="result__desc">
          적정성·적합성 진단은 제출한 정보를 바탕으로
          <br />
          상환능력 등을 고려하여 산출한 결과입니다.
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
 * 적정성·적합성 진단결과(적합). 바텀시트 형식.
 * (현재 플로우는 EligibilityPage 위 오버레이로 표시하며, 이 라우트는 직접 진입 대비 유지)
 */
export function DiagnosisResultPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const productCd = mkpdCd ?? '';
  return (
    <DiagnosisResultSheet
      onConfirm={() => navigate(`/apply/${encodeURIComponent(productCd)}/limit`)}
    />
  );
}
