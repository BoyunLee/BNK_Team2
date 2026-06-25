import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  register,
  sendEmailCode,
  verifyEmailCode,
  type RegisterRequest,
} from '../../lib/auth';
import { ApiError } from '../../lib/api';
import { PinPad } from '../../components/PinPad';
import { PdfViewer } from '../../components/PdfViewer';
import bnkLogo from '../../assets/bnk-logo.png';
import '../../styles/shell.css';
import './bnkauth.css';

const onlyDigits = (v: string) => v.replace(/[^\d]/g, '');
const CODE_TTL = 180; // 인증코드 유효시간(초)

// 본인확인을 위한 약관(동의서) — public/pdf/signup 에 저장
const CONSENT_DOCS = [
  { id: 'collect', label: '개인정보 수집·이용 동의서', url: '/pdf/signup/consent-collect.pdf' },
  { id: 'thirdparty', label: '개인정보 제3자 제공에 대한 동의서', url: '/pdf/signup/consent-thirdparty.pdf' },
];
const TODAY = new Date().toISOString().slice(0, 10); // 생년월일 상한(오늘)

/** 숫자만 추출해 000-0000-0000 형식으로 포맷 */
function formatPhone(v: string): string {
  const d = onlyDigits(v).slice(0, 11);
  if (d.length < 4) return d;
  if (d.length < 8) return `${d.slice(0, 3)}-${d.slice(3)}`;
  return `${d.slice(0, 3)}-${d.slice(3, 7)}-${d.slice(7)}`;
}

type Step = 'intro' | 'identity' | 'extra';

/**
 * 부산은행 앱 스타일 회원가입.
 * 실제 휴대폰/신분증/통장 인증은 생략하고 이메일 인증으로 본인확인을 대체한다.
 * 입력 필드는 백엔드 register 가 실제로 받는 항목만 구성한다.
 */
export function SignupPage() {
  const navigate = useNavigate();
  const exit = () => navigate('/');

  const [step, setStep] = useState<Step>('intro');

  // 본인확인 약관 — 헤더 선택 시 동의서를 순차로 열람·동의(모두동의)
  const [agreedDocs, setAgreedDocs] = useState<Set<string>>(new Set());
  const [openConsent, setOpenConsent] = useState<(typeof CONSENT_DOCS)[number] | null>(
    null,
  );
  const [seqMode, setSeqMode] = useState(false);
  const [termsOpen, setTermsOpen] = useState(false);
  const termsAgreed = agreedDocs.size === CONSENT_DOCS.length;

  // 헤더(체크+제목) 선택 → 미동의 문서부터 순차로 열람·동의(모두동의)
  function startConsent() {
    const first = CONSENT_DOCS.find((d) => !agreedDocs.has(d.id));
    if (first) {
      setSeqMode(true);
      setOpenConsent(first);
    } else {
      setTermsOpen(true); // 이미 전체동의 → 목록 펼침
    }
  }

  // 개별 동의 — 패널에서 문서 하나만 열람·동의
  function openOne(doc: (typeof CONSENT_DOCS)[number]) {
    setSeqMode(false);
    setOpenConsent(doc);
  }

  function agreeConsent() {
    if (!openConsent) return;
    const next = new Set(agreedDocs).add(openConsent.id);
    setAgreedDocs(next);
    if (seqMode) {
      const remaining = CONSENT_DOCS.find((d) => !next.has(d.id));
      if (remaining) {
        setOpenConsent(remaining); // 다음 문서로 이어서
        return;
      }
      setSeqMode(false);
      setTermsOpen(true); // 모두동의 완료 → 목록 펼침
    }
    setOpenConsent(null);
  }
  const [name, setName] = useState('');
  const [birthDate, setBirthDate] = useState('');
  const [phoneNo, setPhoneNo] = useState('');
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [codeSent, setCodeSent] = useState(false);
  const [verified, setVerified] = useState(false);
  const [remain, setRemain] = useState(0);

  // 추가정보
  const [address, setAddress] = useState('');
  const [simplePassword, setSimplePassword] = useState('');
  const [accountPassword, setAccountPassword] = useState('');
  const [signaturePassword, setSignaturePassword] = useState('');
  const [pad, setPad] = useState<null | 'simple' | 'account' | 'signature'>(null);

  const [error, setError] = useState('');
  const [info, setInfo] = useState('');
  const [busy, setBusy] = useState(false);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  // 인증코드 카운트다운
  useEffect(() => {
    if (!codeSent || verified) return;
    timerRef.current = setInterval(() => {
      setRemain((r) => {
        if (r <= 1) {
          if (timerRef.current) clearInterval(timerRef.current);
          return 0;
        }
        return r - 1;
      });
    }, 1000);
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [codeSent, verified]);

  const mmss = `${String(Math.floor(remain / 60)).padStart(2, '0')}:${String(
    remain % 60,
  ).padStart(2, '0')}`;

  // 인증코드 6자리 입력 시 자동 검증
  useEffect(() => {
    if (codeSent && !verified && code.length === 6 && remain > 0 && !busy) {
      onVerifyCode();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [code]);

  async function onSendCode() {
    if (!email.trim()) return;
    setError('');
    setInfo('');
    setBusy(true);
    try {
      await sendEmailCode(email.trim());
      setCodeSent(true);
      setVerified(false);
      setCode('');
      setRemain(CODE_TTL);
      setInfo('인증 코드를 발송했습니다. 메일(또는 서버 콘솔)을 확인하세요.');
    } catch (err) {
      setError(err instanceof ApiError ? err.message : '코드 발송에 실패했습니다.');
    } finally {
      setBusy(false);
    }
  }

  async function onVerifyCode() {
    if (code.length !== 6) return;
    setError('');
    setInfo('');
    setBusy(true);
    try {
      await verifyEmailCode(email.trim(), code);
      setVerified(true);
      if (timerRef.current) clearInterval(timerRef.current);
      setInfo('이메일 인증이 완료되었습니다.');
    } catch (err) {
      setError(err instanceof ApiError ? err.message : '인증에 실패했습니다.');
    } finally {
      setBusy(false);
    }
  }

  // 인증코드 입력 전: 일반 필드가 모두 채워졌는지
  const fieldsOk =
    termsAgreed &&
    name.trim() !== '' &&
    birthDate !== '' &&
    onlyDigits(phoneNo).length >= 10 &&
    email.trim() !== '';

  // 다음 버튼 동작: 코드 미발송이면 발송(+필드 노출), 인증 완료면 다음 단계로
  function onIdentityNext() {
    if (!codeSent) {
      onSendCode();
      return;
    }
    if (verified) {
      setError('');
      setInfo('');
      setStep('extra');
    }
  }

  const extraOk =
    address.trim() !== '' &&
    simplePassword.length === 6 &&
    accountPassword.length === 4 &&
    signaturePassword.length === 6 &&
    !busy;

  async function onRegister() {
    if (!extraOk) return;
    setError('');
    setBusy(true);
    const req: RegisterRequest = {
      name: name.trim(),
      phoneNo: phoneNo.trim(),
      birthDate,
      address: address.trim(),
      email: email.trim(),
      simplePassword,
      accountPassword,
      signaturePassword,
    };
    try {
      await register(req);
      alert('회원가입이 완료되었습니다. 로그인해주세요.');
      navigate('/login');
    } catch (err) {
      setError(err instanceof ApiError ? err.message : '회원가입에 실패했습니다.');
    } finally {
      setBusy(false);
    }
  }

  /* ===== 헤더 ===== */
  const Header = (
    <header className="ba-head">
      <img className="ba-head__logo" src={bnkLogo} alt="BNK 부산은행" />
      <button type="button" className="ba-head__exit" onClick={exit}>
        나가기
      </button>
    </header>
  );

  /* ===== 인트로 ===== */
  if (step === 'intro') {
    return (
      <div className="app-shell">
        {Header}
        <div className="ba-body">
          <h1 className="ba-title">{'회원가입\n3분이면 충분해요'}</h1>
          <div className="ba-illust" aria-hidden="true">
            👛
          </div>
          <p className="ba-lead">한도제한 계좌로 개설됩니다.</p>
          <p className="ba-desc">
            거래한도는 영업점 창구거래 1일 300만원, 영업점 창구거래 외 모든
            비대면거래는 1일 200만원 <b>(해제 후 상향가능)</b>
          </p>
        </div>
        <div className="ba-bottom">
          <button
            type="button"
            className="ba-btn ba-btn--primary"
            onClick={() => setStep('identity')}
          >
            회원가입 및 계좌개설
          </button>
          <button
            type="button"
            className="ba-btn ba-btn--outline"
            onClick={() => navigate('/login')}
          >
            이미 계정이 있어요 (로그인)
          </button>
        </div>
      </div>
    );
  }

  /* ===== 본인확인 ===== */
  if (step === 'identity') {
    return (
      <div className="app-shell">
        {Header}
        <div className="ba-body">
          <h1 className="ba-title">{'고객님의 정보를\n입력해주세요'}</h1>

          {/* 본인확인을 위한 약관 — 좌측: 모두동의 / 우측 ›: 개별 목록 펼침 */}
          <div className="ba-terms">
            <div className="ba-terms__head">
              <button
                type="button"
                className="ba-terms__agree"
                onClick={startConsent}
              >
                <span
                  className={`ba-terms__check${termsAgreed ? ' ba-terms__check--on' : ''}`}
                >
                  ✓
                </span>
                <span className="ba-terms__title">본인확인을 위한 약관</span>
              </button>
              <button
                type="button"
                className="ba-terms__chevbtn"
                onClick={() => setTermsOpen((o) => !o)}
                aria-label="약관 펼치기"
              >
                <span
                  className={`ba-terms__chev${termsOpen ? ' ba-terms__chev--open' : ''}`}
                >
                  ▾
                </span>
              </button>
            </div>
            {termsOpen && (
              <ul className="ba-terms__panel">
                {CONSENT_DOCS.map((doc) => (
                  <li key={doc.id}>
                    <button
                      type="button"
                      className="ba-consent"
                      onClick={() => openOne(doc)}
                    >
                      <span
                        className={`ba-terms__check ba-terms__check--sm${agreedDocs.has(doc.id) ? ' ba-terms__check--on' : ''}`}
                      >
                        ✓
                      </span>
                      <span className="ba-consent__label">[필수] {doc.label}</span>
                      <span className="ba-consent__chev" aria-hidden="true">
                        ›
                      </span>
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {error && <div className="ba-error">{error}</div>}

          {/* 이름 */}
          <div className="ba-field">
            <span className="ba-label">이름</span>
            <input
              className="ba-input"
              placeholder="홍길동"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>

          {/* 생년월일 */}
          <div className="ba-field">
            <span className="ba-label">생년월일</span>
            <input
              className="ba-input"
              type="date"
              min="1900-01-01"
              max={TODAY}
              value={birthDate}
              onChange={(e) => setBirthDate(e.target.value)}
            />
          </div>

          {/* 휴대폰번호 */}
          <div className="ba-field">
            <span className="ba-label">휴대폰번호</span>
            <input
              className="ba-input"
              inputMode="numeric"
              maxLength={13}
              placeholder="010-0000-0000"
              value={phoneNo}
              onChange={(e) => setPhoneNo(formatPhone(e.target.value))}
            />
          </div>

          {/* 이메일 (휴대폰 인증 대체) */}
          <div className="ba-field">
            <span className="ba-label">이메일 (본인확인)</span>
            <input
              className="ba-input"
              type="email"
              inputMode="email"
              placeholder="example@email.com"
              value={email}
              disabled={codeSent}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          {/* 인증코드 — '다음'을 눌러 코드 발송 후 표시, 6자리 입력 시 자동 검증 */}
          {codeSent && !verified && (
            <div className="ba-field ba-field--tight">
              <span className="ba-label">인증코드</span>
              <input
                className="ba-input"
                inputMode="numeric"
                maxLength={6}
                placeholder="인증번호 6자리"
                value={code}
                autoFocus
                onChange={(e) => setCode(onlyDigits(e.target.value))}
              />
              <div className="ba-codeline">
                <button
                  type="button"
                  className="ba-codeline__resend"
                  onClick={onSendCode}
                  disabled={busy}
                >
                  재발송
                </button>
                <span className="ba-codeline__timer">
                  남은시간 {remain > 0 ? mmss : '만료됨'}
                </span>
              </div>
            </div>
          )}

          {info && (
            <p className={`ba-hint${verified ? ' ba-hint--ok' : ''}`}>{info}</p>
          )}
        </div>

        <div className="ba-bottom">
          <button
            type="button"
            className="ba-btn ba-btn--primary"
            disabled={codeSent ? !verified : !fieldsOk || busy}
            onClick={onIdentityNext}
          >
            다음
          </button>
        </div>

        {openConsent && (
          <PdfViewer
            title={openConsent.label}
            url={openConsent.url}
            onClose={() => {
              setOpenConsent(null);
              setSeqMode(false);
            }}
            onAgree={agreeConsent}
            agreeText={
              seqMode &&
              CONSENT_DOCS.some(
                (d) => d.id !== openConsent.id && !agreedDocs.has(d.id),
              )
                ? '동의하고 계속'
                : '동의'
            }
          />
        )}
      </div>
    );
  }

  /* ===== 추가정보 (주소 + 비밀번호 설정) ===== */
  return (
    <div className="app-shell">
      {Header}
      <div className="ba-body">
        <h1 className="ba-title">{'계좌 개설을 위해\n정보를 설정해주세요'}</h1>

        {error && <div className="ba-error">{error}</div>}

        <div className="ba-field">
          <span className="ba-label">주소</span>
          <input
            className="ba-input"
            placeholder="부산시 중구"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
          />
        </div>

        <div className="ba-field">
          <span className="ba-label">간편비밀번호 (숫자 6자리)</span>
          <PinRow
            length={6}
            value={simplePassword}
            placeholder="로그인·본인인증에 사용"
            onOpen={() => setPad('simple')}
          />
        </div>

        <div className="ba-field">
          <span className="ba-label">계좌비밀번호 (숫자 4자리)</span>
          <PinRow
            length={4}
            value={accountPassword}
            placeholder="계좌 거래에 사용"
            onOpen={() => setPad('account')}
          />
        </div>

        <div className="ba-field">
          <span className="ba-label">전자서명비밀번호 (숫자 6자리)</span>
          <PinRow
            length={6}
            value={signaturePassword}
            placeholder="대출 약정 서명에 사용"
            onOpen={() => setPad('signature')}
          />
        </div>
      </div>

      {pad === 'simple' && (
        <PinPad
          title="간편비밀번호 입력"
          sub="숫자 6자리를 입력해주세요"
          length={6}
          onComplete={setSimplePassword}
          onClose={() => setPad(null)}
        />
      )}
      {pad === 'account' && (
        <PinPad
          title="계좌비밀번호 입력"
          sub="숫자 4자리를 입력해주세요"
          length={4}
          onComplete={setAccountPassword}
          onClose={() => setPad(null)}
        />
      )}
      {pad === 'signature' && (
        <PinPad
          title="전자서명비밀번호 입력"
          sub="숫자 6자리를 입력해주세요"
          length={6}
          onComplete={setSignaturePassword}
          onClose={() => setPad(null)}
        />
      )}

      <div className="ba-bottom">
        <button
          type="button"
          className="ba-btn ba-btn--primary"
          disabled={!extraOk}
          onClick={onRegister}
        >
          {busy ? '처리 중…' : '회원가입 완료'}
        </button>
        <button
          type="button"
          className="ba-btn ba-btn--outline"
          onClick={() => setStep('identity')}
        >
          이전
        </button>
      </div>
    </div>
  );
}

/** 비밀번호 입력 행 — 탭하면 보안 키패드를 연다. 입력값은 ● 로 마스킹 표시. */
function PinRow({
  length,
  value,
  placeholder,
  onOpen,
}: {
  length: number;
  value: string;
  placeholder: string;
  onOpen: () => void;
}) {
  return (
    <button type="button" className="ba-pinrow" onClick={onOpen}>
      {value ? (
        <span className="ba-pinrow__dots">
          {Array.from({ length }).map((_, i) => (
            <span
              key={i}
              className={`ba-pinrow__dot${i < value.length ? ' filled' : ''}`}
            />
          ))}
        </span>
      ) : (
        <span className="ba-pinrow__ph">{placeholder}</span>
      )}
      <span className="ba-select__chev" aria-hidden="true">
        ›
      </span>
    </button>
  );
}
