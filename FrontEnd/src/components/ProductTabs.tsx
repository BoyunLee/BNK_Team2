import type { ReactNode } from 'react';
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
  action,
}: {
  active: TabKey;
  onChange: (k: TabKey) => void;
  /** 탭 우측(시뮬레이터 옆)에 붙는 액션 버튼 슬롯 */
  action?: ReactNode;
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
      {action}
    </nav>
  );
}
