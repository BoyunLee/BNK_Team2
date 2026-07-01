import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { BottomSheet } from '../../components/BottomSheet';
import { useApply } from '../../auth/ApplyContext';
import { useAuth } from '../../auth/AuthContext';
import { useApplyExit } from './useApplyExit';
import {
  agreeTerms,
  saveConditions,
  verifyContractSignature,
  type ConditionsResult,
} from '../../lib/loan';
import {
  fetchProductDetail,
  type BePreferentialRate,
} from '../../lib/products';
import {
  COFIX_VALUES,
  rangeForMethod,
  parseLimitWon,
  type RepaymentOpt,
} from '../../lib/loanCalc';
import { ApiError } from '../../lib/api';
import '../../styles/shell.css';
import './apply.css';
import './loan.css';
import './loanform.css';

/* ===== 한도/금리 미확보 시 폴백 값 ===== */
const MY_LIMIT = 30000000; // 나의 한도(원)
const BASE_RATE = 7.9; // 나의 금리(연 %) — screening 미확보 시 폴백

// 상품 OPT_REPAYMENTS 미로드 시 폴백 상환방법
const REPAY_METHODS = [
  '만기일시상환',
  '원금균등상환',
  '원리금균등상환',
  '종합통장대출(마이너스통장)',
];

const BASE_RATE_OPTIONS = [
  '신규취급액기준 COFIX',
  '신잔액기준 COFIX',
  '금융채 6개월',
  '금융채 12개월',
];
const ACCOUNT_OPTIONS = [
  '112-2314-1478-08 (자유입출금)',
  '302-1102-5547-11 (수시입출금)',
];
const PURPOSE_OPTIONS = [
  '학자금',
  '사업자금',
  '주택구입',
  '전세자금반환용',
  '주택임차(전월세)',
  '주택신축및개량',
  '생계자금',
  '내구소비재구입자금',
  '투자자금',
  '기차입금상환자금',
  '공과금및세금납부',
  '기타',
];

const won = (n: number) => n.toLocaleString('ko-KR');

/** 상환방식별 1회차(최초) 상환금액 */
function computeFirstRepay(
  principal: number,
  annualRate: number,
  method: string,
  months: number,
): number {
  const r = annualRate / 100 / 12; // 월 이자율
  if (principal <= 0 || months <= 0) return 0;
  const interest = principal * r;
  if (method === '원금균등상환') {
    // 매월 원금 균등 + 잔액 이자(1회차는 전액 이자)
    return Math.round(principal / months + interest);
  }
  if (method === '원리금균등상환') {
    if (r === 0) return Math.round(principal / months);
    const annuity = (principal * r) / (1 - Math.pow(1 + r, -months));
    return Math.round(annuity);
  }
  // 만기일시상환 / 종합통장대출(마이너스통장): 이자만 납부
  return Math.round(interest);
}

/** 매월 24일 기준 1회차 납부일(다음달 24일) "YYYY.MM.DD" */
function firstPayDate(): string {
  const d = new Date();
  d.setMonth(d.getMonth() + 1);
  d.setDate(24);
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  return `${d.getFullYear()}.${mm}.24`;
}

type SheetKind = 'baseRate' | 'cycle' | 'account' | 'purpose' | null;

/**
 * 금융상품 중요사항 설명 이후 이어지는 대출신청 페이지.
 * 4단계 아코디언(상환방식 → 우대금리 → 신청정보 → 입금정보) 진행 후
 * 약정 확인 화면으로 전환된다.
 */
export function LoanApplyFormPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const { loanAccountNo, screening } = useApply();
  const { accountNo } = useAuth();
  const productCd = mkpdCd ?? '';
  // 뒤로가기 = 저장하고 나가기(보존), 대출취소 = 중단(취소)
  const { requestExit: requestLeave, exitModal: leaveModal } = useApplyExit(
    productCd,
    { preserve: true },
  );
  const { requestExit: requestCancel, exitModal: cancelModal } =
    useApplyExit(productCd);

  // screening 결과(없으면 폴백). 신청 한도 = min(나의 한도, 상품 한도)
  const [productLimit, setProductLimit] = useState<number | null>(null);
  const effLimit = Math.min(
    screening?.maxLimitAmt ?? MY_LIMIT,
    productLimit ?? Infinity,
  );
  const effBaseRate = screening?.appliedBaseRate ?? BASE_RATE;
  // 입금계좌 = 가입 시 만든 고객 계좌(실행 시 검증됨)
  const depositAccountNo = accountNo ?? '';

  const [phase, setPhase] = useState<'form' | 'agreement'>('form');
  const [step, setStep] = useState(1); // 활성 섹션(1~4)
  const [conditions, setConditions] = useState<ConditionsResult | null>(null);
  const [busy, setBusy] = useState(false);

  // 섹션1
  const [method, setMethod] = useState(REPAY_METHODS[0]);
  // 섹션2 — BE 우대금리
  const [prefRates, setPrefRates] = useState<BePreferentialRate[]>([]);
  const [prefs, setPrefs] = useState<number[]>([]); // 선택한 preferentialId
  // 섹션1 — 상품별 상환방법(+기간범위)
  const [repayments, setRepayments] = useState<RepaymentOpt[]>([]);

  // 상품의 우대금리 + 기준금리/변동주기/대출기간 옵션 로드
  useEffect(() => {
    fetchProductDetail(productCd)
      .then((d) => {
        setPrefRates(d.preferentialRates ?? []);
        const byKey = (k: string) =>
          d.descriptions.find((x) => x.attrKey === k)?.attrValue ?? '';
        const rts = byKey('OPT_RATE_TYPE')
          .split(',')
          .map((x) => x.trim())
          .filter(Boolean);
        const cycles = byKey('OPT_RATE_CYCLES')
          .split(',')
          .filter(Boolean)
          .map((m) => `${m}개월`);
        let reps: RepaymentOpt[] = [];
        try {
          reps = JSON.parse(byKey('OPT_REPAYMENTS') || '[]');
        } catch {
          reps = [];
        }
        setRepayments(reps);
        if (reps.length) setMethod(reps[0].method); // 상품의 첫 상환방법 기본 선택
        setRateTypes(rts);
        setCycleOpts(cycles);
        if (rts.length) setBaseRate(rts[0]); // 첫 기준금리를 기본 선택(복수 시 변경 가능)
        setCycle(cycles[0] ?? '해당없음');
        setProductLimit(parseLimitWon(byKey('LOAN_LIMIT'))); // 상품 한도(신청 상한에 반영)
      })
      .catch(() => setPrefRates([]));
  }, [productCd]);
  // 섹션3 (상품별 옵션은 로드 후 채움)
  const [amount, setAmount] = useState(''); // 숫자 문자열(콤마 제외)
  const [baseRate, setBaseRate] = useState('');
  const [term, setTerm] = useState('');
  const [cycle, setCycle] = useState('');
  const [rateTypes, setRateTypes] = useState<string[]>([]); // 기준금리 종류(상품별 1~다수)
  const [cycleOpts, setCycleOpts] = useState<string[]>([]); // 변동주기 옵션
  // 섹션4 (입금계좌 기본값 = 가입 계좌)
  const [account, setAccount] = useState(depositAccountNo);
  const [purpose, setPurpose] = useState('');
  const [staff, setStaff] = useState('');

  const [sheet, setSheet] = useState<SheetKind>(null);

  const prefSum = useMemo(
    () =>
      prefRates
        .filter((p) => prefs.includes(p.preferentialId))
        .reduce((a, b) => a + b.rateValue, 0),
    [prefs, prefRates],
  );
  const prefMax = useMemo(
    () => prefRates.reduce((a, b) => a + b.rateValue, 0),
    [prefRates],
  );

  // 기준금리 선택(baseRate)에 따른 적용금리 보정: base_rate 는 기본(첫) 기준금리 기준이므로
  // 다른 COFIX 선택 시 그 차이(delta)만큼 가감. 둘 다 COFIX 값이 있을 때만 적용.
  const baseDefault = rateTypes[0] ?? '';
  const cofixDelta =
    COFIX_VALUES[baseRate] != null && COFIX_VALUES[baseDefault] != null
      ? COFIX_VALUES[baseRate] - COFIX_VALUES[baseDefault]
      : 0;
  const finalRate = Math.max(0.1, effBaseRate + cofixDelta - prefSum); // 연 %, 최저 0.1%
  const amountNum = Number(amount) || 0;
  const termMonths = Number(term) || 0;
  const firstRepay = computeFirstRepay(amountNum, finalRate, method, termMonths);

  // 상품별 상환방법 목록(미로드 시 폴백)
  const methodOptions = repayments.length
    ? repayments.map((r) => r.method)
    : REPAY_METHODS;
  // 선택한 상환방법의 가능 대출기간(개월) 범위 — 1개월 단위 입력
  const termRange = useMemo(
    () => rangeForMethod(repayments, method, { min: 6, max: 60 }),
    [repayments, method],
  );
  // 상환방법 변경으로 현재 기간이 범위를 벗어나면 초기화
  useEffect(() => {
    const m = Number(term);
    if (term && (m < termRange.min || m > termRange.max)) setTerm('');
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [termRange.min, termRange.max]);

  const togglePref = (id: number) =>
    setPrefs((p) => (p.includes(id) ? p.filter((x) => x !== id) : [...p, id]));

  // 한도 초과 입력 차단(한도까지만)
  const onAmountChange = (v: string) => {
    const digits = v.replace(/[^\d]/g, '');
    const n = Number(digits) || 0;
    setAmount(n > effLimit ? String(effLimit) : digits);
  };

  const section3Ok = amountNum > 0 && !!baseRate && !!term;
  const section4Ok = !!account && !!purpose;

  // 섹션4 다음 → 약관 동의 + 대출조건 입력(status 6→7) → 약정 확인
  async function submitConditions() {
    if (busy) return;
    if (!loanAccountNo) {
      alert('신청서 정보가 없습니다. 처음부터 다시 진행해주세요.');
      return;
    }
    if (amountNum > effLimit) {
      alert(`신청 금액이 한도(${won(effLimit)}원)를 초과합니다.`);
      return;
    }
    setBusy(true);
    try {
      await agreeTerms(loanAccountNo, [
        'PRODUCT_TERMS',
        'PRODUCT_DESCRIPTION',
        'BOND_CONTRACT',
      ]);
      const res = await saveConditions(loanAccountNo, {
        repaymentType: method,
        rateTypeCode: baseRate === '고정금리' ? 'F' : 'V',
        baseRateType: baseRate, // 선택한 기준금리 종류(COFIX 보정에 사용)
        rateChangeCycle: cycle,
        loanPeriod: `${term}개월`,
        depositAccountNo,
        fundPurpose: purpose,
        loanAmount: amountNum,
        preferentialIds: prefs, // 선택한 우대금리 ID
      });
      setConditions(res);
      setPhase('agreement');
    } catch (e) {
      alert(e instanceof ApiError ? e.message : '대출 조건 등록에 실패했습니다.');
    } finally {
      setBusy(false);
    }
  }

  // 약정 및 실행하기 → 약정 전자서명(7→8) → PIN(대출 실행, 8→9)
  async function submitContractSign() {
    if (busy) return;
    if (!loanAccountNo) {
      alert('신청서 정보가 없습니다. 처음부터 다시 진행해주세요.');
      return;
    }
    setBusy(true);
    try {
      await verifyContractSignature(loanAccountNo);
      const after = `/apply/${encodeURIComponent(productCd)}/complete`;
      navigate(
        `/apply/${encodeURIComponent(productCd)}/auth?action=execute&next=${encodeURIComponent(after)}`,
      );
    } catch (e) {
      alert(e instanceof ApiError ? e.message : '약정 처리에 실패했습니다.');
    } finally {
      setBusy(false);
    }
  }

  /* ===== 나의 한도/금리 요약 카드 ===== */
  const SummaryCard = ({ withMethod }: { withMethod?: boolean }) => (
    <div className="lf-summary">
      <div className="lf-summary__row">
        <span className="lf-summary__k">나의 한도</span>
        <span className="lf-summary__v">
          <b>{won(effLimit)}</b> 원
        </span>
      </div>
      <div className="lf-summary__row">
        <span className="lf-summary__k">나의 금리</span>
        <span className="lf-summary__v">
          연 <b className="lf-rate">{finalRate.toFixed(2)}</b>%
        </span>
      </div>
      <div className="lf-summary__row lf-summary__row--sub">
        <span />
        <span className="lf-summary__sub">({baseRate || '기준금리'} 기준)</span>
      </div>
      {withMethod && (
        <div className="lf-summary__row">
          <span className="lf-summary__k">상환방식</span>
          <span className="lf-summary__v lf-summary__v--accent">{method}</span>
        </div>
      )}
    </div>
  );

  /* ===== 약정 확인 화면 ===== */
  if (phase === 'agreement') {
    // BE 조건 등록 결과 우선(없으면 폼 값)
    const agrAmount = conditions?.loanAmount ?? amountNum;
    const agrRate = conditions?.finalRate ?? finalRate;
    const agrFirstRepay = computeFirstRepay(agrAmount, agrRate, method, termMonths);
    const agrMaturity = conditions?.maturityDate ?? '';
    return (
      <div className="app-shell">
        <header className="flow-head flow-head--col">
          <button
            type="button"
            className="flow-head__back"
            onClick={() => setPhase('form')}
          >
            ‹ 뒤로가기
          </button>
        </header>
        <h1 className="page-title">대출신청</h1>

        <div className="flow-body" style={{ paddingBottom: 24 }}>
          <h2 className="agree__lead">
            <b>약정</b>을 진행하시겠습니까?
          </h2>

          <dl className="agree__list">
            <div className="agree__row">
              <dt>대출금액</dt>
              <dd className="agree__strong">{won(agrAmount)}원</dd>
            </div>
            <div className="agree__row">
              <dt>대출금리</dt>
              <dd className="agree__strong">
                {agrRate.toFixed(2)}%
                <span className="agree__sub">({baseRate || '기준금리'} 기준)</span>
              </dd>
            </div>
            <div className="agree__row">
              <dt>금리변경주기</dt>
              <dd>{cycle}</dd>
            </div>
            <div className="agree__row">
              <dt>상환방식</dt>
              <dd>{method}</dd>
            </div>
            <div className="agree__row">
              <dt>이자납입일</dt>
              <dd>매월 24일</dd>
            </div>
            <div className="agree__row">
              <dt>최초상환금액</dt>
              <dd>{won(agrFirstRepay)}원</dd>
            </div>
            <div className="agree__row">
              <dt>대출만기일</dt>
              <dd>{agrMaturity || `대출 실행일로부터 ${term}개월`}</dd>
            </div>
            <div className="agree__row">
              <dt>대출금입금계좌</dt>
              <dd>{account}</dd>
            </div>
          </dl>

          <p className="agree__note">
            - 본 대출과 관련된 비용 및 수수료를 무통, 무인으로 인출하는데
            동의합니다.
          </p>
        </div>

        <div className="flow-2btn">
          <button type="button" className="flow-2btn__cancel" onClick={requestCancel}>
            대출취소
          </button>
          <button
            type="button"
            className="flow-2btn__ok"
            disabled={busy}
            onClick={submitContractSign}
          >
            {busy ? '처리 중…' : '약정 및 실행하기'}
          </button>
        </div>
        {cancelModal}
      </div>
    );
  }

  /* ===== 신청 폼(아코디언) ===== */
  return (
    <div className="app-shell">
      <header className="flow-head flow-head--col">
        <button type="button" className="flow-head__back" onClick={requestLeave}>
          ‹ 뒤로가기
        </button>
      </header>
      <h1 className="page-title">대출신청</h1>

      <div className="flow-body" style={{ paddingBottom: 40 }}>
        {/* 섹션 1 — 대출상환방식 */}
        <AccSection
          no={1}
          title="대출상환방식"
          active={step === 1}
          done={step > 1}
          summary={method}
          onEdit={() => setStep(1)}
        >
          <div className="lf-subhead">
            <span>상환방식을 선택해주세요.</span>
            <span className="lf-chip">상환방식 ?</span>
          </div>
          <ul className="lf-radio">
            {methodOptions.map((m) => (
              <li key={m}>
                <button
                  type="button"
                  className="lf-radio__btn"
                  aria-pressed={method === m}
                  onClick={() => setMethod(m)}
                >
                  <span className="lf-radio__mark" aria-hidden="true">
                    ✓
                  </span>
                  {m}
                </button>
              </li>
            ))}
          </ul>
          <div className="lf-mini">
            <span>나의한도</span>
            <b>{won(effLimit)}</b> 원
          </div>
          <div className="lf-mini">
            <span>나의금리</span> 연 <b className="lf-rate">{finalRate.toFixed(2)}</b>{' '}
            %
          </div>
          <button
            type="button"
            className="flow-submit lf-next"
            onClick={() => setStep(2)}
          >
            다음
          </button>
        </AccSection>

        {/* 섹션 2 — 우대금리 */}
        <AccSection
          no={2}
          title="우대금리"
          active={step === 2}
          done={step > 2}
          summary={`우대금리 ${prefSum.toFixed(2)}%`}
          onEdit={() => setStep(2)}
        >
          <SummaryCard withMethod />
          <div className="lf-prefsum">
            <span>우대금리 합계</span>
            <b>{prefSum.toFixed(2)}%</b>
          </div>

          <h3 className="lf-h3">우대금리 조건을 선택해주세요</h3>
          <p className="lf-max">
            최대 <b>{prefMax.toFixed(2)}%</b>
          </p>
          <p className="lf-help">
            대출 신청 후 대출&gt;대출관리&gt;우대금리 현황에서 확인할 수 있어요
          </p>

          {prefRates.length === 0 && (
            <p className="lf-help">선택 가능한 우대금리가 없는 상품입니다.</p>
          )}
          <ul className="lf-check">
            {prefRates.map((p) => (
              <li key={p.preferentialId}>
                <button
                  type="button"
                  className="lf-check__btn"
                  aria-pressed={prefs.includes(p.preferentialId)}
                  onClick={() => togglePref(p.preferentialId)}
                >
                  <span className="lf-check__mark" aria-hidden="true">
                    ✓
                  </span>
                  <span className="lf-check__label">
                    {p.conditionName} {p.rateValue.toFixed(2)}% 우대받기
                  </span>
                </button>
              </li>
            ))}
          </ul>

          <div className="lf-redinfo">
            <p className="lf-redinfo__em">- 대출 취급 익월부터 매3개월 기준입니다.</p>
            <p>- 조건을 이행하지 않는 경우 우대받는 금리는 자동으로 인상되오니 유의하시기 바랍니다.</p>
            <p>- 이행조건 등 상세내용은 여신 약정단계의 추가약정서(금리우대거래용)에서 확인가능합니다.</p>
          </div>

          <button
            type="button"
            className="flow-submit lf-next"
            onClick={() => setStep(3)}
          >
            {prefs.length === 0 ? '우대금리 없어도 괜찮아요' : '다음'}
          </button>
        </AccSection>

        {/* 섹션 3 — 대출신청정보 */}
        <AccSection
          no={3}
          title="대출신청정보"
          active={step === 3}
          done={step > 3}
          summary={amountNum > 0 ? `${won(amountNum)}원` : ''}
          onEdit={() => setStep(3)}
        >
          <SummaryCard withMethod />

          <div className="field lf-field">
            <div className="field__label">대출신청금액</div>
            <div className="lf-amount">
              <input
                className="lf-amount__input"
                inputMode="numeric"
                placeholder="대출신청금액을 입력해주세요"
                value={amount ? won(amountNum) : ''}
                onChange={(e) => onAmountChange(e.target.value)}
              />
              <span className="lf-amount__unit">원</span>
            </div>
            <p className="lf-help" style={{ marginTop: 8 }}>
              최대 {won(effLimit)}원까지 신청 가능
            </p>
          </div>

          <SelectField
            label="기준금리"
            help
            value={baseRate}
            placeholder="기준금리를 선택해주세요"
            onOpen={() => setSheet('baseRate')}
          />
          <div className="field lf-field">
            <div className="field__label">대출기간</div>
            <div className="lf-amount">
              <input
                className="lf-amount__input"
                inputMode="numeric"
                placeholder={`${termRange.min} ~ ${termRange.max}`}
                value={term}
                onChange={(e) => setTerm(e.target.value.replace(/[^\d]/g, ''))}
                onBlur={() => {
                  if (!term) return;
                  const m = Math.min(
                    termRange.max,
                    Math.max(termRange.min, Number(term)),
                  );
                  setTerm(String(m));
                }}
              />
              <span className="lf-amount__unit">개월</span>
            </div>
            <p className="lf-help" style={{ marginTop: 8 }}>
              {termRange.min}개월 ~ {termRange.max}개월 (1개월 단위)
            </p>
          </div>
          <SelectField
            label="금리변경주기"
            value={cycle}
            placeholder="금리변경주기를 선택해주세요"
            onOpen={() => setSheet('cycle')}
          />

          <div className="field lf-field">
            <div className="lf-payday">
              <span className="field__label" style={{ margin: 0 }}>
                이자납입일
              </span>
              <span className="lf-payday__chip">매월 24일 📅</span>
            </div>
          </div>

          <button
            type="button"
            className="flow-submit lf-next"
            disabled={!section3Ok}
            onClick={() => setStep(4)}
          >
            다음
          </button>
        </AccSection>

        {/* 섹션 4 — 대출금 입금정보 */}
        <AccSection
          no={4}
          title="대출금 입금정보"
          active={step === 4}
          done={false}
          summary=""
          onEdit={() => setStep(4)}
        >
          <div className="lf-deposit">
            <div className="lf-deposit__top">
              <div className="lf-deposit__k">최초상환금액</div>
              <div className="lf-deposit__amt">
                <b>{won(firstRepay)}</b> 원
              </div>
            </div>
            <dl className="lf-deposit__list">
              <div>
                <dt>대출금액</dt>
                <dd>{won(amountNum)} 원</dd>
              </div>
              <div>
                <dt>대출금리</dt>
                <dd>
                  {finalRate.toFixed(2)}%
                  <span className="lf-deposit__sub">({baseRate || '기준금리'} 기준)</span>
                </dd>
              </div>
              <div>
                <dt>인지세</dt>
                <dd>0 원</dd>
              </div>
              <div>
                <dt>상환방식</dt>
                <dd>{method}</dd>
              </div>
              <div>
                <dt>1회차 납부일</dt>
                <dd>{firstPayDate()}</dd>
              </div>
            </dl>
            <p className="lf-deposit__note">ⓘ 대출금리는 변동될 수도 있습니다.</p>
          </div>

          <SelectField
            label="대출금입금계좌"
            value={account}
            placeholder="대출금입금계좌를 선택해주세요"
            onOpen={() => setSheet('account')}
          />
          <SelectField
            label="자금용도"
            value={purpose}
            placeholder="자금용도를 선택해주세요"
            onOpen={() => setSheet('purpose')}
          />

          <div className="field lf-field">
            <div className="field__label">추천직원(선택)</div>
            <div className="lf-search">
              <input
                className="lf-search__input"
                placeholder="직원명 또는 영업점명을 검색해주세요"
                value={staff}
                onChange={(e) => setStaff(e.target.value)}
              />
              <span className="lf-search__icon" aria-hidden="true">
                🔍
              </span>
            </div>
          </div>

          <button
            type="button"
            className="flow-submit lf-next"
            disabled={!section4Ok || busy}
            onClick={submitConditions}
          >
            {busy ? '처리 중…' : '다음'}
          </button>
        </AccSection>
      </div>

      {/* 선택 시트들 */}
      <BottomSheet
        title="기준금리 선택"
        open={sheet === 'baseRate'}
        options={(rateTypes.length ? rateTypes : BASE_RATE_OPTIONS).map((v) => ({
          value: v,
        }))}
        onSelect={setBaseRate}
        onClose={() => setSheet(null)}
      />
      <BottomSheet
        title="금리변경주기 선택"
        open={sheet === 'cycle'}
        options={(cycleOpts.length ? cycleOpts : ['해당없음']).map((v) => ({
          value: v,
        }))}
        onSelect={setCycle}
        onClose={() => setSheet(null)}
      />
      <BottomSheet
        title="대출금입금계좌 선택"
        open={sheet === 'account'}
        options={(depositAccountNo ? [depositAccountNo] : ACCOUNT_OPTIONS).map(
          (v) => ({ value: v }),
        )}
        onSelect={setAccount}
        onClose={() => setSheet(null)}
      />
      <BottomSheet
        title="자금용도 선택"
        open={sheet === 'purpose'}
        options={PURPOSE_OPTIONS.map((v) => ({ value: v }))}
        onSelect={setPurpose}
        onClose={() => setSheet(null)}
      />
      {leaveModal}
    </div>
  );
}

/* ===== 아코디언 섹션 래퍼 ===== */
function AccSection({
  no,
  title,
  active,
  done,
  summary,
  onEdit,
  children,
}: {
  no: number;
  title: string;
  active: boolean;
  done: boolean;
  summary: string;
  onEdit: () => void;
  children: React.ReactNode;
}) {
  if (active) {
    return (
      <section className="acc acc--active">
        <header className="acc__head">
          <span className="acc__no acc__no--on">{no}</span>
          <span className="acc__title">{title}</span>
        </header>
        <div className="acc__body">{children}</div>
      </section>
    );
  }
  return (
    <button
      type="button"
      className={`acc acc--collapsed${done ? ' acc--done' : ''}`}
      onClick={done ? onEdit : undefined}
      disabled={!done}
    >
      <span className={`acc__no${done ? ' acc__no--done' : ''}`}>{no}</span>
      <span className="acc__title">{title}</span>
      {done && summary && <span className="acc__summary">{summary}</span>}
    </button>
  );
}

/* ===== 드롭다운(시트 열기) 필드 ===== */
function SelectField({
  label,
  value,
  placeholder,
  help,
  onOpen,
}: {
  label: string;
  value: string;
  placeholder: string;
  help?: boolean;
  onOpen: () => void;
}) {
  return (
    <div className="field lf-field">
      <div className="field__label">
        {label}
        {help && <span className="lf-q"> ?</span>}
      </div>
      <button type="button" className="select" onClick={onOpen}>
        <span
          className={`select__value${value ? '' : ' select__value--placeholder'}`}
        >
          {value || placeholder}
        </span>
        <span className="select__chev" aria-hidden="true">
          ⌄
        </span>
      </button>
    </div>
  );
}
