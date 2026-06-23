import type { DocumentLink } from '../types/product';
import './DocumentList.css';

export function DocumentList({ documents }: { documents: DocumentLink[] }) {
  if (!documents.length) {
    return <div className="docs-empty">약관 문서가 없습니다.</div>;
  }
  return (
    <div className="docs">
      {documents.map((d, i) => (
        <a
          className="docs__item"
          key={`${d.title}-${i}`}
          href={d.url}
          target="_blank"
          rel="noopener noreferrer"
        >
          <span>{d.title}</span>
          <span className="docs__chev" aria-hidden>
            ›
          </span>
        </a>
      ))}
    </div>
  );
}
