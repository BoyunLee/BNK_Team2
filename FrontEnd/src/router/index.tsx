import { createBrowserRouter } from 'react-router-dom';
import { RootLayout } from '../layouts/RootLayout';
import { ProductListPage } from '../pages/ProductListPage';
import { ProductDetailRoute } from '../pages/ProductDetailRoute';

/** 라우트: / 목록, /product/:mkpdCd 상세. 공통 레이아웃에 전역 챗봇 포함. */
export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: [
      { path: '/', element: <ProductListPage /> },
      { path: '/product/:mkpdCd', element: <ProductDetailRoute /> },
    ],
  },
]);
