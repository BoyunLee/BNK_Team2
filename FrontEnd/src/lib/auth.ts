// 인증 API (회원가입/로그인) — 백엔드 /api/v1/auth/* 매핑.
import { apiFetch } from './api';

export interface Customer {
  customerId: number;
  name: string;
  phoneNo: string;
  birthDate: string;
  address: string;
  email: string;
  emailVerifiedYn: string;
  status: string;
}

export interface LoginResult {
  customer: Customer;
  accountNo: string;
}

export interface RegisterRequest {
  name: string;
  phoneNo: string;
  birthDate: string; // yyyy-MM-dd
  address: string;
  email: string;
  simplePassword: string; // 6자리
  accountPassword: string; // 4자리
  signaturePassword: string; // 6자리
}

export interface RegisterResult {
  customerId: number;
  accountNo: string;
}

/** 이메일 인증 코드 발송 */
export const sendEmailCode = (email: string) =>
  apiFetch<void>('/api/v1/auth/email/send', {
    method: 'POST',
    body: JSON.stringify({ email }),
  });

/** 이메일 인증 코드 확인 */
export const verifyEmailCode = (email: string, code: string) =>
  apiFetch<void>('/api/v1/auth/email/verify', {
    method: 'POST',
    body: JSON.stringify({ email, code }),
  });

/** 회원가입 (이메일 인증 완료 후) */
export const register = (req: RegisterRequest) =>
  apiFetch<RegisterResult>('/api/v1/auth/register', {
    method: 'POST',
    body: JSON.stringify(req),
  });

/** 로그인 → 세션 발급 */
export const login = (email: string, simplePassword: string) =>
  apiFetch<LoginResult>('/api/v1/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, simplePassword }),
  });

/** 로그아웃 → 세션 무효화 */
export const logout = () =>
  apiFetch<void>('/api/v1/auth/logout', { method: 'POST' });
