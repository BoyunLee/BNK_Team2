import DOMPurify from 'dompurify';
import './HtmlBlock.css';

/**
 * 크롤러가 넘긴 HTML 조각(표·우대금리 리스트·변동 블록 등)을 렌더링하는
 * 유일한 컴포넌트. 모든 본문 HTML 은 반드시 이 컴포넌트를 거친다.
 *
 * 보안: product.web.json 의 html 필드는 UNSANITIZED 상태이므로
 * (크롤러 _note 참고) 여기서 DOMPurify.sanitize 로 한 번 거른다.
 * 신뢰 경계: 자사 데이터라도 크롤링 파이프라인을 통과했으므로 정화 필수.
 */
export function HtmlBlock({ html }: { html: string }) {
  const clean = DOMPurify.sanitize(html ?? '');
  return (
    <div className="product-html" dangerouslySetInnerHTML={{ __html: clean }} />
  );
}
