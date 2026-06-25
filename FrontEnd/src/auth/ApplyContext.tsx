import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import { Outlet } from 'react-router-dom';
import type { ScreeningResult } from '../lib/loan';

// 신청 진행 상태는 메모리에만 보관한다. 플로우(/apply/*) 안에서는 ApplyLayout 이
// 유지돼 공유되며, 이탈 후 재진입 시에는 BE(getCurrentApplication)로 복원한다.

/** 대출 신청 플로우 전반에서 공유하는 상태(점진적으로 필드 추가). */
interface ApplyData {
  loanAccountNo: string | null;
  productId: string | null;
  screening: ScreeningResult | null;
}

interface ApplyContextValue extends ApplyData {
  setApplication: (loanAccountNo: string, productId: string) => void;
  setScreening: (screening: ScreeningResult) => void;
  reset: () => void;
}

const ApplyContext = createContext<ApplyContextValue | null>(null);

const EMPTY: ApplyData = { loanAccountNo: null, productId: null, screening: null };

export function ApplyProvider({ children }: { children: ReactNode }) {
  const [data, setData] = useState<ApplyData>(EMPTY);

  // 새 신청서 시작/복원 — 이전 screening 초기화
  const setApplication = useCallback(
    (loanAccountNo: string, productId: string) =>
      setData({ loanAccountNo, productId, screening: null }),
    [],
  );

  const setScreening = useCallback(
    (screening: ScreeningResult) => setData((d) => ({ ...d, screening })),
    [],
  );

  const reset = useCallback(() => setData(EMPTY), []);

  const value = useMemo<ApplyContextValue>(
    () => ({ ...data, setApplication, setScreening, reset }),
    [data, setApplication, setScreening, reset],
  );

  return <ApplyContext.Provider value={value}>{children}</ApplyContext.Provider>;
}

export function useApply(): ApplyContextValue {
  const ctx = useContext(ApplyContext);
  if (!ctx) throw new Error('useApply must be used within ApplyProvider');
  return ctx;
}

/** /apply/* 라우트를 ApplyProvider 로 감싸는 레이아웃. */
export function ApplyLayout() {
  return (
    <ApplyProvider>
      <Outlet />
    </ApplyProvider>
  );
}
