import { useNavigate, useParams } from 'react-router-dom';
import '../../styles/shell.css';
import './apply.css';
import './loanform.css';

/** 대출실행 완료 화면. 체크 아이콘 + 완료 문구 + 이용안내 + 확인 버튼. */
export function LoanCompletePage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const productCd = mkpdCd ?? '';

  return (
    <div className="app-shell">
      <div className="flow-body lc">
        <div className="lc__check" aria-hidden="true">
          <svg viewBox="0 0 80 80" width="80" height="80">
            <circle cx="40" cy="40" r="38" fill="none" stroke="currentColor" strokeWidth="3" />
            <path
              d="M24 41.5 L35 52 L57 29"
              fill="none"
              stroke="currentColor"
              strokeWidth="5"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </div>

        <h1 className="lc__title">
          대출실행이
          <br />
          완료되었습니다.
        </h1>

        <section className="lc-info">
          <div className="lc-info__head">
            <span className="lc-info__i" aria-hidden="true">
              ⓘ
            </span>
            이용안내
          </div>
          <ul className="lc-info__list">
            <li>금리우대 옵션 이행여부는 매3개월마다 확인 합니다.</li>
            <li>
              조건을 이행하지 않는 경우 우대받는 금리는 자동으로 인상되오니
              유의하시기 바랍니다.
            </li>
          </ul>

          <div className="lc-info__sub">대출이자 납입안내</div>
          <ul className="lc-info__list">
            <li>고객님 휴대폰으로 다음달부터 자동으로 알려드립니다.</li>
            <li>
              연락처 변경 또는 대출정보 통지서비스 해지를 원하시면
              [조회/관리&gt;대출&gt;대출정보통지서비스]로 신청해주세요.
            </li>
          </ul>
        </section>
      </div>

      <div className="flow-submit-bar">
        <button
          type="button"
          className="flow-submit"
          onClick={() => navigate(`/product/${encodeURIComponent(productCd)}`)}
        >
          확인
        </button>
      </div>
    </div>
  );
}
