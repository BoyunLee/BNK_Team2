import { Outlet } from 'react-router-dom';
import { ChatWidget } from '../components/ChatWidget';

/** 모든 라우트 공통 레이아웃. 페이지(Outlet) 위에 전역 챗봇을 띄운다. */
export function RootLayout() {
  return (
    <>
      <Outlet />
      <ChatWidget />
    </>
  );
}
