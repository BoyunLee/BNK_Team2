import { Outlet } from 'react-router-dom';
import { ChatWidget } from '../components/ChatWidget';
import { AuthProvider } from '../auth/AuthContext';

/** 모든 라우트 공통 레이아웃. 세션 컨텍스트 + 페이지(Outlet) + 전역 챗봇. */
export function RootLayout() {
  return (
    <AuthProvider>
      <Outlet />
      <ChatWidget />
    </AuthProvider>
  );
}
