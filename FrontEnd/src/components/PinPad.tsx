import { useEffect, useMemo, useState } from 'react';
import './PinPad.css';

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
 * 보안 키패드 PIN 입력 오버레이.
 * - 숫자 위치를 매번 무작위로 배치(보안 키패드)
 * - length 자리 입력 완료 시 onComplete 호출
 */
export function PinPad({
  title,
  sub,
  length,
  onComplete,
  onClose,
}: {
  title: string;
  sub?: string;
  length: number;
  onComplete: (value: string) => void;
  onClose: () => void;
}) {
  const [pin, setPin] = useState('');

  // 마운트(=열릴 때)마다 숫자 위치 무작위. 마지막 칸은 지우기(⌫) 고정.
  const keys = useMemo(() => {
    const cells = shuffle([...'0123456789'.split(''), '']); // 11칸(숫자10 + 빈칸)
    return [...cells, '⌫'];
  }, []);

  useEffect(() => {
    if (pin.length === length) {
      const t = setTimeout(() => {
        onComplete(pin);
        onClose();
      }, 120);
      return () => clearTimeout(t);
    }
  }, [pin, length, onComplete, onClose]);

  function press(k: string) {
    if (k === '⌫') setPin((p) => p.slice(0, -1));
    else if (k !== '') setPin((p) => (p.length < length ? p + k : p));
  }

  return (
    <div className="pinpad" role="dialog" aria-label={title}>
      <div className="pinpad__dim" onClick={onClose} />
      <div className="pinpad__panel">
        <div className="pinpad__head">
          <h3 className="pinpad__title">{title}</h3>
          <button
            type="button"
            className="pinpad__close"
            onClick={onClose}
            aria-label="닫기"
          >
            ×
          </button>
        </div>
        {sub && <p className="pinpad__sub">{sub}</p>}

        <div className="pinpad__dots" aria-label={`${pin.length}자리 입력됨`}>
          {Array.from({ length }).map((_, i) => (
            <span
              key={i}
              className={`pinpad__dot${i < pin.length ? ' filled' : ''}`}
            />
          ))}
        </div>

        <div className="pinpad__keys">
          {keys.map((k, i) => (
            <button
              key={i}
              type="button"
              className={`pinpad__key${k === '' ? ' pinpad__key--empty' : ''}`}
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
