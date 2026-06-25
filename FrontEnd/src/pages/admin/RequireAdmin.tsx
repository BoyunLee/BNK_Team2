import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAdminAuth } from '../../auth/AdminAuthContext';

/** 관리자 미로그인 시 /admin/login 으로. */
export function RequireAdmin() {
  const { isLoggedIn } = useAdminAuth();
  const loc = useLocation();
  if (!isLoggedIn) {
    return <Navigate to="/admin/login" replace state={{ from: loc.pathname }} />;
  }
  return <Outlet />;
}
