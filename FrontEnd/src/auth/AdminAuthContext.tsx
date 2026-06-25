import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import { adminLogout, type AdminUser } from '../lib/admin';

// 관리자 세션 — 고객 세션(bnk.auth)과 완전히 분리한다.
const STORAGE_KEY = 'bnk.admin.session';

interface AdminAuthValue {
  admin: AdminUser | null;
  isLoggedIn: boolean;
  isApprover: boolean;
  signIn: (admin: AdminUser) => void;
  signOut: () => void;
}

const AdminAuthContext = createContext<AdminAuthValue | null>(null);

function readStored(): AdminUser | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as AdminUser) : null;
  } catch {
    return null;
  }
}

export function AdminAuthProvider({ children }: { children: ReactNode }) {
  const [admin, setAdmin] = useState<AdminUser | null>(readStored);

  const signIn = useCallback((next: AdminUser) => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    setAdmin(next);
  }, []);

  const signOut = useCallback(() => {
    // 서버 세션도 정리(실패해도 로컬 상태는 비운다)
    void adminLogout().catch(() => {});
    localStorage.removeItem(STORAGE_KEY);
    setAdmin(null);
  }, []);

  const value = useMemo<AdminAuthValue>(
    () => ({
      admin,
      isLoggedIn: !!admin,
      isApprover: admin?.role === 'APPROVER',
      signIn,
      signOut,
    }),
    [admin, signIn, signOut],
  );

  return <AdminAuthContext.Provider value={value}>{children}</AdminAuthContext.Provider>;
}

export function useAdminAuth(): AdminAuthValue {
  const ctx = useContext(AdminAuthContext);
  if (!ctx) throw new Error('useAdminAuth must be used within AdminAuthProvider');
  return ctx;
}
