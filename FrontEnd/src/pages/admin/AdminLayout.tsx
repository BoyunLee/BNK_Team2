import { useEffect, useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAdminAuth } from '../../auth/AdminAuthContext';
import { listChangeRequests } from '../../lib/admin';

/** 사이드바 + 상단바 + 본문(Outlet) */
export function AdminLayout() {
  const { admin, isApprover, signOut } = useAdminAuth();
  const navigate = useNavigate();
  const [inboxCount, setInboxCount] = useState(0);

  // 결재함 대기 건수(책임자 본인 앞으로 온 PENDING)
  useEffect(() => {
    if (!isApprover || !admin) return;
    let on = true;
    const load = () =>
      listChangeRequests({ status: 'PENDING', approverId: admin.id }).then(
        (l) => on && setInboxCount(l.length),
      );
    load();
    const t = setInterval(load, 5000);
    return () => {
      on = false;
      clearInterval(t);
    };
  }, [isApprover, admin]);

  const handleSignOut = () => {
    signOut();
    navigate('/admin/login', { replace: true });
  };

  return (
    <div className="adm-root">
      <aside className="adm-side">
        <div className="adm-side__brand">
          BNK <b>여신</b> 관리자
        </div>
        <nav className="adm-nav">
          <div className="adm-nav__sec">개요</div>
          <NavLink to="/admin/dashboard">대시보드</NavLink>
          <div className="adm-nav__sec">상품 결재</div>
          <NavLink to="/admin/requests" end={false}>
            상품 변경 신청
          </NavLink>
          <NavLink to="/admin/requests/new">신규 신청서 작성</NavLink>
          {isApprover && (
            <NavLink to="/admin/inbox">
              결재함
              {inboxCount > 0 && <span className="badge">{inboxCount}</span>}
            </NavLink>
          )}
          <div className="adm-nav__sec">배포</div>
          <NavLink to="/admin/deploy">배포 예약 / 이력</NavLink>
          <NavLink to="/admin/products">판매 상품 (AS-IS)</NavLink>
        </nav>
        <div className="adm-side__foot">
          <div className="name">{admin?.name}</div>
          <div className="role">
            {admin?.role === 'APPROVER' ? '책임자' : '담당자'} · {admin?.department}
          </div>
          <button onClick={handleSignOut}>로그아웃</button>
        </div>
      </aside>
      <main className="adm-main">
        <Outlet />
      </main>
    </div>
  );
}
