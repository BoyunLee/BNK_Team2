import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAdminAuth } from '../../auth/AdminAuthContext';
import { adminLogin } from '../../lib/admin';
import './admin.css';

/** 관리자 로그인 — loginId/password 로 백엔드 인증. */
export function AdminLoginPage() {
  const { signIn } = useAdminAuth();
  const navigate = useNavigate();
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [err, setErr] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!loginId.trim() || !password) return;
    setErr('');
    setLoading(true);
    try {
      const admin = await adminLogin(loginId.trim(), password);
      signIn(admin);
      navigate('/admin/requests', { replace: true });
    } catch (e2) {
      setErr(e2 instanceof Error ? e2.message : '로그인에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="adm-login">
      <form className="adm-login__box" onSubmit={submit}>
        <h1>
          BNK <b>여신</b> 관리자
        </h1>
        <p>상품 결재 시스템 · 관리자 계정으로 로그인하세요</p>

        <div className="adm-field">
          <label>아이디</label>
          <input
            value={loginId}
            autoFocus
            placeholder="예) approver1"
            onChange={(e) => setLoginId(e.target.value)}
          />
        </div>
        <div className="adm-field">
          <label>비밀번호</label>
          <input
            type="password"
            value={password}
            placeholder="비밀번호"
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        {err && <div className="adm-err">{err}</div>}

        <button
          type="submit"
          className="adm-btn adm-btn--primary"
          style={{ width: '100%', marginTop: 6 }}
          disabled={loading || !loginId.trim() || !password}
        >
          {loading ? '로그인 중…' : '로그인'}
        </button>

        <p className="adm-hint" style={{ marginTop: 16, marginBottom: 0 }}>
          데모 계정: <b>drafter1</b>(담당자) / <b>approver1</b>(책임자) · 비밀번호 <b>admin1234</b>
        </p>
      </form>
    </div>
  );
}
