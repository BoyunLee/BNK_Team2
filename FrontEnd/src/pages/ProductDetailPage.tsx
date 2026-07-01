import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import type { Product } from '../types/product';
import type { BeProductDetail } from '../lib/products';
import { ProductHeader } from '../components/ProductHeader';
import { ProductTabs, type TabKey } from '../components/ProductTabs';
import { SectionList } from '../components/SectionList';
import { DocumentList } from '../components/DocumentList';
import { LoanSimulator } from '../components/LoanSimulator';
import { ActionBar } from '../components/ActionBar';
import '../styles/shell.css';

/**
 * 상품 상세 페이지. 첨부 화면의 구조를 그대로 조립:
 *   헤더 + 탭(상품안내/금리안내/상품약관) + 탭 본문 + 하단 CTA
 * 데이터는 product(Product) 하나로 주입받는다.
 */
export function ProductDetailPage({
  product,
  detail,
}: {
  product: Product;
  detail: BeProductDetail | null;
}) {
  const [tab, setTab] = useState<TabKey>('info');
  const navigate = useNavigate();
  const { productId } = useParams<{ productId: string }>();

  return (
    <div className="app-shell">
      <div className="topbar topbar--detail">
        <Link className="topbar__back topbar__back--red" to="/products" aria-label="목록으로">
          ‹ 뒤로가기
        </Link>
      </div>
      <h1 className="page-title">대출상세</h1>
      <ProductHeader meta={product.meta} summary={product.summary} />
      <ProductTabs
        active={tab}
        onChange={setTab}
        action={
          <button
            type="button"
            className="tabs__compare"
            onClick={() => navigate(`/products?compareWith=${productId}`)}
            aria-label="다른 상품과 비교"
          >
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              aria-hidden="true"
            >
              <path d="M8 3 4 7l4 4" />
              <path d="M4 7h16" />
              <path d="m16 21 4-4-4-4" />
              <path d="M20 17H4" />
            </svg>
            상품 비교
          </button>
        }
      />

      <main style={{ paddingBottom: 96 }}>
        {tab === 'info' && <SectionList sections={product.infoSections} />}
        {tab === 'rate' && <SectionList sections={product.rateSections} />}
        {tab === 'terms' && <DocumentList documents={product.documents} />}
        {tab === 'sim' &&
          (detail ? (
            <LoanSimulator detail={detail} />
          ) : (
            <p style={{ padding: 24, color: '#999' }}>
              시뮬레이터 데이터를 불러오지 못했습니다.
            </p>
          ))}
      </main>

      <ActionBar onApply={() => navigate(`/apply/${productId}`)} />
    </div>
  );
}
