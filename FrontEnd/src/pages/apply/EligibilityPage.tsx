import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { BottomSheet, type SheetOption } from '../../components/BottomSheet';
import { AlertModal } from '../../components/AlertModal';
import { useApply } from '../../auth/ApplyContext';
import { createApplication, cancelApplication } from '../../lib/loan';
import { ApiError } from '../../lib/api';
import '../../styles/shell.css';
import './apply.css';

// 대출용도 / 상환방법 시트 옵션 (캡쳐 기준, 아이콘은 이모지로 근사)
const PURPOSE_OPTIONS: SheetOption[] = [
  { value: '가계자금', icon: '💰' },
  { value: '사업자금', icon: '💼' },
  { value: '주택자금', icon: '🏠' },
  { value: '경조자금', icon: '✉️' },
  { value: '교육비', icon: '🏫' },
  { value: '의료비', icon: '🏥' },
  { value: '대출금 상환', icon: '📗' },
  { value: '기타', icon: '🧩' },
];
const REPAY_OPTIONS: SheetOption[] = [
  { value: '근로소득', icon: '💵' },
  { value: '사업소득', icon: '🧰' },
  { value: '임대소득', icon: '🏠' },
  { value: '연금소득', icon: '🏦' },
  { value: '기타', icon: '🧩' },
];

const ASSET = ['1억 미만', '5억 미만', '5억 이상'];
const INCOME = ['5천만 미만', '1억 미만', '1억 이상'];
const DEBT = ['5천만 미만', '1억 미만', '1억 이상'];
const FIXED = ['연간소득의 20% 미만', '연간소득의 50% 미만', '연간소득의 50% 이상'];
const CREDIT = ['동의안함', '동의함'];
const COLLATERAL = ['있음', '없음'];
const OVERDUE = ['있음', '없음'];

interface Form {
  purpose: string | null;
  repay: string | null;
  asset: string | null;
  income: string | null;
  debt: string | null;
  fixed: string | null;
  credit: string | null;
  collateral: string | null;
  overdue: string | null;
}
const EMPTY: Form = {
  purpose: null,
  repay: null,
  asset: null,
  income: null,
  debt: null,
  fixed: null,
  credit: null,
  collateral: null,
  overdue: null,
};

/** 버튼 옵션 그룹 */
function Group({
  label,
  options,
  value,
  onChange,
  cols,
}: {
  label: string;
  options: string[];
  value: string | null;
  onChange: (v: string) => void;
  cols: number;
}) {
  return (
    <div className="group">
      <div className="group__label">{label}</div>
      <div
        className="opt-grid"
        style={{ gridTemplateColumns: `repeat(${cols}, 1fr)` }}
      >
        {options.map((o) => (
          <button
            key={o}
            type="button"
            className="opt"
            aria-pressed={value === o}
            onClick={() => onChange(o)}
          >
            {o}
          </button>
        ))}
      </div>
    </div>
  );
}

export function EligibilityPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const { loanAccountNo, setApplication } = useApply();
  const [form, setForm] = useState<Form>(EMPTY);
  const [sheet, setSheet] = useState<null | 'purpose' | 'repay'>(null);
  const [showCreditWarn, setShowCreditWarn] = useState(false);
  const [busy, setBusy] = useState(false);

  const set =
    <K extends keyof Form>(key: K) =>
    (v: Form[K]) =>
      setForm((f) => ({ ...f, [key]: v }));

  // 신용점수 조회: 동의안함 선택 시 경고 팝업
  function onChangeCredit(v: string) {
    set('credit')(v);
    if (v === '동의안함') setShowCreditWarn(true);
  }

  const complete = Object.values(form).every((v) => v != null);
  // 신용점수 조회 미동의 시 대출 심사 불가 → 제출 차단
  const creditDeclined = form.credit === '동의안함';
  const canSubmit = complete && !creditDeclined && !busy;
  const productCd = mkpdCd ?? '';

  // 적합성 진단 제출 → 대출 신청서 생성(status 1) → 본인인증(PIN)
  async function onSubmit() {
    if (!canSubmit) return;
    setBusy(true);
    try {
      // 이전에 만들다 만 신청서가 있으면 취소 후 새로 생성(재진입 대비)
      if (loanAccountNo) {
        try {
          await cancelApplication(loanAccountNo);
        } catch {
          /* 이미 만료/취소된 경우 무시 */
        }
      }
      const res = await createApplication(Number(productCd));
      setApplication(res.loanAccountNo, productCd);
      navigate(`/apply/${productCd}/auth`);
    } catch (e) {
      alert(
        e instanceof ApiError ? e.message : '신청서 생성에 실패했습니다. 다시 시도해주세요.',
      );
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="app-shell">
      <header className="flow-head">
        <span className="flow-head__title">적정성적합성확인</span>
        <button
          type="button"
          className="flow-head__close"
          onClick={() => navigate(`/product/${encodeURIComponent(productCd)}`)}
        >
          닫기
        </button>
      </header>

      <main className="flow-body">
        <svg
          className="flow-check"
          viewBox="0 0 24 24"
          width="54"
          height="54"
          aria-hidden="true"
        >
          <circle cx="12" cy="12" r="12" fill="#22b573" />
          <path
            d="m7 12.3 3.3 3.3L17 9"
            fill="none"
            stroke="#fff"
            strokeWidth="2.2"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
        <p className="flow-law">금융소비자보호법 제17조 제2항에 의해</p>
        <h1 className="flow-lead">
          해당 상품이 고객님에게
          <br />
          적합한지 확인합니다.
        </h1>

        <div className="field">
          <div className="field__label">대출목적</div>
          <button
            type="button"
            className="select"
            onClick={() => setSheet('purpose')}
          >
            <span
              className={`select__value${form.purpose ? '' : ' select__value--placeholder'}`}
            >
              {form.purpose ?? '대출목적을 선택해주세요'}
            </span>
            <Chevron />
          </button>
        </div>

        <div className="field">
          <div className="field__label">상환방법</div>
          <button
            type="button"
            className="select"
            onClick={() => setSheet('repay')}
          >
            <span
              className={`select__value${form.repay ? '' : ' select__value--placeholder'}`}
            >
              {form.repay ?? '상환방법을 선택해주세요'}
            </span>
            <Chevron />
          </button>
        </div>

        <Group
          label="자산규모"
          options={ASSET}
          value={form.asset}
          onChange={set('asset')}
          cols={3}
        />
        <Group
          label="연간소득"
          options={INCOME}
          value={form.income}
          onChange={set('income')}
          cols={3}
        />
        <Group
          label="부채"
          options={DEBT}
          value={form.debt}
          onChange={set('debt')}
          cols={3}
        />
        <Group
          label="고정지출"
          options={FIXED}
          value={form.fixed}
          onChange={set('fixed')}
          cols={1}
        />
        <Group
          label="신용점수 조회"
          options={CREDIT}
          value={form.credit}
          onChange={onChangeCredit}
          cols={2}
        />
        <Group
          label="담보대출"
          options={COLLATERAL}
          value={form.collateral}
          onChange={set('collateral')}
          cols={2}
        />
        <Group
          label="연체"
          options={OVERDUE}
          value={form.overdue}
          onChange={set('overdue')}
          cols={2}
        />
      </main>

      <div className="flow-submit-bar">
        <button
          type="button"
          className="flow-submit"
          disabled={!canSubmit}
          onClick={onSubmit}
        >
          {busy ? '신청서 생성 중…' : '적합성 진단 제출'}
        </button>
      </div>

      <BottomSheet
        title="대출용도"
        open={sheet === 'purpose'}
        options={PURPOSE_OPTIONS}
        onSelect={set('purpose')}
        onClose={() => setSheet(null)}
      />
      <BottomSheet
        title="상환방법"
        open={sheet === 'repay'}
        options={REPAY_OPTIONS}
        onSelect={set('repay')}
        onClose={() => setSheet(null)}
      />

      <AlertModal
        open={showCreditWarn}
        message="신용점수 조회 미동의 시 대출 심사가 불가합니다."
        onConfirm={() => setShowCreditWarn(false)}
      />
    </div>
  );
}

function Chevron() {
  return (
    <svg
      className="select__chev"
      width="20"
      height="20"
      viewBox="0 0 24 24"
      aria-hidden="true"
    >
      <path
        d="m6 9 6 6 6-6"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}
