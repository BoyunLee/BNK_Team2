import { useEffect, useRef, useState } from 'react';
import { Document, Page, pdfjs } from 'react-pdf';
import './PdfViewer.css';

// PDF.js 워커 (Vite 가 번들 자산으로 처리)
pdfjs.GlobalWorkerOptions.workerSrc = new URL(
  'pdfjs-dist/build/pdf.worker.min.mjs',
  import.meta.url,
).toString();

// CJK 글리프/표준 폰트 렌더링용 데이터 (public/pdfjs 에 복사됨)
const PDF_OPTIONS = {
  cMapUrl: '/pdfjs/cmaps/',
  cMapPacked: true,
  standardFontDataUrl: '/pdfjs/standard_fonts/',
};

/**
 * PDF 뷰어 모달 — 약관 열람용. 하단에 다운로드 버튼.
 * onAgree 를 주면 끝까지 스크롤 후 활성화되는 "동의" 버튼이 함께 표시된다(동의서용).
 */
export function PdfViewer({
  title,
  url,
  onClose,
  onAgree,
  agreeText = '동의',
}: {
  title: string;
  url: string;
  onClose: () => void;
  onAgree?: () => void;
  agreeText?: string;
}) {
  const [numPages, setNumPages] = useState(0);
  const [reached, setReached] = useState(false);
  const scrollRef = useRef<HTMLDivElement>(null);
  const width = Math.min(window.innerWidth, 450) - 24;

  function handleScroll(el: HTMLDivElement) {
    if (el.scrollHeight - el.scrollTop - el.clientHeight < 48 && !reached) {
      setReached(true);
    }
  }

  // 문서가 화면보다 짧아 스크롤 불가하면 자동 통과
  useEffect(() => {
    if (numPages > 0) {
      const t = setTimeout(() => {
        const el = scrollRef.current;
        if (el && el.scrollHeight <= el.clientHeight + 8) setReached(true);
      }, 400);
      return () => clearTimeout(t);
    }
  }, [numPages]);

  return (
    <div className="pdfview" role="dialog" aria-label={title}>
      <header className="flow-head">
        <span className="flow-head__title">약관 보기</span>
        <button type="button" className="flow-head__close" onClick={onClose}>
          닫기
        </button>
      </header>
      <h2 className="pdfview__title">{title}</h2>

      <div
        className="pdfview__scroll"
        ref={scrollRef}
        onScroll={(e) => handleScroll(e.currentTarget)}
      >
        <Document
          file={url}
          options={PDF_OPTIONS}
          onLoadSuccess={({ numPages }) => setNumPages(numPages)}
          loading={<div className="pdfview__msg">불러오는 중…</div>}
          error={<div className="pdfview__msg">문서를 불러오지 못했습니다.</div>}
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

      {onAgree && !reached && <div className="pdfview__hint">아래로 스크롤</div>}

      <div className="pdfview__actions">
        <a
          className={`pdfview__download${onAgree ? ' pdfview__download--sub' : ''}`}
          href={url}
          download={`${title}.pdf`}
        >
          다운로드
        </a>
        {onAgree && (
          <button
            type="button"
            className="pdfview__agree"
            disabled={!reached}
            onClick={onAgree}
          >
            {reached ? agreeText : '내용을 모두 읽어주세요'}
          </button>
        )}
      </div>
    </div>
  );
}
