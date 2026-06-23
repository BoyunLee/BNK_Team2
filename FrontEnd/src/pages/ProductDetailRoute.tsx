import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import type { Product } from '../types/product';
import { findByCode, loadIndex, loadProduct } from '../lib/data';
import { ProductDetailPage } from './ProductDetailPage';
import '../styles/shell.css';

/**
 * /product/:mkpdCd 라우트.
 * index.json 에서 mkpd_cd 로 항목을 찾아(카테고리/이름 필요) product.web.json 을 로드한다.
 */
export function ProductDetailRoute() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const [product, setProduct] = useState<Product | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    setProduct(null);
    setError(null);
    (async () => {
      try {
        const index = await loadIndex();
        const item = findByCode(index, mkpdCd ?? '');
        if (!item)
          throw new Error(`상품을 찾을 수 없습니다 (mkpd_cd=${mkpdCd}).`);
        const p = await loadProduct(item);
        if (alive) setProduct(p);
      } catch (e) {
        if (alive) setError(e instanceof Error ? e.message : String(e));
      }
    })();
    return () => {
      alive = false;
    };
  }, [mkpdCd]);

  if (error) {
    return (
      <div className="app-shell">
        <div className="topbar">
          <Link className="topbar__back" to="/" aria-label="목록으로">
            ‹ 목록
          </Link>
        </div>
        <div className="list-error" style={{ padding: '48px 24px' }}>
          <h2>상품을 불러오지 못했습니다</h2>
          <p>{error}</p>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="app-shell">
        <div className="topbar">
          <Link className="topbar__back" to="/" aria-label="목록으로">
            ‹ 목록
          </Link>
        </div>
        <div style={{ padding: 48, textAlign: 'center', color: '#999' }}>
          불러오는 중…
        </div>
      </div>
    );
  }

  return <ProductDetailPage product={product} />;
}
