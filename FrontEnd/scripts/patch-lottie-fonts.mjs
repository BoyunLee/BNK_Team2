// Lottie 애니메이션의 한글 텍스트 렌더링 문제를 일괄 수정한다.
//  1) 모지박케(UTF-8 바이트를 CP1252 로 오독한 깨진 텍스트)를 정상 한글로 복원
//  2) chars(임베드 글리프) 키 제거 → lottie 가 네이티브 <text> 로 렌더(usesGlyphs=false)
//  3) fonts.list 의 fFamily 를 브라우저에 로드된 'Noto Sans KR' 로 통일
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const dir = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  '..',
  'src',
  'assets',
  'notice',
);

// CP1252 0x80~0x9F 특수문자 → 바이트 역매핑
const CP1252_REV = {
  0x20ac: 0x80, 0x201a: 0x82, 0x0192: 0x83, 0x201e: 0x84, 0x2026: 0x85,
  0x2020: 0x86, 0x2021: 0x87, 0x02c6: 0x88, 0x2030: 0x89, 0x0160: 0x8a,
  0x2039: 0x8b, 0x0152: 0x8c, 0x017d: 0x8e, 0x2018: 0x91, 0x2019: 0x92,
  0x201c: 0x93, 0x201d: 0x94, 0x2022: 0x95, 0x2013: 0x96, 0x2014: 0x97,
  0x02dc: 0x98, 0x2122: 0x99, 0x0161: 0x9a, 0x203a: 0x9b, 0x0153: 0x9c,
  0x017e: 0x9e, 0x0178: 0x9f,
};

function fixText(str) {
  const bytes = [];
  for (const ch of str) {
    const cp = ch.codePointAt(0);
    if (CP1252_REV[cp] !== undefined) bytes.push(CP1252_REV[cp]);
    else if (cp <= 0xff) bytes.push(cp);
    else for (const b of Buffer.from(ch, 'utf8')) bytes.push(b);
  }
  try {
    return Buffer.from(bytes).toString('utf8');
  } catch {
    return str;
  }
}

function walk(layers) {
  for (const l of layers || []) {
    if (l.t && l.t.d && l.t.d.k) {
      for (const k of l.t.d.k) {
        if (k.s && typeof k.s.t === 'string') k.s.t = fixText(k.s.t);
      }
    }
    if (l.layers) walk(l.layers);
  }
}

let count = 0;
for (const file of fs.readdirSync(dir).filter((f) => f.endsWith('.json'))) {
  const p = path.join(dir, file);
  const j = JSON.parse(fs.readFileSync(p, 'utf8'));
  walk(j.layers);
  if (j.assets) for (const a of j.assets) if (a.layers) walk(a.layers);
  delete j.chars; // 임베드 글리프 제거 → 네이티브 텍스트 렌더
  if (j.fonts && Array.isArray(j.fonts.list)) {
    for (const f of j.fonts.list) f.fFamily = 'Noto Sans KR';
  }
  fs.writeFileSync(p, JSON.stringify(j));
  count++;
}
console.log(`패치 완료: ${count}개 JSON (텍스트 복원 + chars 제거 + 폰트 통일)`);
