import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import '../../styles/shell.css';
import './apply.css';

/** Fisher-Yates 셔플 */
function shuffle<T>(arr: T[]): T[] {
  const a = [...arr];
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

/**
 * 간편비밀번호(6자리) 로그인. 캡쳐가 없어 표준 키패드로 구성.
 * 데모이므로 6자리가 채워지면 진단결과로 이동(검증 없음).
 */
export function PinAuthPage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const [pin, setPin] = useState('');
  // 보안 키패드 — 진입 시 숫자 위치 무작위 배치(빈칸 1개 + ⌫ 우하단 고정)
  const keys = useMemo(() => {
    const cells = shuffle([...'0123456789'.split(''), '']);
    return [...cells, '⌫'];
  }, []);
  const productCd = mkpdCd ?? '';
  // 인증 후 이동지(없으면 기본: 적합성 진단결과)
  const next =
    params.get('next') ?? `/apply/${encodeURIComponent(productCd)}/result`;

  useEffect(() => {
    if (pin.length === 6) {
      const t = setTimeout(() => navigate(next), 250);
      return () => clearTimeout(t);
    }
  }, [pin, navigate, next]);

  function press(k: string) {
    if (k === '⌫') {
      setPin((p) => p.slice(0, -1));
    } else if (k !== '') {
      setPin((p) => (p.length < 6 ? p + k : p));
    }
  }

  return (
    <div className="app-shell">
      <header className="flow-head">
        <span className="flow-head__title">간편비밀번호</span>
        <button
          type="button"
          className="flow-head__close"
          onClick={() => navigate(-1)}
        >
          닫기
        </button>
      </header>

      <div className="pin">
        <h1 className="pin__title">간편비밀번호를 입력해주세요</h1>
        <p className="pin__sub">6자리 숫자 비밀번호</p>

        <div className="pin__dots" aria-label={`${pin.length}자리 입력됨`}>
          {Array.from({ length: 6 }).map((_, i) => (
            <span
              key={i}
              className={`pin__dot${i < pin.length ? ' filled' : ''}`}
            />
          ))}
        </div>

        <div className="keypad">
          {keys.map((k, i) => (
            <button
              key={i}
              type="button"
              className={`key${k === '' ? ' key--empty' : ''}`}
              onClick={() => press(k)}
              disabled={k === ''}
              aria-label={k === '⌫' ? '지우기' : k}
            >
              {k}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
