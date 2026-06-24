// 동의서 PDF를 ASCII 파일명(t1.pdf..t13.pdf)으로 public 에 복사한다.
// 한글/쉼표/중점 등 특수문자 파일명이 Vite 정적 서빙에서 폴백되는 문제를 회피.
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const src = 'C:/Users/pplue/Downloads/pdf/대출한도조회';
const dst = path.resolve(__dirname, '..', 'public', 'pdf', '대출한도조회');

// LoanLimitConsentPage 의 ITEMS 와 동일한 순서(id ↔ 원본 파일명)
const MAP = [
  ['t1', '여신거래 기본약관(가계용).pdf'],
  ['t2', '대출상품설명서(가계용).pdf'],
  ['t3', '개인(신용)정보 수집 이용제공 동의서.pdf'],
  ['t4', '개인(신용)정보 조회 동의서.pdf'],
  ['t5', '개인 신용정보의 제공활용에 대한 고객의 권리안내.pdf'],
  ['t6', '비대면 대출 및 스크래핑 서비스 이용 신청서.pdf'],
  ['t7', '개인(신용)정보 제3자 제공 동의서(금융결제원 자동이체정보).pdf'],
  ['t8', '개인(신용)정보 수집이용제공 동의서[공공 마이데이터(꾸러미)].pdf'],
  ['t9', '본인행정정보 제3자제공 요구서[공공 마이데이터_여신].pdf'],
  ['t10', '개인정보 수집·이용 및 제3자 제공 동의서(비여신,모바일안심플러스서비스).pdf'],
  ['t11', '개인정보 제3자 제공 동의서(비여신,모바일안심플러스서비스-KCB).pdf'],
  ['t12', '개인정보 제3자 제공 동의서(비여신,모바일안심플러스서비스-이동통신사).pdf'],
  ['t13', '모바일안심플러스 이용약관.pdf'],
];

fs.mkdirSync(dst, { recursive: true });
let ok = 0;
for (const [id, name] of MAP) {
  const from = path.join(src, name);
  if (!fs.existsSync(from)) {
    console.error('  누락:', name);
    continue;
  }
  fs.copyFileSync(from, path.join(dst, `${id}.pdf`));
  ok++;
}
console.log(`복사 완료: ${ok}/${MAP.length} (-> ${dst}\\t{n}.pdf)`);
