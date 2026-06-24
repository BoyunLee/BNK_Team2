import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import { login } from '../../lib/auth';
import { ApiError } from '../../lib/api';
import { PinPad } from '../../components/PinPad';
import bnkLogo from '../../assets/bnk-logo.png';
import '../../styles/shell.css';
import './bnkauth.css';

/** 부산은행 앱 스타일 로그인. 이메일 + 간편비밀번호 6자리. */
export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { setSession } = useAuth();
  const from = (location.state as { from?: string } | null)?.from ?? '/';

  const [email, setEmail] = useState('');
  const [pw, setPw] = useState('');
  const [padOpen, setPadOpen] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const canSubmit = email.trim() !== '' && pw.length === 6 && !loading;

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!canSubmit) return;
    setError('');
    setLoading(true);
    try {
      const result = await login(email.trim(), pw);
      setSession(result);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err instanceof ApiError ? err.message : '로그인에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="app-shell">
      <header className="ba-head">
        <img className="ba-head__logo" src={bnkLogo} alt="BNK 부산은행" />
        <button type="button" className="ba-head__exit" onClick={() => navigate('/')}>
          나가기
        </button>
      </header>

      <form className="ba-body" onSubmit={onSubmit}>
        <h1 className="ba-title">{'반갑습니다\n로그인해주세요'}</h1>

        {error && <div className="ba-error">{error}</div>}

        <div className="ba-field">
          <span className="ba-label">이메일</span>
          <input
            className="ba-input"
            type="email"
            inputMode="email"
            autoComplete="username"
            placeholder="example@email.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>

        <div className="ba-field">
          <span className="ba-label">간편비밀번호</span>
          <button
            type="button"
            className="ba-pinrow"
            onClick={() => setPadOpen(true)}
          >
            {pw ? (
              <span className="ba-pinrow__dots">
                {Array.from({ length: 6 }).map((_, i) => (
                  <span
                    key={i}
                    className={`ba-pinrow__dot${i < pw.length ? ' filled' : ''}`}
                  />
                ))}
              </span>
            ) : (
              <span className="ba-pinrow__ph">숫자 6자리</span>
            )}
            <span className="ba-select__chev" aria-hidden="true">
              ›
            </span>
          </button>
        </div>

        <p className="ba-foot">
          아직 회원이 아니신가요?{' '}
          <button type="button" onClick={() => navigate('/signup')}>
            회원가입
          </button>
        </p>

        <div className="ba-bottom">
          <button type="submit" className="ba-btn ba-btn--primary" disabled={!canSubmit}>
            {loading ? '로그인 중…' : '로그인'}
          </button>
        </div>
      </form>

      {padOpen && (
        <PinPad
          title="간편비밀번호 입력"
          sub="숫자 6자리를 입력해주세요"
          length={6}
          onComplete={setPw}
          onClose={() => setPadOpen(false)}
        />
      )}
    </div>
  );
}
