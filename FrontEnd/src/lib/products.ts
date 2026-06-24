// 상품 목록/상세 — 백엔드 /api/v1/products 연동 + 기존 Product 형태로 매핑.
import { apiFetch } from './api';
import type {
  Product,
  ProductCategory,
  Section,
  DocumentLink,
} from '../types/product';

export interface BeDescription {
  attrKey: string;
  attrValue: string;
  sortOrder: number;
}

export interface BePreferentialRate {
  preferentialId: number;
  conditionCode: string;
  conditionName: string;
  rateValue: number;
  description: string;
}

export interface BeProductListItem {
  productId: number;
  productName: string;
  baseRate: number;
  loanPeriod: string;
  status: string;
  category: ProductCategory;
  mkpdCd: string;
  catchphrase: string | null;
  rateMin: string | null;
  rateMax: string | null;
}

export interface BeProductDetail extends BeProductListItem {
  descriptions: BeDescription[];
  preferentialRates: BePreferentialRate[];
}

/** 판매 중 상품 목록 */
export const fetchProducts = () =>
  apiFetch<BeProductListItem[]>('/api/v1/products');

/** 상품 상세 (productId) */
export const fetchProductDetail = (productId: string | number) =>
  apiFetch<BeProductDetail>(`/api/v1/products/${productId}`);

/**
 * BE 상세 응답 → 기존 Product 형태로 매핑(상세 컴포넌트 재사용).
 * 설명은 시드 변환 시 부여한 접두사(INFO:/RATE:/DOC:)와 메타 키로 복원한다.
 */
export function toProduct(d: BeProductDetail): Product {
  const byKey = (k: string) =>
    d.descriptions.find((x) => x.attrKey === k)?.attrValue ?? '';

  const sections = (prefix: string): Section[] =>
    d.descriptions
      .filter((x) => x.attrKey.startsWith(prefix))
      .sort((a, b) => a.sortOrder - b.sortOrder)
      .map((x) => ({ title: x.attrKey.slice(prefix.length), html: x.attrValue }));

  const documents: DocumentLink[] = d.descriptions
    .filter((x) => x.attrKey.startsWith('DOC:'))
    .sort((a, b) => a.sortOrder - b.sortOrder)
    .map((x) => ({
      type: 'TERMS',
      title: x.attrKey.slice('DOC:'.length),
      file: '',
      url: x.attrValue,
    }));

  return {
    meta: {
      name: d.productName,
      baseDate: byKey('BASE_DATE'),
      mkpd_cd: d.mkpdCd,
      fpcd: '',
    },
    summary: {
      catchphrase: d.catchphrase ?? '',
      rateMin: d.rateMin,
      rateMax: d.rateMax,
      baseRate: byKey('BASE_RATE_RAW') || (d.baseRate != null ? String(d.baseRate) : null),
      limit: byKey('LOAN_LIMIT'),
      term: d.loanPeriod || byKey('LOAN_TERM'),
      target: byKey('TARGET'),
    },
    infoSections: sections('INFO:'),
    rateSections: sections('RATE:'),
    documents,
  };
}
