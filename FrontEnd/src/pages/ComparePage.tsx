import { useEffect, useMemo, useState } from 'react';
import { Link, Navigate, useSearchParams } from 'react-router-dom';
import {
  fetchProductDetail,
  toProduct,
  type BeProductDetail,
} from '../lib/products';
import { formatRateRange } from '../lib/rate';
import { parseLimitWon } from '../lib/loanCalc';
import '../styles/shell.css';
import './MyLoansPage.css';
import './ComparePage.css';

/** 비교 화면용 상품 뷰모델 */
interface CmpItem {
  id: number;
  name: string;
  category: string;
  rateText: string;
  rateMinNum: number | null; // 최저금리(숫자) — 우위 강조용
  baseRateText: string;
  term: string;
  limitText: string;
  limitNum: number | null; // 한도(원) — 우위 강조용
  target: string;
  prefText: string;
  prefSum: number | null; // 우대금리 합계(%p) — 우위 강조용
}

function toCmpItem(d: BeProductDetail): CmpItem {
  const p = toProduct(d);
  const rateMinNum =
    d.rateMin != null && d.rateMin !== '' ? parseFloat(d.rateMin) : NaN;
  const prefSum = d.preferentialRates.reduce((a, b) => a + (b.rateValue ?? 0), 0);
  return {
    id: d.productId,
    name: d.productName,
    category: d.category,
    rateText: formatRateRange(d.rateMin, d.rateMax),
    rateMinNum: Number.isFinite(rateMinNum) ? rateMinNum : null,
    baseRateText: d.baseRate != null ? `연 ${d.baseRate}%` : (p.summary.baseRate ?? '-'),
    term: p.summary.term || '-',
    limitText: p.summary.limit || '-',
    limitNum: parseLimitWon(p.summary.limit),
    target: p.summary.target || '-',
    prefText: prefSum > 0 ? `최대 −${prefSum}%p` : '-',
    prefSum: prefSum > 0 ? prefSum : null,
  };
}

export function ComparePage() {
  const [params, setParams] = useSearchParams();
  const ids = useMemo(() => {
    const raw = params.get('ids') ?? '';
    const seen = new Set<number>();
    return raw
      .split(',')
      .map((s) => Number(s))
      .filter((n) => Number.isInteger(n) && n > 0 && !seen.has(n) && seen.add(n))
      .slice(0, 3);
  }, [params]);

  const [items, setItems] = useState<CmpItem[] | null>(null);

  useEffect(() => {
    if (ids.length < 2) return;
    let alive = true;
    Promise.all(ids.map((id) => fetchProductDetail(id).catch(() => null))).then(
      (list) => {
        if (!alive) return;
        setItems(
          list.filter((d): d is BeProductDetail => d != null).map(toCmpItem),
        );
      },
    );
    return () => {
      alive = false;
    };
  }, [ids]);

  // 우위 값 계산(강조 배지)
  const best = useMemo(() => {
    const list = items ?? [];
    const rateVals = list.map((x) => x.rateMinNum).filter((v): v is number => v != null);
    const limitVals = list.map((x) => x.limitNum).filter((v): v is number => v != null);
    const prefVals = list.map((x) => x.prefSum).filter((v): v is number => v != null);
    return {
      minRate: rateVals.length >= 2 ? Math.min(...rateVals) : null,
      maxLimit: limitVals.length >= 2 ? Math.max(...limitVals) : null,
      maxPref: prefVals.length >= 2 ? Math.max(...prefVals) : null,
    };
  }, [items]);

  // id 2개 미만이면 비교 불가 → 목록으로 (모든 hook 이후)
  if (ids.length < 2) return <Navigate to="/products" replace />;

  const removeItem = (id: number) => {
    const next = ids.filter((x) => x !== id);
    setParams(next.length ? { ids: next.join(',') } : {}, { replace: true });
  };

  const ROWS: {
    label: string;
    get: (x: CmpItem) => string;
    badge?: (x: CmpItem) => string | null;
  }[] = [
    { label: '카테고리', get: (x) => x.category },
    {
      label: '금리',
      get: (x) => x.rateText,
      badge: (x) =>
        best.minRate != null && x.rateMinNum === best.minRate ? '최저' : null,
    },
    { label: '기준금리', get: (x) => x.baseRateText },
    { label: '대출기간', get: (x) => x.term },
    {
      label: '한도',
      get: (x) => x.limitText,
      badge: (x) =>
        best.maxLimit != null && x.limitNum === best.maxLimit ? '최대' : null,
    },
    { label: '대상', get: (x) => x.target },
    {
      label: '우대금리',
      get: (x) => x.prefText,
      badge: (x) =>
        best.maxPref != null && x.prefSum === best.maxPref ? '최대' : null,
    },
  ];

  return (
    <div className="app-shell my-loans">
      <div className="topbar">
        <Link className="topbar__back topbar__back--red" to="/products" aria-label="상품몰로">
          ‹ 상품몰
        </Link>
      </div>
      <h1 className="page-title">상품 비교</h1>

      <main className="ml-main">
        {!items ? (
          <p className="ml-empty">불러오는 중…</p>
        ) : items.length < 2 ? (
          <p className="ml-empty">비교할 상품 정보를 불러오지 못했습니다.</p>
        ) : (
          <div className="cmp-scroll">
            <table className="cmp-table">
              <thead>
                <tr>
                  <th className="cmp-corner" scope="col" />
                  {items.map((x) => (
                    <th key={x.id} scope="col" className="cmp-head">
                      <button
                        type="button"
                        className="cmp-head__remove"
                        onClick={() => removeItem(x.id)}
                        aria-label={`${x.name} 비교에서 제거`}
                      >
                        ×
                      </button>
                      <span className="cmp-head__name">{x.name}</span>
                      <Link className="cmp-head__link" to={`/product/${x.id}`}>
                        상세보기
                      </Link>
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {ROWS.map((row) => (
                  <tr key={row.label}>
                    <th scope="row" className="cmp-rowhead">
                      {row.label}
                    </th>
                    {items.map((x) => {
                      const badge = row.badge?.(x) ?? null;
                      return (
                        <td key={x.id} className={badge ? 'is-best' : undefined}>
                          <span>{row.get(x)}</span>
                          {badge && <em className="cmp-best">{badge}</em>}
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  );
}
