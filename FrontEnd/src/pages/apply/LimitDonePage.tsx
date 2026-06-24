import { useNavigate, useParams } from 'react-router-dom';
import '../../styles/shell.css';
import './apply.css';

/**
 * 대출한도조회 완료(본인 인증 후). 진단결과와 동일한 하단 시트 형식.
 * 확인 시 상품 상세로 복귀. (이후 실제 한도 결과 화면은 추후 연결)
 */
export function LimitDonePage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const productCd = mkpdCd ?? '';

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
        <button
          type="button"
          className="result__confirm"
          onClick={() => navigate(`/apply/${encodeURIComponent(productCd)}/loan`)}
        >
          확인
        </button>
      </div>
    </div>
  );
}
