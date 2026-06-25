// 관리자 화면 공용 조각: 뱃지, 날짜포맷, AS-IS/TO-BE diff, 결재상신 모달, 스냅샷 필드 정의.
import { useEffect, useState } from 'react';
import {
  STATUS_LABEL,
  TYPE_LABEL,
  listApprovers,
  type AdminUser,
  type ChangeStatus,
  type ChangeType,
  type ProductSnapshot,
} from '../../lib/admin';

export function StatusBadge({ status }: { status: ChangeStatus }) {
  return <span className={`adm-badge adm-badge--${status}`}>{STATUS_LABEL[status]}</span>;
}

export function TypeBadge({ type }: { type: ChangeType }) {
  return <span className="adm-badge adm-badge--type">{TYPE_LABEL[type]}</span>;
}

export function formatDateTime(iso: string | null): string {
  if (!iso) return '-';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '-';
  const p = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`;
}

/** 스냅샷 표시 순서/라벨 (diff·폼 공용) */
export const SNAPSHOT_FIELDS: { key: keyof ProductSnapshot; label: string; long?: boolean }[] = [
  { key: 'productName', label: '상품명' },
  { key: 'category', label: '카테고리' },
  { key: 'baseRate', label: '기준금리(%)' },
  { key: 'rateMin', label: '최저금리' },
  { key: 'rateMax', label: '최고금리' },
  { key: 'loanPeriod', label: '대출기간' },
  { key: 'loanLimit', label: '대출한도' },
  { key: 'target', label: '대출대상' },
  { key: 'repayment', label: '상환방법' },
  { key: 'catchphrase', label: '캐치프레이즈', long: true },
  { key: 'summary', label: '상품요약', long: true },
];

/** AS-IS vs TO-BE 비교표. 변경된 셀은 강조. */
export function SnapshotDiff({
  asis,
  tobe,
}: {
  asis: ProductSnapshot | null;
  tobe: ProductSnapshot;
}) {
  return (
    <table className="adm-diff">
      <colgroup>
        <col style={{ width: 130 }} />
        <col className="asis" />
        <col className="asis" />
      </colgroup>
      <thead>
        <tr>
          <th></th>
          <th className="adm-diff__head-asis">AS-IS (현재 배포본)</th>
          <th className="adm-diff__head-tobe">TO-BE (변경안)</th>
        </tr>
      </thead>
      <tbody>
        {SNAPSHOT_FIELDS.map(({ key, label }) => {
          const a = asis?.[key] ?? '';
          const b = tobe[key] ?? '';
          const changed = (a || '') !== (b || '');
          return (
            <tr key={key}>
              <th>{label}</th>
              <td className="asis-cell">{asis ? a || '-' : <em>신규</em>}</td>
              <td className={`tobe-cell${changed ? ' changed' : ''}`}>{b || '-'}</td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}

/** 결재상신 모달 — 책임자 선택 후 확정 */
export function SubmitModal({
  onClose,
  onSubmit,
}: {
  onClose: () => void;
  onSubmit: (approver: AdminUser) => void;
}) {
  const [approvers, setApprovers] = useState<AdminUser[]>([]);
  const [picked, setPicked] = useState<number | null>(null);

  useEffect(() => {
    listApprovers().then(setApprovers);
  }, []);

  const chosen = approvers.find((a) => a.id === picked) ?? null;

  return (
    <div className="adm-modal__dim" onClick={onClose}>
      <div className="adm-modal" onClick={(e) => e.stopPropagation()}>
        <h3>결재 상신 — 책임자 선택</h3>
        <div className="adm-pick">
          {approvers.map((a) => (
            <div
              key={a.id}
              className={`adm-pick__item${picked === a.id ? ' active' : ''}`}
              onClick={() => setPicked(a.id)}
            >
              <input type="radio" readOnly checked={picked === a.id} />
              <div>
                <div className="nm">
                  {a.name} <span className="dp">({a.department})</span>
                </div>
                <div className="dp">책임자 · {a.loginId}</div>
              </div>
            </div>
          ))}
          {approvers.length === 0 && <div className="adm-hint">책임자 목록을 불러오는 중…</div>}
        </div>
        <div className="adm-row adm-row--end">
          <button className="adm-btn adm-btn--ghost" onClick={onClose}>
            취소
          </button>
          <button
            className="adm-btn adm-btn--primary"
            disabled={!chosen}
            onClick={() => chosen && onSubmit(chosen)}
          >
            상신
          </button>
        </div>
      </div>
    </div>
  );
}
