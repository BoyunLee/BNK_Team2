import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useApply } from '../../auth/ApplyContext';
import { useApplyExit } from './useApplyExit';
import { saveIncome, annualIncomeFor } from '../../lib/loan';
import { ApiError } from '../../lib/api';
import '../../styles/shell.css';
import './apply.css';
import './loan.css';

/** 사업소득자용 사업자 정보 입력. 확인 시 소득 저장 후 한도/금리 조회로 이동. */
export function BusinessInfoPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const { loanAccountNo } = useApply();
  const { requestExit, exitModal } = useApplyExit(mkpdCd ?? '');
  const productCd = mkpdCd ?? '';

  const [name, setName] = useState('');
  const [bizNo, setBizNo] = useState('');
  const [addr, setAddr] = useState('');
  const [busy, setBusy] = useState(false);

  const complete = name.trim() !== '' && bizNo.trim() !== '';

  const onConfirm = async () => {
    if (busy) return;
    if (!loanAccountNo) {
      alert('신청서 정보가 없습니다. 처음부터 다시 진행해주세요.');
      return;
    }
    setBusy(true);
    try {
      await saveIncome(loanAccountNo, {
        companyName: name.trim(),
        jobType: '사업소득자',
        employmentType: '개인사업자',
        annualIncome: annualIncomeFor('사업소득자'),
      });
      navigate(`/apply/${encodeURIComponent(productCd)}/loan-result`);
    } catch (e) {
      alert(e instanceof ApiError ? e.message : '소득정보 등록에 실패했습니다.');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="app-shell">
      <header className="flow-head">
        <button type="button" className="flow-head__back" onClick={requestExit}>
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
        <button type="button" className="flow-2btn__cancel" onClick={requestExit}>
          취소
        </button>
        <button
          type="button"
          className="flow-2btn__ok"
          disabled={!complete || busy}
          onClick={onConfirm}
        >
          {busy ? '처리 중…' : '확인'}
        </button>
      </div>
      {exitModal}
    </div>
  );
}
