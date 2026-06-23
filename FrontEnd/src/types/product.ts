// product.web.json 스키마 — 크롤러(build_for_web)가 생성하는 화면용 정제 데이터.
// 이 타입이 프론트엔드와 크롤러 사이의 "계약"이다. 필드명은 크롤러 출력과 1:1.

/** 본문/금리 섹션: 제목 + HTML 조각(표·리스트 등). html 은 렌더 시 DOMPurify 필수. */
export interface Section {
  title: string;
  html: string;
}

/** 약관/설명서 PDF 링크 */
export interface DocumentLink {
  type: 'TERMS' | 'PRODUCT_GUIDE' | 'DISCLOSURE' | 'ETC';
  title: string;
  file: string;
  url: string;
}

export interface ProductMeta {
  name: string;
  baseDate: string;
  mkpd_cd: string;
  fpcd: string;
}

export interface ProductSummary {
  catchphrase: string;
  /** 숫자 또는 수식형 문자열("수신금리+1.3") 또는 null */
  rateMin: string | null;
  rateMax: string | null;
  baseRate: string | null;
  limit: string;
  term: string;
  target: string;
}

/** 한 상품의 전체 화면용 데이터 */
export interface Product {
  meta: ProductMeta;
  summary: ProductSummary;
  infoSections: Section[]; // 상품안내 탭 (HTML 주입)
  rateSections: Section[]; // 금리안내 탭 (HTML 주입)
  documents: DocumentLink[]; // 상품약관 탭
  _note?: string;
}

/** 화면 카테고리 — public/data 의 폴더명(사용자 큐레이션 기준). lrg_clacd 와는 별개. */
export type ProductCategory =
  | '신용대출'
  | '담보대출'
  | '서민금융'
  | '보증서대출';

/**
 * index.json 한 항목 (상품 목록/로딩용).
 * scripts/gen-index.mjs 가 폴더 구조 + product.web.json 을 합쳐 생성한다.
 */
export interface ProductIndexItem {
  name: string;
  /** 폴더 카테고리 = 목록 필터 기준 */
  category: ProductCategory;
  mkpd_cd: string;
  fpcd: string;
  /** 원본 대분류 코드(참고용, 카테고리와 1:1 아님) */
  lrg_clacd?: string;
  catchphrase: string;
  /** 숫자 또는 수식형 문자열 또는 null */
  rateMin: string | null;
  rateMax: string | null;
}
