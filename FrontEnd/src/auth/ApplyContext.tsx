import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import { Outlet } from 'react-router-dom';

const STORAGE_KEY = 'bnk.apply';

/** 대출 신청 플로우 전반에서 공유하는 상태(점진적으로 필드 추가). */
interface ApplyData {
  loanAccountNo: string | null;
  productId: string | null;
}

interface ApplyContextValue extends ApplyData {
  setApplication: (loanAccountNo: string, productId: string) => void;
  reset: () => void;
}

const ApplyContext = createContext<ApplyContextValue | null>(null);

function readStored(): ApplyData {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (raw) return JSON.parse(raw) as ApplyData;
  } catch {
    /* ignore */
  }
  return { loanAccountNo: null, productId: null };
}

export function ApplyProvider({ children }: { children: ReactNode }) {
  const [data, setData] = useState<ApplyData>(readStored);

  const persist = useCallback((next: ApplyData) => {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    setData(next);
  }, []);

  const setApplication = useCallback(
    (loanAccountNo: string, productId: string) =>
      persist({ loanAccountNo, productId }),
    [persist],
  );

  const reset = useCallback(() => {
    sessionStorage.removeItem(STORAGE_KEY);
    setData({ loanAccountNo: null, productId: null });
  }, []);

  const value = useMemo<ApplyContextValue>(
    () => ({ ...data, setApplication, reset }),
    [data, setApplication, reset],
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
