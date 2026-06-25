import './AlertModal.css';

/**
 * 중앙 알림/확인 모달. onCancel 을 주면 취소·확인 2버튼 확인 다이얼로그가 된다.
 * app-shell 폭에 맞춰 정렬.
 */
export function AlertModal({
  open,
  title,
  message,
  confirmText = '확인',
  cancelText = '취소',
  onConfirm,
  onCancel,
}: {
  open: boolean;
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  onConfirm: () => void;
  onCancel?: () => void;
}) {
  if (!open) return null;
  const dismiss = onCancel ?? onConfirm;
  return (
    <div className="modal" role="alertdialog" aria-modal="true">
      <div className="modal__dim" onClick={dismiss} />
      <div className="modal__box">
        {title && <h3 className="modal__title">{title}</h3>}
        <p className="modal__msg">{message}</p>
        {onCancel ? (
          <div className="modal__actions">
            <button type="button" className="modal__cancel" onClick={onCancel}>
              {cancelText}
            </button>
            <button type="button" className="modal__confirm" onClick={onConfirm}>
              {confirmText}
            </button>
          </div>
        ) : (
          <button type="button" className="modal__confirm" onClick={onConfirm}>
            {confirmText}
          </button>
        )}
      </div>
    </div>
  );
}
