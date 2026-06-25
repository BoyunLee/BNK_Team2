import { useState } from 'react';
import type { DocumentLink } from '../types/product';
import { PdfViewer } from './PdfViewer';
import './DocumentList.css';

export function DocumentList({ documents }: { documents: DocumentLink[] }) {
  const [openDoc, setOpenDoc] = useState<DocumentLink | null>(null);

  if (!documents.length) {
    return <div className="docs-empty">약관 문서가 없습니다.</div>;
  }
  return (
    <div className="docs">
      {documents.map((d, i) => (
        <button
          className="docs__item"
          key={`${d.title}-${i}`}
          type="button"
          onClick={() => setOpenDoc(d)}
        >
          <span>{d.title}</span>
          <span className="docs__chev" aria-hidden>
            ›
          </span>
        </button>
      ))}

      {openDoc && (
        <PdfViewer
          title={openDoc.title}
          url={openDoc.url}
          onClose={() => setOpenDoc(null)}
        />
      )}
    </div>
  );
}
