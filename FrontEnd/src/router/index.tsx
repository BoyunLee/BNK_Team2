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

/** 라우트: / 목록, /product/:mkpdCd 상세, /apply/:mkpdCd 대출신청 플로우. 공통 레이아웃에 전역 챗봇 포함. */
export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: [
      { path: '/', element: <ProductListPage /> },
      { path: '/product/:mkpdCd', element: <ProductDetailRoute /> },
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
]);
