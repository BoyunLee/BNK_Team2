import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../../styles/shell.css';
import './apply.css';
import './loan.css';

/** 사업소득자용 사업자 정보 입력. 확인 시 한도/금리 조회 결과로 이동. */
export function BusinessInfoPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const productCd = mkpdCd ?? '';

  const [name, setName] = useState('');
  const [bizNo, setBizNo] = useState('');
  const [addr, setAddr] = useState('');

  const complete = name.trim() !== '' && bizNo.trim() !== '';

  return (
    <div className="app-shell">
      <header className="flow-head">
        <button type="button" className="flow-head__back" onClick={() => navigate(-1)}>
          ‹ 뒤로가기
        </button>
      </header>

      <main className="flow-body">
        <h1 className="flow-lead" style={{ marginTop: 24, marginBottom: 40 }}>
          사업자 정보를
          <br />
          입력해주세요.
        </h1>

        <div className="biz-field">
          <input
            className="biz-input"
            placeholder="사업장명을 입력해주세요"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>
        <div className="biz-field">
          <input
            className="biz-input"
            inputMode="numeric"
            placeholder="사업자번호를 입력해주세요"
            value={bizNo}
            onChange={(e) => setBizNo(e.target.value)}
          />
        </div>
        <div className="biz-field biz-field--search">
          <input
            className="biz-input"
            placeholder="사업장주소를 검색해주세요"
            value={addr}
            onChange={(e) => setAddr(e.target.value)}
          />
          <svg className="biz-search" width="20" height="20" viewBox="0 0 24 24" aria-hidden="true">
            <path
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              d="m21 21-4.3-4.3M11 18a7 7 0 1 0 0-14 7 7 0 0 0 0 14Z"
            />
          </svg>
        </div>
      </main>

      <div className="flow-2btn">
        <button type="button" className="flow-2btn__cancel" onClick={() => navigate(-1)}>
          취소
        </button>
        <button
          type="button"
          className="flow-2btn__ok"
          disabled={!complete}
          onClick={() => navigate(`/apply/${encodeURIComponent(productCd)}/loan-result`)}
        >
          확인
        </button>
      </div>
    </div>
  );
}
