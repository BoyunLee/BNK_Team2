import type { Section } from '../types/product';
import { HtmlBlock } from './HtmlBlock';
import './SectionList.css';

/**
 * 제목 + 본문 섹션 리스트. 상품안내/금리안내 탭에서 공용.
 * 제목과 래퍼는 직접 그리고, 본문은 HtmlBlock(주입)에 위임한다.
 * 내용이 빈 섹션은 그리지 않는다(크롤러가 1차로 걸러도 방어적으로).
 */
export function SectionList({ sections }: { sections: Section[] }) {
  const visible = sections.filter((s) => s.html && s.html.trim());

  if (visible.length === 0) {
    return <div className="section-empty">표시할 내용이 없습니다.</div>;
  }

  return (
    <div className="section-list">
      {visible.map((s, i) => (
        <section className="section" key={`${s.title}-${i}`}>
          <h2 className="section__title">{s.title}</h2>
          <HtmlBlock html={s.html} />
        </section>
      ))}
    </div>
  );
}
