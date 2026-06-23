// public/data 폴더 구조를 스캔해 화면용 index.json 을 생성한다.
// 카테고리(폴더명) + product.web.json 의 meta/summary 를 합쳐 19개 목록을 만든다.
// 사용: node scripts/gen-index.mjs
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const base = path.resolve(__dirname, '..', 'public', 'data');

const fullIndex = JSON.parse(fs.readFileSync(path.join(base, 'index.json'), 'utf8'));
// 이미 생성된(평탄화된) index 면 name 으로, 원본(74개)이면 그대로 매핑
const byName = new Map(fullIndex.map((x) => [x.name, x]));

const categories = fs
  .readdirSync(base, { withFileTypes: true })
  .filter((d) => d.isDirectory())
  .map((d) => d.name);

const out = [];
const missing = [];
for (const cat of categories) {
  const catDir = path.join(base, cat);
  for (const prod of fs
    .readdirSync(catDir, { withFileTypes: true })
    .filter((d) => d.isDirectory())) {
    const name = prod.name;
    const webPath = path.join(catDir, name, 'product.web.json');
    if (!fs.existsSync(webPath)) {
      missing.push(`${cat}/${name}`);
      continue;
    }
    const web = JSON.parse(fs.readFileSync(webPath, 'utf8'));
    const idx = byName.get(name) ?? {};
    out.push({
      name,
      category: cat,
      mkpd_cd: web.meta?.mkpd_cd ?? idx.mkpd_cd ?? '',
      fpcd: web.meta?.fpcd ?? idx.fpcd ?? '',
      lrg_clacd: idx.lrg_clacd ?? '',
      catchphrase: web.summary?.catchphrase ?? '',
      rateMin: web.summary?.rateMin ?? null,
      rateMax: web.summary?.rateMax ?? null,
    });
  }
}

out.sort(
  (a, b) =>
    a.category.localeCompare(b.category, 'ko') ||
    a.name.localeCompare(b.name, 'ko'),
);

fs.writeFileSync(path.join(base, 'index.json'), JSON.stringify(out, null, 2));
console.log(`항목 수: ${out.length} | 누락: ${missing.length ? missing.join(', ') : '없음'}`);
for (const x of out) {
  console.log(`  [${x.category}] ${x.name} (${x.mkpd_cd}) ${x.rateMin}~${x.rateMax}`);
}
