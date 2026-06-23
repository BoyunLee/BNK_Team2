import './ActionBar.css';

/**
 * 하단 고정 CTA. 대출신청.
 * onApply 를 주입받아 동작은 상위에서 결정.
 */
export function ActionBar({ onApply }: { onApply?: () => void }) {
  return (
    <div className="cta">
      <button className="cta__btn cta__btn--main" onClick={onApply}>
        대출신청
      </button>
    </div>
  );
}
