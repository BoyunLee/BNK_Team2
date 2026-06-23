import type { ProductSummary, ProductMeta } from '../types/product';
import { formatRate } from '../lib/rate';
import './ProductHeader.css';

/**
 * 상세 페이지 헤더(히어로). summary/meta 의 값만 받아 직접 그린다.
 * HTML 주입 없음 — 순수 데이터 렌더링.
 */
export function ProductHeader({
  meta,
  summary,
}: {
  meta: ProductMeta;
  summary: ProductSummary;
}) {
  const { rateMin, rateMax } = summary;
  const hasMin = rateMin != null;
  const hasMax = rateMax != null;
  const showRange = hasMin || hasMax;
  // 단일 표기 조건: 한쪽만 있거나(예: 수식형 단일) 최저=최고(예: 연 6%).
  // → "최저/최고" 라벨 없이 값 하나만 보여준다.
  const singleRate =
    hasMin !== hasMax || (hasMin && hasMax && rateMin === rateMax);
  const singleValue = (rateMin ?? rateMax) as string;

  return (
    <header className="hero">
      <div className="hero__brand">
        <b>BNK</b>
        <span>부산은행</span>
      </div>

      <div className="hero__tag">{meta.name}</div>
      <h1 className="hero__pitch">{summary.catchphrase}</h1>

      <div className="hero__asof">기준일자 : {meta.baseDate}</div>

      {showRange && (
        <div className="hero__rates">
          {singleRate ? (
            <span>
              <em>{formatRate(singleValue)}</em>
            </span>
          ) : (
            <>
              <span>
                최저 <em>{formatRate(rateMin as string)}</em>
              </span>
              <span>
                최고 <em>{formatRate(rateMax as string)}</em>
              </span>
            </>
          )}
        </div>
      )}

      <div className="hero__meta">
        <div className="hero__col">
          <div className="hero__k">대출한도(최대)</div>
          <div className="hero__v">{summary.limit || '–'}</div>
        </div>
        <div className="hero__col">
          <div className="hero__k">대출기간</div>
          <div className="hero__v">{summary.term || '–'}</div>
        </div>
      </div>

      {summary.target && (
        <div className="hero__target">
          <div className="hero__k">대출대상</div>
          <div className="hero__target-v">{summary.target}</div>
        </div>
      )}
    </header>
  );
}
