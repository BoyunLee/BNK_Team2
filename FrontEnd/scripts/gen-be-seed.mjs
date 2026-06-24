// FE 정적 상품 데이터(public/data) 19개를 백엔드 DB 시드(data.sql)로 변환한다.
//  - LOAN_PRODUCT (category, mkpd_cd 포함)
//  - PRODUCT_DESCRIPTION (요약 항목 + infoSections + rateSections + documents)
//  - PRODUCT_TERMS_BASE / HISTORY (대출 플로우 서류용, 상품별 6종)
//  ※ PRODUCT_PREFERENTIAL_RATE 는 FE 데이터가 비구조화(HTML)라 생략 — 대출 플로우 연동 시 별도 처리.
//
// 실행: node scripts/gen-be-seed.mjs  → ../BackEnd/data.sql 생성
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const DATA = path.join(ROOT, 'public', 'data');
const OUT = path.resolve(ROOT, '..', 'BackEnd', 'data.sql');

/** MySQL 문자열 리터럴 이스케이프 */
const esc = (s) => String(s).replace(/\\/g, '\\\\').replace(/'/g, "''");
/** 문자열 → SQL ('...' 또는 NULL) */
const q = (s) => (s == null || s === '' ? 'NULL' : `'${esc(s)}'`);
/** 선두 숫자만 추출(예: "5.5", "수신금리+1.30" → null) */
function numOrNull(v) {
  if (v == null) return null;
  const m = String(v).match(/^\s*(\d+(?:\.\d+)?)/);
  return m ? m[1] : null;
}

const TERMS_TYPES = [
  'ADMIN_INFO_REQUEST',
  'PERSONAL_INFO_CONSENT',
  'MOBILE_AUTH_TERMS',
  'PRODUCT_TERMS',
  'PRODUCT_DESCRIPTION',
  'BOND_CONTRACT',
];

const index = JSON.parse(fs.readFileSync(path.join(DATA, 'index.json'), 'utf8'));

const out = [];
out.push('-- ============================================================');
out.push('-- 부산은행 대출상품 시드 (FE public/data 19종 자동 변환)');
out.push('-- 생성: scripts/gen-be-seed.mjs — 직접 수정하지 말 것');
out.push('-- ============================================================');
out.push('USE bnk3;');
out.push('');
out.push('SET FOREIGN_KEY_CHECKS = 0;');
out.push('TRUNCATE TABLE product_terms_history;');
out.push('TRUNCATE TABLE product_terms_base;');
out.push('TRUNCATE TABLE product_preferential_rate;');
out.push('TRUNCATE TABLE product_description;');
out.push('TRUNCATE TABLE loan_product;');
out.push('SET FOREIGN_KEY_CHECKS = 1;');
out.push('');

let id = 0;
for (const item of index) {
  id += 1;
  const file = path.join(DATA, item.category, item.name, 'product.web.json');
  if (!fs.existsSync(file)) {
    console.warn(`! product.web.json 없음: ${item.category}/${item.name}`);
    continue;
  }
  const p = JSON.parse(fs.readFileSync(file, 'utf8'));
  const meta = p.meta ?? {};
  const sum = p.summary ?? {};

  const baseRate =
    numOrNull(sum.baseRate) ?? numOrNull(sum.rateMin) ?? numOrNull(item.rateMin) ?? '0.00';
  const mkpdCd = meta.mkpd_cd ?? item.mkpd_cd ?? '';
  const catchphrase = sum.catchphrase ?? item.catchphrase ?? '';
  const rateMin = sum.rateMin ?? item.rateMin ?? null;
  const rateMax = sum.rateMax ?? item.rateMax ?? null;

  out.push(`-- [${id}] ${item.name} (${item.category})`);
  out.push(
    'INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES',
  );
  out.push(
    `(${id}, ${q(item.name)}, ${baseRate}, ${q(sum.term)}, 'SALE', ${q(item.category)}, ${q(mkpdCd)}, ${q(catchphrase)}, ${q(rateMin)}, ${q(rateMax)}, NOW(), NOW());`,
  );

  // 설명 행 구성
  const rows = []; // {key, value, sort}
  const add = (key, value, sort) => {
    if (value != null && value !== '') rows.push({ key, value, sort });
  };
  // catchphrase·rateMin·rateMax 는 loan_product 컬럼으로 이동(중복 저장 안 함)
  add('BASE_RATE_RAW', sum.baseRate, 4);
  add('LOAN_LIMIT', sum.limit, 5);
  add('LOAN_TERM', sum.term, 6);
  add('TARGET', sum.target, 7);
  add('BASE_DATE', meta.baseDate, 8);

  (p.infoSections ?? []).forEach((s, i) =>
    add(`INFO:${s.title}`.slice(0, 100), s.html, 100 + i),
  );
  (p.rateSections ?? []).forEach((s, i) =>
    add(`RATE:${s.title}`.slice(0, 100), s.html, 300 + i),
  );
  (p.documents ?? []).forEach((d, i) =>
    add(`DOC:${d.title}`.slice(0, 100), d.url ?? d.file, 500 + i),
  );

  out.push(
    'INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES',
  );
  out.push(
    rows
      .map(
        (r) => `(${id}, ${q(r.key)}, ${q(r.value)}, ${r.sort}, NOW(), NOW())`,
      )
      .join(',\n') + ';',
  );
  out.push('');
}

// 약관 기본(상품별 6종)
out.push('-- ============================================================');
out.push('-- 약관 기본 (PRODUCT_TERMS_BASE) — 상품별 6종');
out.push('-- ============================================================');
for (let pid = 1; pid <= id; pid += 1) {
  out.push(
    'INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES',
  );
  out.push(
    TERMS_TYPES.map(
      (t) =>
        `(${pid}, '${t}', '/terms/p${pid}/${t.toLowerCase()}.pdf', 'Y', NOW(), NOW())`,
    ).join(',\n') + ';',
  );
}
out.push('');
out.push('-- 약관 이력 (각 base 당 seq=1)');
out.push(
  'INSERT INTO product_terms_history (terms_id, terms_seq, terms_path, created_at)',
);
out.push('SELECT terms_id, 1, terms_path, NOW() FROM product_terms_base;');
out.push('');

fs.writeFileSync(OUT, out.join('\n'), 'utf8');
console.log(`생성 완료: ${OUT}  (상품 ${id}종)`);
