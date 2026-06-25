import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAdminAuth } from '../../auth/AdminAuthContext';
import {
  createDraft,
  emptySnapshot,
  getChangeRequest,
  listLiveProducts,
  updateDraft,
  type ChangeType,
  type LiveProduct,
  type ProductSnapshot,
} from '../../lib/admin';
import { SNAPSHOT_FIELDS } from './AdminBits';

const CATEGORIES = ['신용', '담보', '전세', '주택', '사업자', '기타'];

export function ProductFormPage() {
  const { id } = useParams();
  const editing = id != null;
  const navigate = useNavigate();
  const { admin } = useAdminAuth();

  const [changeType, setChangeType] = useState<ChangeType>('CREATE');
  const [productId, setProductId] = useState<number | null>(null);
  const [title, setTitle] = useState('');
  const [snap, setSnap] = useState<ProductSnapshot>(emptySnapshot());
  const [live, setLive] = useState<LiveProduct[]>([]);
  const [err, setErr] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    listLiveProducts().then(setLive);
  }, []);

  // 수정 모드: 기존 신청서 로드
  useEffect(() => {
    if (!editing) return;
    getChangeRequest(Number(id)).then((r) => {
      if (!r) return;
      setChangeType(r.changeType);
      setProductId(r.productId);
      setTitle(r.title);
      setSnap(r.tobe);
    });
  }, [editing, id]);

  // 신규+변경/판매중지: 대상 상품 선택 시 AS-IS 로 프리필
  const pickTarget = (pid: number) => {
    setProductId(pid);
    const p = live.find((x) => x.productId === pid);
    if (p) setSnap({ ...p.snapshot });
  };

  const onType = (t: ChangeType) => {
    setChangeType(t);
    setProductId(null);
    if (t === 'CREATE') setSnap(emptySnapshot());
  };

  const readOnlyFields = changeType === 'DISCONTINUE';
  const needTarget = changeType !== 'CREATE';

  const canSave = useMemo(() => {
    if (!title.trim()) return false;
    if (needTarget && productId == null) return false;
    if (!readOnlyFields && !snap.productName.trim()) return false;
    return true;
  }, [title, needTarget, productId, readOnlyFields, snap.productName]);

  const setField = (k: keyof ProductSnapshot, v: string) =>
    setSnap((s) => ({ ...s, [k]: v }));

  const save = async () => {
    if (!admin || !canSave) return;
    setErr('');
    setSaving(true);
    try {
      if (editing) {
        const r = await updateDraft(Number(id), { title, tobe: snap });
        navigate(`/admin/requests/${r.id}`);
      } else {
        const r = await createDraft({
          changeType,
          productId: needTarget ? productId : null,
          title,
          tobe: snap,
        });
        navigate(`/admin/requests/${r.id}`);
      }
    } catch (e) {
      setErr(e instanceof Error ? e.message : '저장에 실패했습니다.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      <div className="adm-topbar">
        <button className="adm-btn adm-btn--ghost adm-btn--sm" onClick={() => navigate(-1)}>
          ← 뒤로
        </button>
        <h1>{editing ? `신청서 수정 #${id}` : '신규 상품 변경 신청서'}</h1>
      </div>
      <div className="adm-content">
        <div className="adm-card">
          <p className="adm-card__title">신청 정보</p>
          <div className="adm-grid2">
            <div className="adm-field">
              <label>변경 유형</label>
              <select
                value={changeType}
                disabled={editing}
                onChange={(e) => onType(e.target.value as ChangeType)}
              >
                <option value="CREATE">신규등록</option>
                <option value="UPDATE">변경</option>
                <option value="DISCONTINUE">판매중지</option>
              </select>
            </div>
            {needTarget && (
              <div className="adm-field">
                <label>대상 상품 (AS-IS)</label>
                <select
                  value={productId ?? ''}
                  disabled={editing}
                  onChange={(e) => pickTarget(Number(e.target.value))}
                >
                  <option value="" disabled>
                    상품 선택…
                  </option>
                  {live
                    .filter((p) => p.status === 'SALE')
                    .map((p) => (
                      <option key={p.productId} value={p.productId}>
                        {p.snapshot.productName} (v{p.version})
                      </option>
                    ))}
                </select>
              </div>
            )}
          </div>
          <div className="adm-field">
            <label>결재 제목</label>
            <input
              value={title}
              placeholder={
                changeType === 'DISCONTINUE'
                  ? '예) ONE스피드 전세대출 판매중지 (사유 포함)'
                  : '예) BNK 357 직장인대출 금리 인하 변경 건'
              }
              onChange={(e) => setTitle(e.target.value)}
            />
          </div>
        </div>

        <div className="adm-card">
          <p className="adm-card__title">
            {changeType === 'DISCONTINUE' ? '판매중지 대상 (TO-BE = 현재본 그대로, 배포 시 판매중지)' : 'TO-BE 상품 내용'}
          </p>
          {needTarget && productId == null ? (
            <div className="adm-hint">먼저 대상 상품을 선택하세요.</div>
          ) : (
            <div className="adm-grid2">
              {SNAPSHOT_FIELDS.map(({ key, label, long }) => (
                <div className="adm-field" key={key} style={long ? { gridColumn: '1 / -1' } : undefined}>
                  <label>{label}</label>
                  {key === 'category' ? (
                    <select
                      value={snap.category}
                      disabled={readOnlyFields}
                      onChange={(e) => setField('category', e.target.value)}
                    >
                      {CATEGORIES.map((c) => (
                        <option key={c} value={c}>
                          {c}
                        </option>
                      ))}
                    </select>
                  ) : long ? (
                    <textarea
                      value={snap[key]}
                      disabled={readOnlyFields}
                      onChange={(e) => setField(key, e.target.value)}
                    />
                  ) : (
                    <input
                      value={snap[key]}
                      disabled={readOnlyFields}
                      onChange={(e) => setField(key, e.target.value)}
                    />
                  )}
                </div>
              ))}
            </div>
          )}
          {err && <div className="adm-err">{err}</div>}
          <div className="adm-row adm-row--end" style={{ marginTop: 8 }}>
            <button className="adm-btn adm-btn--ghost" onClick={() => navigate(-1)}>
              취소
            </button>
            <button className="adm-btn adm-btn--primary" disabled={!canSave || saving} onClick={save}>
              {saving ? '저장 중…' : '작성 저장 (DRAFT)'}
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
