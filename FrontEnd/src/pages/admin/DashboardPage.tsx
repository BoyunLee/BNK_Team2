import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  listChangeRequests,
  listLiveProducts,
  STATUS_LABEL,
  TYPE_LABEL,
  type ChangeStatus,
  type ChangeType,
  type LiveProduct,
  type ProductChangeRequest,
} from '../../lib/admin';
import { StatusBadge, TypeBadge, formatDateTime } from './AdminBits';

interface BarItem {
  label: string;
  value: number;
  color?: string;
}

/** 의존성 없는 CSS 막대 차트. */
function Bars({ items }: { items: BarItem[] }) {
  const max = Math.max(1, ...items.map((i) => i.value));
  return (
    <div className="adm-bars">
      {items.map((i) => (
        <div key={i.label}>
          <div className="adm-bar__top">
            <span className="adm-bar__label">{i.label}</span>
            <span className="adm-bar__val">{i.value}</span>
          </div>
          <div className="adm-bar__track">
            <div
              className="adm-bar__fill"
              style={{ width: `${(i.value / max) * 100}%`, background: i.color }}
            />
          </div>
        </div>
      ))}
    </div>
  );
}

const STATUS_COLOR: Record<ChangeStatus, string> = {
  DRAFT: '#9aa3af',
  PENDING: '#e6920a',
  APPROVED: '#1d4ed8',
  REJECTED: '#c01724',
  DEPLOYED: '#1a7f37',
  CANCELLED: '#b6bcc4',
};

const TYPE_COLOR: Record<ChangeType, string> = {
  CREATE: '#1a7f37',
  UPDATE: '#1d4ed8',
  DISCONTINUE: '#e6920a',
};

const STATUS_ORDER: ChangeStatus[] = [
  'DRAFT',
  'PENDING',
  'APPROVED',
  'REJECTED',
  'DEPLOYED',
  'CANCELLED',
];

function isToday(iso: string | null): boolean {
  if (!iso) return false;
  const d = new Date(iso);
  const n = new Date();
  return (
    d.getFullYear() === n.getFullYear() &&
    d.getMonth() === n.getMonth() &&
    d.getDate() === n.getDate()
  );
}

export function DashboardPage() {
  const navigate = useNavigate();
  const [requests, setRequests] = useState<ProductChangeRequest[]>([]);
  const [products, setProducts] = useState<LiveProduct[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([listChangeRequests(), listLiveProducts()])
      .then(([r, p]) => {
        setRequests(r);
        setProducts(p);
      })
      .finally(() => setLoading(false));
  }, []);

  const stats = useMemo(() => {
    const status: Record<string, number> = {};
    const type: Record<string, number> = {};
    const approverPending: Record<string, number> = {};
    for (const r of requests) {
      status[r.status] = (status[r.status] ?? 0) + 1;
      type[r.changeType] = (type[r.changeType] ?? 0) + 1;
      if (r.status === 'PENDING' && r.approverName) {
        approverPending[r.approverName] = (approverPending[r.approverName] ?? 0) + 1;
      }
    }

    const category: Record<string, number> = {};
    let saleCount = 0;
    for (const p of products) {
      if (p.status === 'SALE') saleCount += 1;
      const c = p.snapshot.category || '기타';
      category[c] = (category[c] ?? 0) + 1;
    }

    const decided = (status.DEPLOYED ?? 0) + (status.REJECTED ?? 0);
    const rejectRate = decided > 0 ? Math.round(((status.REJECTED ?? 0) / decided) * 100) : 0;
    const inProgress = (status.DRAFT ?? 0) + (status.PENDING ?? 0) + (status.APPROVED ?? 0);
    const deployedToday = requests.filter((r) => r.status === 'DEPLOYED' && isToday(r.deployedAt)).length;

    const recentDeployed = [...requests]
      .filter((r) => r.status === 'DEPLOYED')
      .sort((a, b) => (b.deployedAt ?? '').localeCompare(a.deployedAt ?? ''))
      .slice(0, 5);
    const recentRequests = [...requests].sort((a, b) => b.id - a.id).slice(0, 5);

    return {
      status,
      type,
      approverPending,
      category,
      saleCount,
      rejectRate,
      inProgress,
      deployedToday,
      recentDeployed,
      recentRequests,
    };
  }, [requests, products]);

  const statusBars: BarItem[] = STATUS_ORDER.filter((s) => (stats.status[s] ?? 0) > 0).map((s) => ({
    label: STATUS_LABEL[s],
    value: stats.status[s] ?? 0,
    color: STATUS_COLOR[s],
  }));

  const typeBars: BarItem[] = (Object.keys(stats.type) as ChangeType[]).map((t) => ({
    label: TYPE_LABEL[t],
    value: stats.type[t],
    color: TYPE_COLOR[t],
  }));

  const categoryBars: BarItem[] = Object.entries(stats.category)
    .sort((a, b) => b[1] - a[1])
    .map(([label, value]) => ({ label, value }));

  const approverBars: BarItem[] = Object.entries(stats.approverPending)
    .sort((a, b) => b[1] - a[1])
    .map(([label, value]) => ({ label, value, color: '#e6920a' }));

  return (
    <>
      <div className="adm-topbar">
        <h1>대시보드</h1>
      </div>
      <div className="adm-content">
        {loading ? (
          <div className="adm-empty">불러오는 중…</div>
        ) : (
          <>
            <div className="adm-kpis">
              <div className="adm-kpi">
                <div className="label">판매중 상품</div>
                <div className="value">{stats.saleCount}</div>
                <div className="delta">전체 {products.length}개</div>
              </div>
              <div className="adm-kpi">
                <div className="label">진행중 결재</div>
                <div className="value">{stats.inProgress}</div>
                <div className="delta">작성중·대기·예약 합계</div>
              </div>
              <div className="adm-kpi">
                <div className="label">결재 대기</div>
                <div className="value accent">{stats.status.PENDING ?? 0}</div>
                <div className="delta">책임자 승인 대기</div>
              </div>
              <div className="adm-kpi">
                <div className="label">오늘 배포</div>
                <div className="value">{stats.deployedToday}</div>
                <div className="delta">반려율 {stats.rejectRate}%</div>
              </div>
            </div>

            <div className="adm-dash-grid">
              <div className="adm-card">
                <p className="adm-card__title">신청 상태 분포</p>
                {statusBars.length ? <Bars items={statusBars} /> : <div className="adm-empty">신청 내역이 없습니다.</div>}
              </div>

              <div className="adm-card">
                <p className="adm-card__title">변경 유형 분포</p>
                {typeBars.length ? <Bars items={typeBars} /> : <div className="adm-empty">신청 내역이 없습니다.</div>}
              </div>

              <div className="adm-card">
                <p className="adm-card__title">책임자별 결재 대기</p>
                {approverBars.length ? <Bars items={approverBars} /> : <div className="adm-empty">대기 중인 결재가 없습니다.</div>}
              </div>

              <div className="adm-card">
                <p className="adm-card__title">상품 카테고리 분포</p>
                {categoryBars.length ? <Bars items={categoryBars} /> : <div className="adm-empty">상품이 없습니다.</div>}
              </div>

              <div className="adm-card">
                <p className="adm-card__title">최근 배포</p>
                {stats.recentDeployed.length ? (
                  <div className="adm-mini">
                    {stats.recentDeployed.map((r) => (
                      <div key={r.id} className="adm-mini__item" onClick={() => navigate(`/admin/requests/${r.id}`)}>
                        <TypeBadge type={r.changeType} />
                        <span className="adm-mini__title">{r.title}</span>
                        <span className="adm-mini__time">{formatDateTime(r.deployedAt)}</span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="adm-empty">배포 이력이 없습니다.</div>
                )}
              </div>

              <div className="adm-card">
                <p className="adm-card__title">최근 신청</p>
                {stats.recentRequests.length ? (
                  <div className="adm-mini">
                    {stats.recentRequests.map((r) => (
                      <div key={r.id} className="adm-mini__item" onClick={() => navigate(`/admin/requests/${r.id}`)}>
                        <StatusBadge status={r.status} />
                        <span className="adm-mini__title">{r.title}</span>
                        <span className="adm-mini__time">{formatDateTime(r.createdAt)}</span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="adm-empty">신청 내역이 없습니다.</div>
                )}
              </div>
            </div>
          </>
        )}
      </div>
    </>
  );
}
