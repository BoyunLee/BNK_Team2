import { createBrowserRouter } from 'react-router-dom';
import { ProductListPage } from '../pages/ProductListPage';
import { ProductDetailRoute } from '../pages/ProductDetailRoute';

/** 라우트: / 목록, /product/:mkpdCd 상세 */
export const router = createBrowserRouter([
  { path: '/', element: <ProductListPage /> },
  { path: '/product/:mkpdCd', element: <ProductDetailRoute /> },
]);
