import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAdminAuth } from '../../auth/AdminAuthContext';
import { listChangeRequests, type ProductChangeRequest } from '../../lib/admin';
import { TypeBadge, formatDateTime } from './AdminBits';

/** 결재함 — 본인(책임자) 앞으로 상신된 결재대기(PENDING) 목록. */
export function ApprovalInboxPage() {
  const { admin } = useAdminAuth();
  const navigate = useNavigate();
  const [rows, setRows] = useState<ProductChangeRequest[]>([]);

  useEffect(() => {
    if (!admin) return;
    listChangeRequests({ status: 'PENDING', approverId: admin.id }).then(setRows);
  }, [admin]);

  return (
    <>
      <div className="adm-topbar">
        <h1>결재함</h1>
      </div>
      <div className="adm-content">
        <div className="adm-card" style={{ padding: 0, overflow: 'hidden' }}>
          <table className="adm-table">
            <thead>
              <tr>
                <th style={{ width: 80 }}>번호</th>
                <th style={{ width: 90 }}>유형</th>
                <th>제목</th>
                <th style={{ width: 100 }}>담당자</th>
                <th style={{ width: 160 }}>상신일</th>
                <th style={{ width: 100 }}></th>
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
                  <td className="num">{formatDateTime(r.submittedAt)}</td>
                  <td>
                    <button
                      className="adm-btn adm-btn--primary adm-btn--sm"
                      onClick={(e) => {
                        e.stopPropagation();
                        navigate(`/admin/requests/${r.id}`);
                      }}
                    >
                      결재
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {rows.length === 0 && <div className="adm-empty">결재 대기 중인 건이 없습니다.</div>}
        </div>
      </div>
    </>
  );
}
