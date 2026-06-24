import './AlertModal.css';

/** 중앙 알림 모달(확인 1버튼). app-shell 폭에 맞춰 정렬. */
export function AlertModal({
  open,
  title,
  message,
  confirmText = '확인',
  onConfirm,
}: {
  open: boolean;
  title?: string;
  message: string;
  confirmText?: string;
  onConfirm: () => void;
}) {
  if (!open) return null;
  return (
    <div className="modal" role="alertdialog" aria-modal="true">
      <div className="modal__dim" onClick={onConfirm} />
      <div className="modal__box">
        {title && <h3 className="modal__title">{title}</h3>}
        <p className="modal__msg">{message}</p>
        <button type="button" className="modal__confirm" onClick={onConfirm}>
          {confirmText}
        </button>
      </div>
    </div>
  );
}
