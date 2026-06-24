import { useNavigate, useParams } from 'react-router-dom';
import '../../styles/shell.css';
import './apply.css';

/**
 * 적정성·적합성 진단결과(적합). 대출목적/상환방법 선택과 동일하게
 * 아래에서 올라오는 바텀시트 형식으로 표시. 확인 시 상품 상세로 복귀.
 */
export function DiagnosisResultPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const productCd = mkpdCd ?? '';
  // 적합 확인 → 대출한도조회(동의)로 이어진다
  const proceed = () =>
    navigate(`/apply/${encodeURIComponent(productCd)}/limit`);

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
        <button type="button" className="result__confirm" onClick={proceed}>
          확인
        </button>
      </div>
    </div>
  );
}
