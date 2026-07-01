import { Fragment, type ReactNode } from 'react';

/**
 * 경량 마크다운 렌더러 — LLM 챗봇 응답용.
 * 지원: 굵게, 기울임, 인라인 코드, 링크, 글머리/번호 목록, 제목(#).
 * HTML 문자열이 아닌 React 요소로 생성하므로 별도 sanitize 없이 안전하다.
 */

// 한 줄 내 인라인 서식 파싱
function inline(text: string): ReactNode[] {
  const nodes: ReactNode[] = [];
  const re =
    /(`[^`]+`)|(\*\*[^*]+\*\*)|(\*[^*\n]+\*)|(_[^_\n]+_)|(\[[^\]]+\]\([^)]+\))/g;
  let last = 0;
  let m: RegExpExecArray | null;
  let i = 0;
  while ((m = re.exec(text)) !== null) {
    if (m.index > last) nodes.push(text.slice(last, m.index));
    const tok = m[0];
    const key = `t${i}`;
    if (tok.startsWith('`')) {
      nodes.push(<code key={key}>{tok.slice(1, -1)}</code>);
    } else if (tok.startsWith('**')) {
      nodes.push(<strong key={key}>{tok.slice(2, -2)}</strong>);
    } else if (tok.startsWith('*') || tok.startsWith('_')) {
      nodes.push(<em key={key}>{tok.slice(1, -1)}</em>);
    } else {
      // 링크 [label](url) — http/https만 허용
      const mm = /^\[([^\]]+)\]\(([^)]+)\)$/.exec(tok);
      if (mm && /^https?:\/\//i.test(mm[2])) {
        nodes.push(
          <a key={key} href={mm[2]} target="_blank" rel="noreferrer">
            {mm[1]}
          </a>,
        );
      } else if (mm) {
        nodes.push(mm[1]);
      }
    }
    last = m.index + tok.length;
    i += 1;
  }
  if (last < text.length) nodes.push(text.slice(last));
  return nodes;
}

export function Markdown({ text }: { text: string }) {
  const lines = (text ?? '').replace(/\r\n/g, '\n').split('\n');
  const blocks: ReactNode[] = [];
  let list: { ordered: boolean; items: string[] } | null = null;
  let para: string[] = [];

  const flushList = () => {
    if (!list) return;
    const items = list.items.map((it, idx) => <li key={idx}>{inline(it)}</li>);
    blocks.push(
      list.ordered ? (
        <ol className="md-ol" key={`b${blocks.length}`}>
          {items}
        </ol>
      ) : (
        <ul className="md-ul" key={`b${blocks.length}`}>
          {items}
        </ul>
      ),
    );
    list = null;
  };
  const flushPara = () => {
    if (para.length === 0) return;
    const buf = para;
    blocks.push(
      <p className="md-p" key={`b${blocks.length}`}>
        {buf.map((l, idx) => (
          <Fragment key={idx}>
            {idx > 0 && <br />}
            {inline(l)}
          </Fragment>
        ))}
      </p>,
    );
    para = [];
  };

  for (const raw of lines) {
    const line = raw.replace(/\s+$/, '');
    const ul = /^\s*[-*]\s+(.*)$/.exec(line);
    const ol = /^\s*\d+\.\s+(.*)$/.exec(line);
    const h = /^(#{1,6})\s+(.*)$/.exec(line);

    if (ul) {
      flushPara();
      if (!list || list.ordered) {
        flushList();
        list = { ordered: false, items: [] };
      }
      list.items.push(ul[1]);
      continue;
    }
    if (ol) {
      flushPara();
      if (!list || !list.ordered) {
        flushList();
        list = { ordered: true, items: [] };
      }
      list.items.push(ol[1]);
      continue;
    }
    flushList();
    if (h) {
      flushPara();
      blocks.push(
        <p className="md-h" key={`b${blocks.length}`}>
          {inline(h[2])}
        </p>,
      );
      continue;
    }
    if (line.trim() === '') {
      flushPara(); // 빈 줄 → 문단 구분
      continue;
    }
    para.push(line);
  }
  flushList();
  flushPara();

  return <>{blocks}</>;
}
