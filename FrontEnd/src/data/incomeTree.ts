// 소득구분 계층 — flow/대출신청/소득구분.txt 기준.
// 사업소득자는 하위가 없고, 선택 시 별도 '사업자 정보' 입력 페이지로 이동한다.

export interface IncomeNode {
  label: string;
  children?: IncomeNode[];
}

export const INCOME_TREE: IncomeNode[] = [
  {
    label: '급여소득자',
    children: [
      {
        label: '01. 일반기업',
        children: [
          { label: '정규직>상장/등록/외감법인' },
          { label: '정규직>기타법인' },
          { label: '정규직>부실업체(법정관리/화의 등)' },
          { label: '임시직' },
        ],
      },
      {
        label: '02. 금융계',
        children: [
          { label: '은행' },
          { label: '은행 외>정규직' },
          { label: '은행 외>임시직' },
        ],
      },
      {
        label: '03. 법조계>변호사/그외',
        children: [{ label: '변호사' }, { label: '사법연수원생/판검사시보' }],
      },
      { label: '04. 공무원>일반공무원', children: [{ label: '공무원' }] },
      { label: '05. 공무원>판사/검사', children: [{ label: '판사/검사' }] },
      {
        label: '06. 공무원>국공립학교교직원',
        children: [
          { label: '교사/교수>정규학교/특수학교/유치원/놀이방' },
          { label: '사무행정직원>국공립학교' },
        ],
      },
      {
        label: '07. 교육계>사립학교교사/교수',
        children: [{ label: '교사/교수>정규학교/특수학교/유치원/놀이방' }],
      },
      {
        label: '08. 교육계>직원/그외',
        children: [
          { label: '교사>사설학원' },
          { label: '사무행정직원>사립학교 정규직' },
          { label: '사무행정직원>사립학교 임시직' },
          { label: '연구원>연구소/학술단체 등 정규직' },
          { label: '연구원>연구소/학술단체 등 임시직' },
        ],
      },
      {
        label: '09. 의료계>의사/약사/수의사',
        children: [
          { label: '종합병원/국공립의료기관>병원장/전문의' },
          { label: '일반병원/의원>병원장/전문의' },
          { label: '레지던트/인턴/의사' },
          { label: '약사' },
          { label: '수의사' },
        ],
      },
      {
        label: '10. 의료계>간호사/직원/그외',
        children: [
          { label: '간호사' },
          { label: '의료기사' },
          { label: '병의원사무직원>정규직' },
          { label: '병의원사무직원>임시직' },
        ],
      },
      {
        label: '11. 공기업/공공단체',
        children: [
          { label: '정부투자기관' },
          { label: '정부재투자기관/정부재정지원기관>정규직' },
          { label: '정부재투자기관/정부재정지원기관>임시직' },
          { label: '비영리공동단체>정규직' },
          { label: '비영리공동단체>임시직' },
        ],
      },
      {
        label: '12. 언론계',
        children: [
          { label: '정규직(기자/PD/아나운서 포함)' },
          { label: '임시직' },
        ],
      },
      {
        label: '13. 기타전문직',
        children: [
          { label: '도선사/변리사' },
          { label: '공인회계사/관세사/세무사' },
          { label: '기타 전문자격증 보유' },
        ],
      },
      {
        label: '14. 기타',
        children: [
          { label: '종교인' },
          { label: '예술가' },
          { label: '스포츠인' },
        ],
      },
    ],
  },
  { label: '사업소득자' },
  {
    label: '기타소득자',
    children: [
      { label: '모집인' },
      { label: '연금소득자' },
      { label: '이자/배당/임대소득자' },
      {
        label: '정년퇴직자',
        children: [
          { label: '퇴직공무원/군인/교사' },
          { label: '기타 정년퇴직자' },
        ],
      },
      { label: '주부' },
      { label: '학생' },
    ],
  },
];

/** 경로(선택한 라벨 배열)의 끝 노드를 반환. */
export function nodeAtPath(path: string[]): IncomeNode | null {
  let arr: IncomeNode[] = INCOME_TREE;
  let node: IncomeNode | null = null;
  for (const label of path) {
    node = arr.find((n) => n.label === label) ?? null;
    if (!node) return null;
    arr = node.children ?? [];
  }
  return node;
}
