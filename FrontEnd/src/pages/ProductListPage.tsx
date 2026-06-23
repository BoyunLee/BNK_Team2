import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import type { ProductCategory, ProductIndexItem } from '../types/product';
import { loadIndex } from '../lib/data';
import { formatRateRange } from '../lib/rate';
import '../styles/shell.css';
import './ProductListPage.css';

const CATEGORIES: ProductCategory[] = [
  '신용대출',
  '담보대출',
  '서민금융',
  '보증서대출',
];

type Filter = ProductCategory | '전체';

export function ProductListPage() {
  const [index, setIndex] = useState<ProductIndexItem[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<Filter>('전체');
  const [query, setQuery] = useState('');

  useEffect(() => {
    loadIndex()
      .then(setIndex)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)));
  }, []);

  const counts = useMemo(() => {
    const m = new Map<Filter, number>();
    if (index) {
      m.set('전체', index.length);
      for (const c of CATEGORIES)
        m.set(c, index.filter((p) => p.category === c).length);
    }
    return m;
  }, [index]);

  const filtered = useMemo(() => {
    if (!index) return [];
    // 검색: 공백 무시 + 대소문자 무시로 상품명/캐치프레이즈 부분일치
    const q = query.trim().toLowerCase().replace(/\s+/g, '');
    return index.filter((p) => {
      if (filter !== '전체' && p.category !== filter) return false;
      if (!q) return true;
      const hay = `${p.name}${p.catchphrase}`.toLowerCase().replace(/\s+/g, '');
      return hay.includes(q);
    });
  }, [index, filter, query]);

  if (error) {
    return (
      <div className="app-shell">
        <div className="list-error">
          <h2>목록을 불러오지 못했습니다</h2>
          <p>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="app-shell">
      <header className="list-head">
        <div className="list-head__brand">
          <b>BNK</b>
          <span>부산은행</span>
        </div>
        <h1 className="list-head__title">여신상품몰</h1>
        <p className="list-head__sub">부산은행 대출상품을 한눈에</p>
      </header>

      <div className="search">
        <svg
          className="search__icon"
          width="18"
          height="18"
          viewBox="0 0 24 24"
          aria-hidden="true"
        >
          <path
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            d="m21 21-4.3-4.3M11 18a7 7 0 1 0 0-14 7 7 0 0 0 0 14Z"
          />
        </svg>
        <input
          className="search__input"
          type="search"
          inputMode="search"
          placeholder="상품명 검색"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          aria-label="상품명 검색"
        />
        {query && (
          <button
            className="search__clear"
            type="button"
            onClick={() => setQuery('')}
            aria-label="검색어 지우기"
          >
            ×
          </button>
        )}
      </div>

      <nav className="chips" aria-label="상품 유형 필터">
        {(['전체', ...CATEGORIES] as Filter[]).map((c) => (
          <button
            key={c}
            className="chip"
            aria-pressed={filter === c}
            onClick={() => setFilter(c)}
          >
            {c}
            <span className="chip__count">{counts.get(c) ?? 0}</span>
          </button>
        ))}
      </nav>

      <main className="list-main">
        {!index ? (
          <ul className="cards" aria-busy="true">
            {Array.from({ length: 4 }).map((_, i) => (
              <li key={i} className="card card--skeleton" />
            ))}
          </ul>
        ) : filtered.length === 0 ? (
          <div className="list-empty">
            {query.trim()
              ? `'${query.trim()}' 검색 결과가 없습니다.`
              : '해당 유형의 상품이 없습니다.'}
          </div>
        ) : (
          <ul className="cards">
            {filtered.map((p) => (
              <li key={p.mkpd_cd}>
                <Link
                  className="card"
                  to={`/product/${encodeURIComponent(p.mkpd_cd)}`}
                >
                  <span className="card__cat">{p.category}</span>
                  <span className="card__name">{p.name}</span>
                  {p.catchphrase && (
                    <span className="card__pitch">{p.catchphrase}</span>
                  )}
                  <span className="card__rate">
                    {formatRateRange(p.rateMin, p.rateMax)}
                  </span>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </main>
    </div>
  );
}
