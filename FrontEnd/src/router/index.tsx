import { createBrowserRouter } from 'react-router-dom';
import { RootLayout } from '../layouts/RootLayout';
import { ProductListPage } from '../pages/ProductListPage';
import { ProductDetailRoute } from '../pages/ProductDetailRoute';
import { EligibilityPage } from '../pages/apply/EligibilityPage';
import { PinAuthPage } from '../pages/apply/PinAuthPage';
import { DiagnosisResultPage } from '../pages/apply/DiagnosisResultPage';
import { LoanLimitConsentPage } from '../pages/apply/LoanLimitConsentPage';
import { LimitDonePage } from '../pages/apply/LimitDonePage';
import { LoanApplyPage } from '../pages/apply/LoanApplyPage';
import { BusinessInfoPage } from '../pages/apply/BusinessInfoPage';
import { LoanResultPage } from '../pages/apply/LoanResultPage';
import { ImportantNoticePage } from '../pages/apply/ImportantNoticePage';
import { LoanApplyFormPage } from '../pages/apply/LoanApplyFormPage';
import { LoanCompletePage } from '../pages/apply/LoanCompletePage';
import { LoginPage } from '../pages/auth/LoginPage';
import { SignupPage } from '../pages/auth/SignupPage';
import { RequireAuth } from '../auth/RequireAuth';
import { ApplyLayout } from '../auth/ApplyContext';
import { AdminRoot } from '../pages/admin/AdminRoot';
import { AdminLoginPage } from '../pages/admin/AdminLoginPage';
import { RequireAdmin } from '../pages/admin/RequireAdmin';
import { AdminLayout } from '../pages/admin/AdminLayout';
import { DashboardPage } from '../pages/admin/DashboardPage';
import { ChangeRequestListPage } from '../pages/admin/ChangeRequestListPage';
import { ProductFormPage } from '../pages/admin/ProductFormPage';
import { ChangeRequestDetailPage } from '../pages/admin/ChangeRequestDetailPage';
import { ApprovalInboxPage } from '../pages/admin/ApprovalInboxPage';
import { DeployStatusPage } from '../pages/admin/DeployStatusPage';
import { LiveProductsPage } from '../pages/admin/LiveProductsPage';
import { Navigate } from 'react-router-dom';

/** 라우트: / 목록, /product/:mkpdCd 상세, /apply/:mkpdCd 대출신청 플로우. 공통 레이아웃에 전역 챗봇 포함. */
export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: [
      { path: '/', element: <ProductListPage /> },
      { path: '/login', element: <LoginPage /> },
      { path: '/signup', element: <SignupPage /> },
      { path: '/product/:productId', element: <ProductDetailRoute /> },
      {
        element: <RequireAuth />,
        children: [
          {
            element: <ApplyLayout />,
            children: [
          { path: '/apply/:mkpdCd', element: <EligibilityPage /> },
          { path: '/apply/:mkpdCd/auth', element: <PinAuthPage /> },
          { path: '/apply/:mkpdCd/result', element: <DiagnosisResultPage /> },
          { path: '/apply/:mkpdCd/limit', element: <LoanLimitConsentPage /> },
          { path: '/apply/:mkpdCd/done', element: <LimitDonePage /> },
          { path: '/apply/:mkpdCd/loan', element: <LoanApplyPage /> },
          { path: '/apply/:mkpdCd/business', element: <BusinessInfoPage /> },
          { path: '/apply/:mkpdCd/loan-result', element: <LoanResultPage /> },
          { path: '/apply/:mkpdCd/notice', element: <ImportantNoticePage /> },
          { path: '/apply/:mkpdCd/form', element: <LoanApplyFormPage /> },
          { path: '/apply/:mkpdCd/complete', element: <LoanCompletePage /> },
            ],
          },
        ],
      },
    ],
  },
  // ===== 관리자(상품 결재) 영역 — 고객 레이아웃과 분리(챗봇/고객세션 없음) =====
  {
    path: '/admin',
    element: <AdminRoot />,
    children: [
      { path: 'login', element: <AdminLoginPage /> },
      {
        element: <RequireAdmin />,
        children: [
          {
            element: <AdminLayout />,
            children: [
              { index: true, element: <Navigate to="/admin/dashboard" replace /> },
              { path: 'dashboard', element: <DashboardPage /> },
              { path: 'requests', element: <ChangeRequestListPage /> },
              { path: 'requests/new', element: <ProductFormPage /> },
              { path: 'requests/:id', element: <ChangeRequestDetailPage /> },
              { path: 'requests/:id/edit', element: <ProductFormPage /> },
              { path: 'inbox', element: <ApprovalInboxPage /> },
              { path: 'deploy', element: <DeployStatusPage /> },
              { path: 'products', element: <LiveProductsPage /> },
            ],
          },
        ],
      },
    ],
  },
]);
