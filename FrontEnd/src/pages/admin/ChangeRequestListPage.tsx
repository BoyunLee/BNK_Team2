import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  listChangeRequests,
  type ChangeStatus,
  type ProductChangeRequest,
} from '../../lib/admin';
import { StatusBadge, TypeBadge, formatDateTime } from './AdminBits';

const TABS: { key: ChangeStatus | 'ALL'; label: string }[] = [
  { key: 'ALL', label: '전체' },
  { key: 'DRAFT', label: '작성중' },
  { key: 'PENDING', label: '결재대기' },
  { key: 'APPROVED', label: '배포예약' },
  { key: 'REJECTED', label: '반려' },
  { key: 'DEPLOYED', label: '배포완료' },
];

export function ChangeRequestListPage() {
  const navigate = useNavigate();
  const [all, setAll] = useState<ProductChangeRequest[]>([]);
  const [tab, setTab] = useState<ChangeStatus | 'ALL'>('ALL');

  useEffect(() => {
    listChangeRequests().then(setAll);
  }, []);

  const counts = useMemo(() => {
    const c: Record<string, number> = {};
    for (const r of all) c[r.status] = (c[r.status] ?? 0) + 1;
    return c;
  }, [all]);

  const rows = tab === 'ALL' ? all : all.filter((r) => r.status === tab);

  return (
    <>
      <div className="adm-topbar">
        <h1>상품 변경 신청</h1>
        <div className="adm-spacer" />
        <button className="adm-btn adm-btn--primary" onClick={() => navigate('/admin/requests/new')}>
          + 신규 신청서
        </button>
      </div>
      <div className="adm-content">
        <div className="adm-kpis">
          <div className="adm-kpi">
            <div className="label">결재대기</div>
            <div className="value">{counts.PENDING ?? 0}</div>
          </div>
          <div className="adm-kpi">
            <div className="label">배포예약</div>
            <div className="value">{counts.APPROVED ?? 0}</div>
          </div>
          <div className="adm-kpi">
            <div className="label">반려</div>
            <div className="value">{counts.REJECTED ?? 0}</div>
          </div>
          <div className="adm-kpi">
            <div className="label">배포완료</div>
            <div className="value">{counts.DEPLOYED ?? 0}</div>
          </div>
        </div>

        <div className="adm-tabs">
          {TABS.map((t) => (
            <button
              key={t.key}
              className={`adm-tab${tab === t.key ? ' active' : ''}`}
              onClick={() => setTab(t.key)}
            >
              {t.label}
              {t.key !== 'ALL' && counts[t.key] ? ` (${counts[t.key]})` : ''}
            </button>
          ))}
        </div>

        <div className="adm-card" style={{ padding: 0, overflow: 'hidden' }}>
          <table className="adm-table">
            <thead>
              <tr>
                <th style={{ width: 80 }}>번호</th>
                <th style={{ width: 90 }}>유형</th>
                <th>제목</th>
                <th style={{ width: 100 }}>담당자</th>
                <th style={{ width: 100 }}>책임자</th>
                <th style={{ width: 150 }}>상신일</th>
                <th style={{ width: 100 }}>상태</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.id} onClick={() => navigate(`/admin/requests/${r.id}`)}>
                  <td className="num">{r.id}</td>
                  <td>
                    <TypeBadge type={r.changeType} />
                  </td>
                  <td>{r.title}</td>
                  <td>{r.drafterName}</td>
                  <td>{r.approverName ?? '-'}</td>
                  <td className="num">{formatDateTime(r.submittedAt)}</td>
                  <td>
                    <StatusBadge status={r.status} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {rows.length === 0 && <div className="adm-empty">신청서가 없습니다.</div>}
        </div>
      </div>
    </>
  );
}
