import type { Product, ProductIndexItem } from '../types/product';

/**
 * 크롤링 결과 루트. 개발 시 public/data 에 복사돼 있다(`/data`).
 * 다른 위치를 쓰려면 .env 에 VITE_DATA_BASE 지정.
 */
const DATA_BASE = import.meta.env.VITE_DATA_BASE ?? '/data';

/**
 * 폴더명 sanitize — 크롤러 sanitize() 와 동일 규칙.
 * 실제 폴더명은 상품명 그대로지만, 경계 문자가 섞인 경우를 대비해 동일 규칙으로 정규화한다.
 */
export function folderName(name: string): string {
  let n = (name ?? '').trim().replace(/[\\/:*?"<>|]+/g, '_');
  n = n.replace(/\s+/g, ' ').slice(0, 120);
  return n || 'unnamed';
}

/** index 항목 → product.web.json 경로. 카테고리/상품명 2단 구조. */
export function productPath(item: Pick<ProductIndexItem, 'category' | 'name'>): string {
  const cat = encodeURIComponent(item.category);
  const folder = encodeURIComponent(folderName(item.name));
  return `${DATA_BASE}/${cat}/${folder}/product.web.json`;
}

/** 상품 목록(index.json) 로드 — gen-index.mjs 산출물(카테고리 포함). */
export async function loadIndex(): Promise<ProductIndexItem[]> {
  const res = await fetch(`${DATA_BASE}/index.json`);
  if (!res.ok) throw new Error(`index.json 로드 실패: HTTP ${res.status}`);
  const arr = await res.json();
  if (!Array.isArray(arr)) throw new Error('index.json 형식 오류(배열 아님)');
  return arr as ProductIndexItem[];
}

/** 단일 상품(product.web.json) 로드 — index 항목으로 경로를 만든다. */
export async function loadProduct(
  item: Pick<ProductIndexItem, 'category' | 'name'>,
): Promise<Product> {
  const res = await fetch(productPath(item));
  if (!res.ok)
    throw new Error(`product.web.json 로드 실패(${item.name}): HTTP ${res.status}`);
  return res.json();
}

/** mkpd_cd 로 index 항목 찾기(상세 라우팅용). */
export function findByCode(
  index: ProductIndexItem[],
  mkpd_cd: string,
): ProductIndexItem | undefined {
  return index.find((x) => x.mkpd_cd === mkpd_cd);
}
