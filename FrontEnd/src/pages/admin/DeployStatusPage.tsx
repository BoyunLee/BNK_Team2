import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { listChangeRequests, type ProductChangeRequest } from '../../lib/admin';
import { TypeBadge, formatDateTime } from './AdminBits';

/** 배포 예약/이력 — 형상이행 스케줄러 현황. 수동 실행(데모) 제공. */
export function DeployStatusPage() {
  const navigate = useNavigate();
  const [scheduled, setScheduled] = useState<ProductChangeRequest[]>([]);
  const [done, setDone] = useState<ProductChangeRequest[]>([]);
  const [msg, setMsg] = useState('');

  const reload = useCallback(() => {
    listChangeRequests({ status: 'APPROVED' }).then((l) =>
      setScheduled(
        [...l].sort(
          (a, b) =>
            new Date(a.scheduledDeployAt ?? 0).getTime() -
            new Date(b.scheduledDeployAt ?? 0).getTime(),
        ),
      ),
    );
    listChangeRequests({ status: 'DEPLOYED' }).then(setDone);
  }, []);

  useEffect(() => {
    reload();
  }, [reload]);

  const refresh = () => {
    setMsg('최신 배포 현황으로 새로고침했습니다.');
    reload();
  };

  return (
    <>
      <div className="adm-topbar">
        <h1>배포 예약 / 이력</h1>
        <div className="adm-spacer" />
        <button className="adm-btn adm-btn--primary" onClick={refresh}>
          새로고침
        </button>
      </div>
      <div className="adm-content">
        <p className="adm-hint" style={{ marginTop: 0 }}>
          승인된 신청서는 예약시각에 스케줄러가 자동으로 라이브(AS-IS)에 반영합니다. 반영 전까지는
          기존 배포본이 유지됩니다.
        </p>
        {msg && (
          <div className="adm-card" style={{ background: '#f0f7ff', borderColor: '#cfe3ff' }}>
            {msg}
          </div>
        )}

        <div className="adm-card">
          <p className="adm-card__title">배포 예약 (대기)</p>
          <table className="adm-table">
            <thead>
              <tr>
                <th style={{ width: 80 }}>번호</th>
                <th style={{ width: 90 }}>유형</th>
                <th>제목</th>
                <th style={{ width: 170 }}>예약시각</th>
              </tr>
            </thead>
            <tbody>
              {scheduled.map((r) => (
                <tr key={r.id} onClick={() => navigate(`/admin/requests/${r.id}`)}>
                  <td className="num">{r.id}</td>
                  <td>
                    <TypeBadge type={r.changeType} />
                  </td>
                  <td>{r.title}</td>
                  <td className="num">{formatDateTime(r.scheduledDeployAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {scheduled.length === 0 && <div className="adm-empty">예약된 배포가 없습니다.</div>}
        </div>

        <div className="adm-card">
          <p className="adm-card__title">배포 이력</p>
          <table className="adm-table">
            <thead>
              <tr>
                <th style={{ width: 80 }}>번호</th>
                <th style={{ width: 90 }}>유형</th>
                <th>제목</th>
                <th style={{ width: 170 }}>배포완료</th>
              </tr>
            </thead>
            <tbody>
              {done.map((r) => (
                <tr key={r.id} onClick={() => navigate(`/admin/requests/${r.id}`)}>
                  <td className="num">{r.id}</td>
                  <td>
                    <TypeBadge type={r.changeType} />
                  </td>
                  <td>{r.title}</td>
                  <td className="num">{formatDateTime(r.deployedAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {done.length === 0 && <div className="adm-empty">배포 이력이 없습니다.</div>}
        </div>
      </div>
    </>
  );
}
