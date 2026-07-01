// 내 계좌·대출 조회 API — 백엔드 /api/v1/customers/me/* 매핑 (HomePage 등에서 사용)
import { apiFetch } from './api';

export interface MyAccount {
  accountNo: string; // 마스킹된 계좌번호
  balance: number;
  status: string;
  customerName: string; // 마스킹된 이름
}

export interface LoanSummary {
  loanAccountNo: string; // 대출 신청번호(BNK...)
  productId: number;
  productName: string;
  statusCode: string; // '1'~'9' / 'X' / 'R'
  statusName: string;
  loanAmount: number | null;
  finalRate: number | null;
  maturityDate: string | null; // yyyy-MM-dd
  loanDepositAccountNo: string | null; // 실제 대출계좌번호(마스킹), 미실행 시 null
  appliedAt: string;
}

/** 내 입출금 계좌(대표) */
export const fetchMyAccount = () =>
  apiFetch<MyAccount>('/api/v1/customers/me/account');

/** 내 대출 목록(신청/실행 포함, 최신순) */
export const fetchMyLoans = () =>
  apiFetch<LoanSummary[]>('/api/v1/customers/me/loans');
