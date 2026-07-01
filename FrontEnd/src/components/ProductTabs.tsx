import './ProductTabs.css';

export type TabKey = 'info' | 'rate' | 'terms' | 'sim';

const TABS: { key: TabKey; label: string }[] = [
  { key: 'info', label: '상품안내' },
  { key: 'rate', label: '금리안내' },
  { key: 'terms', label: '상품약관' },
  { key: 'sim', label: '시뮬레이터' },
];

export function ProductTabs({
  active,
  onChange,
}: {
  active: TabKey;
  onChange: (k: TabKey) => void;
}) {
  return (
    <nav className="tabs" role="tablist" aria-label="상품 상세 탭">
      {TABS.map((t) => (
        <button
          key={t.key}
          role="tab"
          aria-selected={active === t.key}
          className="tabs__btn"
          onClick={() => onChange(t.key)}
        >
          {t.label}
        </button>
      ))}
    </nav>
  );
}
