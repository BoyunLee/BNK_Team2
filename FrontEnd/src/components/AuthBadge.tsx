import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

/** 우측 상단 인증 영역. 로그인 시 이름+로그아웃, 비로그인 시 로그인 버튼. */
export function AuthBadge() {
  const navigate = useNavigate();
  const { isLoggedIn, customer, signOut } = useAuth();

  if (isLoggedIn) {
    return (
      <div className="authbadge">
        <span className="authbadge__name">{customer?.name}님</span>
        <button
          type="button"
          className="authbadge__btn"
          onClick={() => signOut()}
        >
          로그아웃
        </button>
      </div>
    );
  }

  return (
    <div className="authbadge">
      <button
        type="button"
        className="authbadge__btn authbadge__btn--primary"
        onClick={() => navigate('/login')}
      >
        로그인
      </button>
    </div>
  );
}
