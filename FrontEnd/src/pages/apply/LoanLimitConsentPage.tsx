import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Document, Page, pdfjs } from 'react-pdf';
import '../../styles/shell.css';
import './apply.css';
import './LoanLimitConsentPage.css';

// PDF.js 워커 (Vite 가 번들 자산으로 처리)
pdfjs.GlobalWorkerOptions.workerSrc = new URL(
  'pdfjs-dist/build/pdf.worker.min.mjs',
  import.meta.url,
).toString();

const PDF_BASE = '/pdf/대출한도조회';

// CJK 글리프/표준 폰트 렌더링용 데이터 (public/pdfjs 에 복사됨).
// 미설정 시 일부 약관(예: 여신거래 기본약관) 글자가 렌더되지 않는다.
const PDF_OPTIONS = {
  cMapUrl: '/pdfjs/cmaps/',
  cMapPacked: true,
  standardFontDataUrl: '/pdfjs/standard_fonts/',
};

interface Doc {
  id: string;
  label: string;
  file: string;
}

// 캡쳐 순서대로. file 은 public/pdf/대출한도조회 의 실제 파일명과 1:1.
const ITEMS: Doc[] = [
  { id: 't1', label: '여신거래 기본약관(가계용)', file: '여신거래 기본약관(가계용).pdf' },
  { id: 't2', label: '대출상품설명서(가계용)', file: '대출상품설명서(가계용).pdf' },
  {
    id: 't3',
    label: '개인(신용)정보 수집 이용제공 동의서',
    file: '개인(신용)정보 수집 이용제공 동의서.pdf',
  },
  {
    id: 't4',
    label: '개인(신용)정보 조회 동의서',
    file: '개인(신용)정보 조회 동의서.pdf',
  },
  {
    id: 't5',
    label: '개인 신용정보의 제공활용에 대한 고객의 권리안내',
    file: '개인 신용정보의 제공활용에 대한 고객의 권리안내.pdf',
  },
  {
    id: 't6',
    label: '비대면 대출 및 스크래핑 서비스 이용 신청서',
    file: '비대면 대출 및 스크래핑 서비스 이용 신청서.pdf',
  },
  {
    id: 't7',
    label: '개인(신용)정보 제3자 제공 동의서(금융결제원 자동이체정보)',
    file: '개인(신용)정보 제3자 제공 동의서(금융결제원 자동이체정보).pdf',
  },
  {
    id: 't8',
    label: '개인(신용)정보 수집이용제공 동의서[공공 마이데이터(꾸러미)]',
    file: '개인(신용)정보 수집이용제공 동의서[공공 마이데이터(꾸러미)].pdf',
  },
  {
    id: 't9',
    label: '본인행정정보 제3자제공 요구서[공공 마이데이터_여신]',
    file: '본인행정정보 제3자제공 요구서[공공 마이데이터_여신].pdf',
  },
  {
    id: 't10',
    label: '개인정보 수집·이용 및 제3자 제공 동의서(비여신,모바일안심플러스서비스)',
    file: '개인정보 수집·이용 및 제3자 제공 동의서(비여신,모바일안심플러스서비스).pdf',
  },
  {
    id: 't11',
    label: '개인정보 제3자 제공 동의서(비여신,모바일안심플러스서비스-KCB)',
    file: '개인정보 제3자 제공 동의서(비여신,모바일안심플러스서비스-KCB).pdf',
  },
  {
    id: 't12',
    label: '개인정보 제3자 제공 동의서(비여신,모바일안심플러스서비스-이동통신사)',
    file: '개인정보 제3자 제공 동의서(비여신,모바일안심플러스서비스-이동통신사).pdf',
  },
  { id: 't13', label: '모바일안심플러스 이용약관', file: '모바일안심플러스 이용약관.pdf' },
];

export function LoanLimitConsentPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const productCd = mkpdCd ?? '';
  const back = () => navigate(`/product/${encodeURIComponent(productCd)}`);

  const [agreed, setAgreed] = useState<Set<string>>(new Set());
  const [openId, setOpenId] = useState<string | null>(null);
  const [seq, setSeq] = useState(false); // 전체동의 순차 진행 모드
  const [showNotice, setShowNotice] = useState(false); // 보이스피싱 유의사항 시트

  // 유의사항 확인 → 간편비밀번호 본인 인증(인증 후 한도조회 완료)
  const proceedToAuth = () => {
    const after = `/apply/${encodeURIComponent(productCd)}/done`;
    navigate(
      `/apply/${encodeURIComponent(productCd)}/auth?next=${encodeURIComponent(after)}`,
    );
  };

  const allAgreed = agreed.size === ITEMS.length;
  const openItem = ITEMS.find((i) => i.id === openId) ?? null;

  // 한 문서 동의 처리. 순차 모드면 다음 미동의 문서로 자동 이동.
  const agreeOne = (id: string) => {
    const next = new Set(agreed).add(id);
    setAgreed(next);
    if (seq) {
      const remaining = ITEMS.find((i) => !next.has(i.id));
      if (remaining) {
        setOpenId(remaining.id);
        return;
      }
      setSeq(false);
    }
    setOpenId(null);
  };

  // 전체동의: 맨 위 미동의 문서부터 차례로 보여주며 동의 진행
  const startSequential = () => {
    const first = ITEMS.find((i) => !agreed.has(i.id));
    if (!first) return;
    setSeq(true);
    setOpenId(first.id);
  };

  // 뷰어 닫기(닫기 버튼) → 순차 모드 중단
  const closeViewer = () => {
    setOpenId(null);
    setSeq(false);
  };

  return (
    <div className="app-shell">
      <header className="flow-head">
        <button type="button" className="flow-head__back" onClick={back}>
          ‹ 뒤로가기
        </button>
      </header>
      <h1 className="limit-title">대출한도조회</h1>

      <main className="limit-body">
        <button
          type="button"
          className="agree-all"
          disabled={allAgreed}
          onClick={startSequential}
        >
          전체동의
        </button>

        <ul className="consent-list">
          {ITEMS.map((it) => (
            <li key={it.id}>
              <button
                type="button"
                className="consent-item"
                onClick={() => setOpenId(it.id)}
              >
                <span
                  className={`consent-check${agreed.has(it.id) ? ' on' : ''}`}
                  aria-hidden="true"
                >
                  ✓
                </span>
                <span className="consent-label">[필수] {it.label}</span>
                <span className="consent-chev" aria-hidden="true">
                  ›
                </span>
              </button>
            </li>
          ))}
        </ul>

        <p className="limit-note">
          - 특별고용지원업종(조선업) 근로자께서는 가까운 영업점에서 대출을
          상담하여 주시기 바랍니다.
        </p>
      </main>

      <div className="limit-actions">
        <button type="button" className="limit-cancel" onClick={back}>
          취소
        </button>
        <button
          type="button"
          className="limit-next"
          disabled={!allAgreed}
          onClick={() => setShowNotice(true)}
        >
          다음
        </button>
      </div>

      {openItem && (
        <DocViewer
          key={openItem.id}
          item={openItem}
          seqHint={seq}
          onClose={closeViewer}
          onAgree={() => agreeOne(openItem.id)}
        />
      )}

      {showNotice && (
        <div className="notice" role="dialog" aria-label="유의사항">
          <div className="notice__dim" onClick={() => setShowNotice(false)} />
          <div className="notice__sheet">
            <div className="notice__head">
              <h3 className="notice__head-title">꼭 확인해주세요</h3>
              <button
                type="button"
                className="notice__close"
                onClick={() => setShowNotice(false)}
                aria-label="닫기"
              >
                ×
              </button>
            </div>
            <div className="notice__body">
              <h2 className="notice__title">
                보이스피싱 예방 및 대출의사 확인을 위한 유의사항
              </h2>
              <ul className="notice__list">
                <li>
                  보이스 피싱 피해 예방 등 소중한 재산보호를 위하여 대출 실행 전
                  휴대전화를 통한 본인확인이 되어야 해요.
                </li>
                <li>
                  본인확인은 내 명의(주민등록번호 기준)로 가입된 휴대전화만
                  가능해요.
                  <p className="notice__sub">
                    - 다른 사람 명의이거나, 휴대전화가 없다면 대출을 신청할 수
                    없어요.
                  </p>
                </li>
                <li>
                  검찰, 경찰, 금융감독원 등 전화로 이체를 요청하는 전화를 받았다면
                  보이스피싱일 수 있으니 부산은행 고객센터(
                  <span className="notice__tel">1588-6200</span>)로 문의해주세요.
                  <p className="notice__sub">
                    - 부산은행 고객센터 전화 후 안내에 따라 * → 5 → # 입력
                  </p>
                </li>
              </ul>
            </div>
            <div className="notice__actions">
              <button
                type="button"
                className="notice__confirm"
                onClick={proceedToAuth}
              >
                모두 확인했어요
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

/** 단일 약관 PDF 뷰어. 끝까지 스크롤해야 동의 버튼 활성화. */
function DocViewer({
  item,
  seqHint,
  onClose,
  onAgree,
}: {
  item: Doc;
  seqHint: boolean;
  onClose: () => void;
  onAgree: () => void;
}) {
  const [numPages, setNumPages] = useState(0);
  const [reached, setReached] = useState(false);
  const scrollRef = useRef<HTMLDivElement>(null);
  // ASCII id 로 서빙(한글/쉼표 파일명 서빙 문제 회피), 다운로드명만 한글 라벨
  const url = `${PDF_BASE}/${item.id}.pdf`;
  const width = Math.min(window.innerWidth, 450) - 24;

  function handleScroll(el: HTMLDivElement) {
    if (el.scrollHeight - el.scrollTop - el.clientHeight < 48) {
      if (!reached) setReached(true);
    }
  }

  // 문서가 화면보다 짧아 스크롤이 불가능하면 자동으로 통과 처리
  useEffect(() => {
    if (numPages > 0) {
      const t = setTimeout(() => {
        const el = scrollRef.current;
        if (el && el.scrollHeight <= el.clientHeight + 8) setReached(true);
      }, 400);
      return () => clearTimeout(t);
    }
  }, [numPages]);

  const agreeLabel = !reached
    ? '내용을 모두 읽어주세요'
    : seqHint
      ? '동의하고 계속'
      : '동의';

  return (
    <div className="docview">
      <header className="flow-head">
        <span className="flow-head__title">약관·동의서 보기</span>
        <button type="button" className="flow-head__close" onClick={onClose}>
          닫기
        </button>
      </header>
      <h2 className="docview__title">[필수] {item.label}</h2>

      <div
        className="docview__scroll"
        ref={scrollRef}
        onScroll={(e) => handleScroll(e.currentTarget)}
      >
        <Document
          file={url}
          options={PDF_OPTIONS}
          onLoadSuccess={({ numPages }) => setNumPages(numPages)}
          loading={<div className="docview__msg">불러오는 중…</div>}
          error={<div className="docview__msg">문서를 불러오지 못했습니다.</div>}
        >
          {Array.from({ length: numPages }, (_, i) => (
            <Page
              key={i}
              pageNumber={i + 1}
              width={width}
              renderTextLayer={false}
              renderAnnotationLayer={false}
            />
          ))}
        </Document>
      </div>

      {!reached && <div className="docview__hint">아래로 스크롤</div>}

      <div className="docview__actions">
        <a className="docview__download" href={url} download={item.file}>
          다운로드
        </a>
        <button
          type="button"
          className="docview__agree"
          disabled={!reached}
          onClick={onAgree}
        >
          {agreeLabel}
        </button>
      </div>
    </div>
  );
}
