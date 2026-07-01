import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import type { Product } from '../types/product';
import {
  fetchProductDetail,
  toProduct,
  type BeProductDetail,
} from '../lib/products';
import { ProductDetailPage } from './ProductDetailPage';
import '../styles/shell.css';

/**
 * /product/:productId 라우트.
 * 백엔드 GET /products/{productId} 를 불러와 기존 Product 형태로 매핑한다.
 */
export function ProductDetailRoute() {
  const { productId } = useParams<{ productId: string }>();
  const [product, setProduct] = useState<Product | null>(null);
  const [detail, setDetail] = useState<BeProductDetail | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    setProduct(null);
    setDetail(null);
    setError(null);
    (async () => {
      try {
        const d = await fetchProductDetail(productId ?? '');
        if (alive) {
          setDetail(d);
          setProduct(toProduct(d));
        }
      } catch (e) {
        if (alive) setError(e instanceof Error ? e.message : String(e));
      }
    })();
    return () => {
      alive = false;
    };
  }, [productId]);

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

  return <ProductDetailPage product={product} detail={detail} />;
}
