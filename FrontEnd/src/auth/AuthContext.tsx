import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import type { Customer, LoginResult } from '../lib/auth';
import { logout as apiLogout } from '../lib/auth';

const STORAGE_KEY = 'bnk.auth';

interface StoredSession {
  customer: Customer;
  accountNo: string;
}

interface AuthContextValue {
  customer: Customer | null;
  accountNo: string | null;
  isLoggedIn: boolean;
  setSession: (result: LoginResult) => void;
  signOut: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

function readStored(): StoredSession | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as StoredSession) : null;
  } catch {
    return null;
  }
}

/** 세션 상태를 앱 전역에 제공. 새로고침 대비 localStorage 에 미러링한다. */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSessionState] = useState<StoredSession | null>(readStored);

  const setSession = useCallback((result: LoginResult) => {
    const next: StoredSession = {
      customer: result.customer,
      accountNo: result.accountNo,
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    setSessionState(next);
  }, []);

  const signOut = useCallback(async () => {
    try {
      await apiLogout();
    } catch {
      // 세션이 이미 만료된 경우도 로컬 상태는 정리한다
    }
    localStorage.removeItem(STORAGE_KEY);
    setSessionState(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      customer: session?.customer ?? null,
      accountNo: session?.accountNo ?? null,
      isLoggedIn: !!session,
      setSession,
      signOut,
    }),
    [session, setSession, signOut],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
