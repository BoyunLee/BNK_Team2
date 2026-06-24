import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';

/**
 * 세션이 필요한 라우트 보호. 비로그인 시 로그인으로 보내고,
 * 원래 가려던 경로를 state.from 에 담아 로그인 후 복귀시킨다.
 */
export function RequireAuth() {
  const { isLoggedIn } = useAuth();
  const loc = useLocation();
  if (!isLoggedIn) {
    return (
      <Navigate to="/login" replace state={{ from: loc.pathname + loc.search }} />
    );
  }
  return <Outlet />;
}
