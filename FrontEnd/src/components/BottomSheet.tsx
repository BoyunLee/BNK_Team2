import './BottomSheet.css';

export interface SheetOption {
  value: string;
  icon?: string;
}

/** 하단에서 올라오는 선택 시트(대출용도/상환방법 등). app-shell 폭에 맞춰 정렬. */
export function BottomSheet({
  title,
  open,
  options,
  onSelect,
  onClose,
}: {
  title: string;
  open: boolean;
  options: SheetOption[];
  onSelect: (value: string) => void;
  onClose: () => void;
}) {
  if (!open) return null;
  return (
    <div className="sheet" role="dialog" aria-label={title}>
      <div className="sheet__dim" onClick={onClose} />
      <div className="sheet__panel">
        <div className="sheet__head">
          <h3 className="sheet__title">{title}</h3>
          <button
            className="sheet__close"
            type="button"
            onClick={onClose}
            aria-label="닫기"
          >
            ×
          </button>
        </div>
        <ul className="sheet__list">
          {options.map((o) => (
            <li key={o.value}>
              <button
                className="sheet__item"
                type="button"
                onClick={() => {
                  onSelect(o.value);
                  onClose();
                }}
              >
                {o.icon && (
                  <span className="sheet__icon" aria-hidden="true">
                    {o.icon}
                  </span>
                )}
                <span className="sheet__label">{o.value}</span>
              </button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
