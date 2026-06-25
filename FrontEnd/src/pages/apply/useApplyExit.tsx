import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertModal } from '../../components/AlertModal';
import { useApply } from '../../auth/ApplyContext';
import { cancelApplication } from '../../lib/loan';

/**
 * 대출 신청 플로우 공용 "중단" 처리.
 * 뒤로가기/닫기/취소 시 중단 확인 모달을 띄우고, 확인하면
 * 진행 중 신청서를 취소한 뒤 상품 상세로 돌아간다.
 *
 *   const { requestExit, exitModal } = useApplyExit(productId);
 *   <button onClick={requestExit}>‹ 뒤로가기</button>
 *   {exitModal}
 */
export function useApplyExit(productId: string) {
  const navigate = useNavigate();
  const { loanAccountNo, reset } = useApply();
  const [open, setOpen] = useState(false);

  const requestExit = () => setOpen(true);

  const confirmExit = async () => {
    setOpen(false);
    if (loanAccountNo) {
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
      title="대출 신청 중단"
      message="대출 신청을 중단하고 상품 화면으로 돌아갈까요?"
      confirmText="중단하기"
      cancelText="계속하기"
      onConfirm={confirmExit}
      onCancel={() => setOpen(false)}
    />
  );

  return { requestExit, exitModal };
}
