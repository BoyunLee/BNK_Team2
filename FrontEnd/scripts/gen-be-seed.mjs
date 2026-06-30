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
// 상품 약관 PDF 를 ASCII 경로로 복사할 곳(한글/쉼표 파일명 서빙 문제 회피)
const TERMS_OUT = path.join(ROOT, 'public', 'terms');
fs.rmSync(TERMS_OUT, { recursive: true, force: true });

// 상품별 상환방법(+기간범위)·우대금리 큐레이션 (상품명으로 매칭)
const OVERRIDES = JSON.parse(
  fs.readFileSync(
    path.resolve(ROOT, '..', 'BackEnd', 'scripts', 'product-rate-overrides.json'),
    'utf8',
  ),
);

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

/** HTML → 평문 */
const strip = (h) =>
  String(h ?? '')
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/g, ' ')
    .replace(/&[a-z#0-9]+;/gi, ' ')
    .replace(/\s+/g, ' ')
    .trim();

/** 섹션 제목으로 본문(평문) 찾기 */
function sectionText(p, ...titles) {
  const secs = [...(p.infoSections ?? []), ...(p.rateSections ?? [])];
  for (const t of titles) {
    const s = secs.find((x) => x.title === t);
    if (s) return strip(s.html);
  }
  return '';
}

/** 기준금리 종류 (예: "신잔액기준 COFIX", "금융채", "고정금리") */
function parseRateType(p) {
  const s = sectionText(p, '기준금리');
  if (!s) return '고정금리';
  if (/COFIX/i.test(s)) return s.split(':')[0].trim().slice(0, 40);
  if (/금융채/.test(s)) return '금융채';
  if (/고정/.test(s)) return '고정금리';
  return s.split(':')[0].trim().slice(0, 40);
}

/** 금리변동주기 개월 목록 (예: [3,6,12]) — 고정/해당없음이면 [] */
function parseCycles(p) {
  const s = sectionText(p, '금리변동주기', '금리변동주기 ');
  const nums = (s.match(/\d+/g) ?? [])
    .map(Number)
    .filter((n) => [1, 3, 6, 12].includes(n));
  return [...new Set(nums)].sort((a, b) => a - b);
}

const TERM_BREAKPOINTS = [
  ['6개월', 6], ['1년', 12], ['2년', 24], ['3년', 36], ['5년', 60],
  ['7년', 84], ['10년', 120], ['15년', 180], ['20년', 240], ['30년', 360], ['40년', 480],
];

/** 대출기간 옵션 — 요약(term)의 최소~최대 범위 내 표준 구간 */
function parseTerms(termStr) {
  const s = String(termStr ?? '');
  const months = [];
  for (const m of s.matchAll(/(\d+)\s*년/g)) months.push(Number(m[1]) * 12);
  for (const m of s.matchAll(/(\d+)\s*개월/g)) months.push(Number(m[1]));
  if (months.length === 0) return ['6개월', '1년', '2년', '3년', '5년'];
  const minM = Math.min(...months);
  const maxM = Math.max(...months);
  const opts = TERM_BREAKPOINTS.filter(([, m]) => m >= minM && m <= maxM).map(
    ([l]) => l,
  );
  return opts.length ? opts : ['6개월'];
}

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

  // 대출 기본금리(우대 전) = 최고금리 우선 → 최저 → COFIX 순. COFIX(≈2.5%)만 쓰면 너무 낮음.
  const baseRate =
    numOrNull(sum.rateMax) ??
    numOrNull(item.rateMax) ??
    numOrNull(sum.rateMin) ??
    numOrNull(item.rateMin) ??
    numOrNull(sum.baseRate) ??
    '5.00';
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
  // 상품별 선택지(폼 셀렉트용)
  add('OPT_RATE_TYPE', parseRateType(p), 9);
  add('OPT_RATE_CYCLES', parseCycles(p).join(','), 10);
  add('OPT_TERMS', parseTerms(sum.term).join(','), 11);
  // 상환방법별 가능한 대출기간 범위(상품별 큐레이션) — FE 조건폼이 상환방식 선택에 따라 기간 옵션 산출
  add('OPT_REPAYMENTS', JSON.stringify(OVERRIDES[item.name]?.repayments ?? []), 12);

  (p.infoSections ?? []).forEach((s, i) =>
    add(`INFO:${s.title}`.slice(0, 100), s.html, 100 + i),
  );
  (p.rateSections ?? []).forEach((s, i) =>
    add(`RATE:${s.title}`.slice(0, 100), s.html, 300 + i),
  );
  // 상품약관: 로컬 PDF 를 /terms/p{id}/{i}.pdf 로 복사하고 그 경로를 저장(원본 부산은행 링크 대체)
  (p.documents ?? []).forEach((d, i) => {
    let urlVal = d.url ?? d.file;
    const src = d.file
      ? path.join(DATA, item.category, item.name, '약관', d.file)
      : null;
    if (src && fs.existsSync(src)) {
      const destDir = path.join(TERMS_OUT, `p${id}`);
      fs.mkdirSync(destDir, { recursive: true });
      fs.copyFileSync(src, path.join(destDir, `${i}.pdf`));
      urlVal = `/terms/p${id}/${i}.pdf`;
    }
    add(`DOC:${d.title}`.slice(0, 100), urlVal, 500 + i);
  });

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

  // 우대금리 — 상품별 큐레이션(scripts/product-rate-overrides.json). 고객 직접 충족 항목만(등급성 제외).
  const prefs = OVERRIDES[item.name]?.preferentials;
  if (prefs == null) {
    console.warn(`! override 없음(우대금리 생략): ${item.name}`);
  }
  if (prefs && prefs.length) {
    out.push(
      'INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES',
    );
    out.push(
      prefs
        .map(
          (pr, i) =>
            `(${id}, ${q(`PREF_${id}_${i + 1}`)}, ${q(pr.conditionName)}, ${pr.rateValue}, ${q(pr.description)}, NOW(), NOW())`,
        )
        .join(',\n') + ';',
    );
  }
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
