import { useState } from 'react';
import { Link } from 'react-router-dom';
import type { Product } from '../types/product';
import { ProductHeader } from '../components/ProductHeader';
import { ProductTabs, type TabKey } from '../components/ProductTabs';
import { SectionList } from '../components/SectionList';
import { DocumentList } from '../components/DocumentList';
import { ActionBar } from '../components/ActionBar';
import '../styles/shell.css';

/**
 * 상품 상세 페이지. 첨부 화면의 구조를 그대로 조립:
 *   헤더 + 탭(상품안내/금리안내/상품약관) + 탭 본문 + 하단 CTA
 * 데이터는 product(Product) 하나로 주입받는다.
 */
export function ProductDetailPage({ product }: { product: Product }) {
  const [tab, setTab] = useState<TabKey>('info');

  return (
    <div className="app-shell">
      <div className="topbar">
        <Link className="topbar__back" to="/" aria-label="목록으로">
          ‹ 목록
        </Link>
      </div>
      <ProductHeader meta={product.meta} summary={product.summary} />
      <ProductTabs active={tab} onChange={setTab} />

      <main style={{ paddingBottom: 96 }}>
        {tab === 'info' && <SectionList sections={product.infoSections} />}
        {tab === 'rate' && <SectionList sections={product.rateSections} />}
        {tab === 'terms' && <DocumentList documents={product.documents} />}
      </main>

      <ActionBar onApply={() => alert('대출신청 플로우 연결 예정')} />
    </div>
  );
}
