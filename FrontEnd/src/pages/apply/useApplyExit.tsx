import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertModal } from '../../components/AlertModal';
import { useApply } from '../../auth/ApplyContext';
import { cancelApplication } from '../../lib/loan';

/**
 * 대출 신청 플로우 공용 이탈 처리.
 * - 기본(중단): 진행 중 신청서를 취소한 뒤 상품 상세로.
 * - preserve(저장하고 나가기): 신청서를 취소하지 않아 재진입 시 이어서 진행 가능.
 *
 *   const { requestExit, exitModal } = useApplyExit(productId);
 *   const { requestExit: leave, exitModal: leaveModal } = useApplyExit(productId, { preserve: true });
 */
export function useApplyExit(productId: string, opts?: { preserve?: boolean }) {
  const preserve = opts?.preserve ?? false;
  const navigate = useNavigate();
  const { loanAccountNo, reset } = useApply();
  const [open, setOpen] = useState(false);

  const requestExit = () => setOpen(true);

  const confirmExit = async () => {
    setOpen(false);
    if (!preserve && loanAccountNo) {
      try {
        await cancelApplication(loanAccountNo);
      } catch {
        /* 이미 만료/취소된 경우 무시 */
      }
    }
    reset();
    navigate(`/product/${encodeURIComponent(productId)}`);
  };

  const exitModal = (
    <AlertModal
      open={open}
      title={preserve ? '잠깐!' : '대출 신청 중단'}
      message={
        preserve
          ? '진행 내용을 저장하고 나갈까요? 다음에 이어서 진행할 수 있어요.'
          : '대출 신청을 중단하고 상품 화면으로 돌아갈까요?'
      }
      confirmText={preserve ? '나가기' : '중단하기'}
      cancelText={preserve ? '계속 진행' : '계속하기'}
      onConfirm={confirmExit}
      onCancel={() => setOpen(false)}
    />
  );

  return { requestExit, exitModal };
}
