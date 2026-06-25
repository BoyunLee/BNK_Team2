import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { BottomSheet } from '../../components/BottomSheet';
import { INCOME_TREE, nodeAtPath, type IncomeNode } from '../../data/incomeTree';
import { useApply } from '../../auth/ApplyContext';
import { useApplyExit } from './useApplyExit';
import { saveIncome, annualIncomeFor } from '../../lib/loan';
import { ApiError } from '../../lib/api';
import '../../styles/shell.css';
import './apply.css';

/** 레벨별 필드/시트 라벨 */
function levelLabel(depth: number, root: string | undefined): string {
  if (depth === 0) return '소득구분';
  if (root === '급여소득자' && depth === 1) return '직군정보';
  return '상세구분';
}

/**
 * 대출신청 — 직장소득정보(소득구분) 선택.
 * 소득구분.txt 계층을 단계별 드롭다운(바텀시트)으로 캐스케이딩 선택한다.
 * 사업소득자 선택 시 별도 사업자 정보 입력 페이지로 이동.
 */
export function LoanApplyPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const { loanAccountNo } = useApply();
  const { requestExit, exitModal } = useApplyExit(mkpdCd ?? '');
  const productCd = mkpdCd ?? '';
  const back = requestExit;

  const [path, setPath] = useState<string[]>([]);
  const [openLevel, setOpenLevel] = useState<number | null>(null);
  const [busy, setBusy] = useState(false);

  // 현재 경로에 따른 단계 목록 구성
  const levels: {
    depth: number;
    label: string;
    options: IncomeNode[];
    selected: string | null;
  }[] = [];
  {
    let opts: IncomeNode[] | undefined = INCOME_TREE;
    let depth = 0;
    while (opts && opts.length) {
      const selected = path[depth] ?? null;
      levels.push({ depth, label: levelLabel(depth, path[0]), options: opts, selected });
      if (!selected) break;
      opts = opts.find((o) => o.label === selected)?.children;
      depth++;
    }
  }

  const isBusiness = path[0] === '사업소득자';
  const endNode = nodeAtPath(path);
  const complete =
    path.length > 0 &&
    (isBusiness || !!(endNode && !(endNode.children && endNode.children.length)));

  const select = (depth: number, value: string) => {
    setPath((p) => [...p.slice(0, depth), value]); // 더 깊은 선택은 초기화
    setOpenLevel(null);
  };

  const onConfirm = async () => {
    // 사업소득자는 사업자 정보 페이지에서 소득 저장
    if (isBusiness) {
      navigate(`/apply/${encodeURIComponent(productCd)}/business`);
      return;
    }
    if (busy) return;
    if (!loanAccountNo) {
      alert('신청서 정보가 없습니다. 처음부터 다시 진행해주세요.');
      return;
    }
    setBusy(true);
    try {
      await saveIncome(loanAccountNo, {
        companyName: '-',
        jobType: path[1] ?? path[0],
        employmentType: path[path.length - 1] ?? path[0],
        annualIncome: annualIncomeFor(path[0]),
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
        <button type="button" className="flow-head__back" onClick={back}>
          ‹ 뒤로가기
        </button>
      </header>

      <main className="flow-body">
        <h1 className="flow-lead" style={{ marginBottom: 36 }}>
          간편한도 조회를 위해
          <br />
          <strong>직장소득정보</strong>를 선택해주세요.
        </h1>

        {levels.map((lv) => (
          <div className="field" key={lv.depth}>
            {lv.depth > 0 && <div className="field__label">{lv.label}</div>}
            <button
              type="button"
              className="select"
              onClick={() => setOpenLevel(lv.depth)}
            >
              <span
                className={`select__value${lv.selected ? '' : ' select__value--placeholder'}`}
              >
                {lv.selected ?? `${lv.label}을 선택해주세요.`}
              </span>
              <Chevron />
            </button>
          </div>
        ))}

        <div className="flow-info">
          <div className="flow-info__head">
            <InfoIcon />
            이용안내
          </div>
          <p>
            직장 소득정보 확인 후 대출한도 확인이 가능하며, 실제 정보와 다른 경우
            거절될 수 있습니다.
          </p>
        </div>
      </main>

      <div className="flow-2btn">
        <button type="button" className="flow-2btn__cancel" onClick={back}>
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

      {openLevel != null && levels[openLevel] && (
        <BottomSheet
          title={`${levels[openLevel].label}을 선택해주세요.`}
          open
          options={levels[openLevel].options.map((o) => ({ value: o.label }))}
          onSelect={(v) => select(openLevel, v)}
          onClose={() => setOpenLevel(null)}
        />
      )}
      {exitModal}
    </div>
  );
}

function Chevron() {
  return (
    <svg className="select__chev" width="20" height="20" viewBox="0 0 24 24" aria-hidden="true">
      <path d="m6 9 6 6 6-6" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}
function InfoIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" aria-hidden="true">
      <circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" strokeWidth="2" />
      <circle cx="12" cy="8" r="1.2" fill="currentColor" />
      <path d="M12 11v6" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
    </svg>
  );
}
