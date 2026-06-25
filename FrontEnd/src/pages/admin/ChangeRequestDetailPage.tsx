import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAdminAuth } from '../../auth/AdminAuthContext';
import {
  approve,
  cancelRequest,
  deployNow,
  getChangeRequest,
  reject,
  submitForApproval,
  type AdminUser,
  type ProductChangeRequest,
} from '../../lib/admin';
import { SnapshotDiff, StatusBadge, SubmitModal, TypeBadge, formatDateTime } from './AdminBits';

/** datetime-local 기본값: 현재+10분 */
function defaultScheduleValue(): string {
  const d = new Date(Date.now() + 10 * 60 * 1000);
  const p = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}`;
}

export function ChangeRequestDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { admin, isApprover } = useAdminAuth();
  const [req, setReq] = useState<ProductChangeRequest | null>(null);
  const [showSubmit, setShowSubmit] = useState(false);
  const [schedule, setSchedule] = useState(defaultScheduleValue());
  const [comment, setComment] = useState('');
  const [rejecting, setRejecting] = useState(false);
  const [err, setErr] = useState('');

  const reload = useCallback(() => {
    getChangeRequest(Number(id)).then(setReq);
  }, [id]);

  useEffect(() => {
    reload();
  }, [reload]);

  if (!req) {
    return (
      <>
        <div className="adm-topbar">
          <h1>신청서</h1>
        </div>
        <div className="adm-content">
          <div className="adm-empty">불러오는 중…</div>
        </div>
      </>
    );
  }

  const run = async (fn: () => Promise<unknown>) => {
    setErr('');
    try {
      await fn();
      reload();
    } catch (e) {
      setErr(e instanceof Error ? e.message : '처리에 실패했습니다.');
    }
  };

  const onSubmitApprover = (approver: AdminUser) => {
    setShowSubmit(false);
    run(() => submitForApproval(req.id, approver));
  };

  const isDrafter = admin?.id === req.drafterId;
  const isMyApproval = isApprover && admin?.id === req.approverId;
  const editable = req.status === 'DRAFT' || req.status === 'REJECTED';

  return (
    <>
      <div className="adm-topbar">
        <button className="adm-btn adm-btn--ghost adm-btn--sm" onClick={() => navigate('/admin/requests')}>
          ← 목록
        </button>
        <h1>신청서 #{req.id}</h1>
        <StatusBadge status={req.status} />
      </div>
      <div className="adm-content">
        {/* 신청 정보 */}
        <div className="adm-card">
          <div className="adm-row" style={{ marginBottom: 14 }}>
            <TypeBadge type={req.changeType} />
            <strong style={{ fontSize: 16 }}>{req.title}</strong>
          </div>
          <dl className="adm-meta">
            <dt>담당자</dt>
            <dd>{req.drafterName}</dd>
            <dt>책임자</dt>
            <dd>{req.approverName ?? '-'}</dd>
            <dt>작성일</dt>
            <dd>{formatDateTime(req.createdAt)}</dd>
            <dt>상신일</dt>
            <dd>{formatDateTime(req.submittedAt)}</dd>
            <dt>결재일</dt>
            <dd>{formatDateTime(req.decidedAt)}</dd>
            {req.scheduledDeployAt && (
              <>
                <dt>배포예약</dt>
                <dd>{formatDateTime(req.scheduledDeployAt)}</dd>
              </>
            )}
            {req.deployedAt && (
              <>
                <dt>배포완료</dt>
                <dd>
                  {formatDateTime(req.deployedAt)}
                  {req.productId != null && ` · 상품ID ${req.productId}`}
                </dd>
              </>
            )}
            {req.decisionComment && (
              <>
                <dt>결재의견</dt>
                <dd>{req.decisionComment}</dd>
              </>
            )}
          </dl>
        </div>

        {/* AS-IS / TO-BE */}
        <div className="adm-card">
          <p className="adm-card__title">변경 내용 (AS-IS / TO-BE)</p>
          <SnapshotDiff asis={req.asis} tobe={req.tobe} />
        </div>

        {err && <div className="adm-err">{err}</div>}

        {/* 담당자 액션: 작성중/반려 */}
        {editable && isDrafter && (
          <div className="adm-card">
            <p className="adm-card__title">담당자 작업</p>
            {req.status === 'REJECTED' && (
              <p className="adm-hint" style={{ marginTop: -4 }}>
                반려된 신청서입니다. 내용을 수정 후 다시 상신할 수 있습니다.
              </p>
            )}
            <div className="adm-row">
              <button
                className="adm-btn"
                onClick={() => navigate(`/admin/requests/${req.id}/edit`)}
              >
                내용 수정
              </button>
              <button className="adm-btn adm-btn--primary" onClick={() => setShowSubmit(true)}>
                결재 상신
              </button>
              <div className="adm-spacer" />
              <button
                className="adm-btn adm-btn--danger"
                onClick={() => run(() => cancelRequest(req.id))}
              >
                신청 취소
              </button>
            </div>
          </div>
        )}

        {/* 책임자 액션: 결재대기 */}
        {req.status === 'PENDING' &&
          (isMyApproval ? (
            <div className="adm-card">
              <p className="adm-card__title">결재 (책임자)</p>
              {!rejecting ? (
                <>
                  <div className="adm-field" style={{ maxWidth: 320 }}>
                    <label>배포예약 시각 (승인 시 형상이행 시점)</label>
                    <input
                      type="datetime-local"
                      value={schedule}
                      onChange={(e) => setSchedule(e.target.value)}
                    />
                  </div>
                  <div className="adm-field">
                    <label>결재의견 (선택)</label>
                    <textarea value={comment} onChange={(e) => setComment(e.target.value)} />
                  </div>
                  <div className="adm-row">
                    <button
                      className="adm-btn adm-btn--primary"
                      onClick={() =>
                        run(() =>
                          approve(req.id, new Date(schedule).toISOString(), comment || undefined),
                        )
                      }
                    >
                      승인 + 배포예약
                    </button>
                    <button className="adm-btn adm-btn--danger" onClick={() => setRejecting(true)}>
                      반려
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <div className="adm-field">
                    <label>반려 사유 (필수)</label>
                    <textarea value={comment} onChange={(e) => setComment(e.target.value)} />
                  </div>
                  <div className="adm-row">
                    <button className="adm-btn adm-btn--ghost" onClick={() => setRejecting(false)}>
                      취소
                    </button>
                    <button
                      className="adm-btn adm-btn--danger"
                      disabled={!comment.trim()}
                      onClick={() => run(() => reject(req.id, comment.trim()))}
                    >
                      반려 확정
                    </button>
                  </div>
                </>
              )}
            </div>
          ) : (
            <div className="adm-card">
              <p className="adm-hint">
                책임자 <b>{req.approverName}</b> 님의 결재를 대기 중입니다.
              </p>
            </div>
          ))}

        {/* 승인됨: 배포 대기 */}
        {req.status === 'APPROVED' && (
          <div className="adm-card">
            <p className="adm-card__title">배포 대기 (형상이행 예약됨)</p>
            <p className="adm-hint">
              예약 시각 <b>{formatDateTime(req.scheduledDeployAt)}</b> 에 스케줄러가 TO-BE 를
              라이브에 반영합니다. 그 전까지 사용자에게는 AS-IS 가 노출됩니다.
            </p>
            <div className="adm-row">
              <button className="adm-btn adm-btn--primary" onClick={() => run(() => deployNow(req.id))}>
                지금 즉시 배포 (데모)
              </button>
            </div>
          </div>
        )}
      </div>

      {showSubmit && <SubmitModal onClose={() => setShowSubmit(false)} onSubmit={onSubmitApprover} />}
    </>
  );
}
