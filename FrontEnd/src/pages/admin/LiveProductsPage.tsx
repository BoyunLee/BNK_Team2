import { useEffect, useState } from 'react';
import { listLiveProducts, type LiveProduct } from '../../lib/admin';

/** 현재 사용자에게 노출되는 라이브(AS-IS) 상품. 배포 결과가 여기에 반영된다. */
export function LiveProductsPage() {
  const [products, setProducts] = useState<LiveProduct[]>([]);

  useEffect(() => {
    listLiveProducts().then(setProducts);
  }, []);

  return (
    <>
      <div className="adm-topbar">
        <h1>판매 상품 (AS-IS)</h1>
      </div>
      <div className="adm-content">
        <p className="adm-hint" style={{ marginTop: 0 }}>
          현재 라이브로 배포되어 사용자에게 노출 중인 상품입니다. 결재·배포가 완료되면 이 목록이
          갱신됩니다.
        </p>
        <div className="adm-card" style={{ padding: 0, overflow: 'hidden' }}>
          <table className="adm-table">
            <thead>
              <tr>
                <th style={{ width: 70 }}>ID</th>
                <th>상품명</th>
                <th style={{ width: 90 }}>카테고리</th>
                <th style={{ width: 130 }}>금리</th>
                <th style={{ width: 90 }}>상태</th>
                <th style={{ width: 70 }}>버전</th>
              </tr>
            </thead>
            <tbody>
              {products.map((p) => (
                <tr key={p.productId}>
                  <td className="num">{p.productId}</td>
                  <td>{p.snapshot.productName}</td>
                  <td>{p.snapshot.category}</td>
                  <td className="num">
                    {p.snapshot.rateMin}~{p.snapshot.rateMax}%
                  </td>
                  <td>
                    <span
                      className={`adm-badge adm-badge--${
                        p.status === 'SALE' ? 'DEPLOYED' : 'CANCELLED'
                      }`}
                    >
                      {p.status === 'SALE' ? '판매중' : '판매중지'}
                    </span>
                  </td>
                  <td className="num">v{p.version}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {products.length === 0 && <div className="adm-empty">상품이 없습니다.</div>}
        </div>
      </div>
    </>
  );
}
