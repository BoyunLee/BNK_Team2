import { Outlet } from 'react-router-dom';
import { AdminAuthProvider } from '../../auth/AdminAuthContext';
import './admin.css';

/**
 * 관리자 영역 최상위. 고객 영역(RootLayout)과 분리 — 챗봇/고객세션 없음.
 * 형상이행은 서버 스케줄러(DeployScheduler)가 수행하므로 클라이언트 틱은 없다.
 */
export function AdminRoot() {
  return (
    <AdminAuthProvider>
      <Outlet />
    </AdminAuthProvider>
  );
}
