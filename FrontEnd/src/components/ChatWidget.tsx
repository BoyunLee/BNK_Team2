import { useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { sendChat, type ChatMessage } from '../lib/chat';
import '../styles/shell.css';
import './ChatWidget.css';

const GREETING: ChatMessage = {
  role: 'assistant',
  content:
    '안녕하세요! 부산은행 여신상품 도우미예요. 궁금한 점을 물어보세요.\n예) "전세자금 대출 뭐가 있어?", "사회초년생 신용대출 추천해줘"',
};

/** 전역 플로팅 챗봇. 모든 화면 위에 떠 있는 FAB → 패널. */
export function ChatWidget() {
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([GREETING]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const endRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (open) endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading, open]);

  async function handleSend() {
    const text = input.trim();
    if (!text || loading) return;
    const next: ChatMessage[] = [...messages, { role: 'user', content: text }];
    setMessages(next);
    setInput('');
    setLoading(true);
    try {
      const res = await sendChat(text, next);
      setMessages((m) => [
        ...m,
        { role: 'assistant', content: res.answer, sources: res.sources },
      ]);
    } catch {
      setMessages((m) => [
        ...m,
        {
          role: 'assistant',
          content:
            '죄송해요, 답변 중 오류가 발생했어요. 잠시 후 다시 시도해 주세요.',
        },
      ]);
    } finally {
      setLoading(false);
    }
  }

  function onKeyDown(e: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  }

  return (
    <div className="chat-root" aria-live="polite">
      <div className="chat-root__col">
        {!open && (
          <button
            className="chat-fab"
            type="button"
            onClick={() => setOpen(true)}
            aria-label="상품 도우미 열기"
          >
            <svg width="26" height="26" viewBox="0 0 24 24" aria-hidden="true">
              <path
                fill="currentColor"
                d="M12 3c5 0 9 3.36 9 7.5S17 18 12 18c-.96 0-1.89-.12-2.75-.34L4 19l1.1-3.3C3.78 14.4 3 12.55 3 10.5 3 6.36 7 3 12 3Z"
              />
            </svg>
          </button>
        )}

        {open && (
          <section className="chat-panel" role="dialog" aria-label="상품 도우미">
            <header className="chat-head">
              <div className="chat-head__title">
                <span className="chat-head__dot" />
                상품 도우미
              </div>
              <button
                className="chat-head__close"
                type="button"
                onClick={() => setOpen(false)}
                aria-label="닫기"
              >
                ×
              </button>
            </header>

            <div className="chat-body">
              {messages.map((m, i) => (
                <div key={i} className={`msg msg--${m.role}`}>
                  <div className="msg__bubble">{m.content}</div>
                  {m.sources && m.sources.length > 0 && (
                    <div className="msg__sources">
                      {m.sources.map((s) => (
                        <Link
                          key={s.mkpd_cd}
                          className="msg__source"
                          to={`/product/${encodeURIComponent(s.mkpd_cd)}`}
                          onClick={() => setOpen(false)}
                        >
                          {s.productName}
                          {s.sectionTitle ? ` · ${s.sectionTitle}` : ''}
                        </Link>
                      ))}
                    </div>
                  )}
                </div>
              ))}
              {loading && (
                <div className="msg msg--assistant">
                  <div className="msg__bubble msg__bubble--typing">
                    <span />
                    <span />
                    <span />
                  </div>
                </div>
              )}
              <div ref={endRef} />
            </div>

            <div className="chat-input">
              <textarea
                className="chat-input__field"
                rows={1}
                placeholder="메시지를 입력하세요"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={onKeyDown}
                aria-label="메시지 입력"
              />
              <button
                className="chat-input__send"
                type="button"
                onClick={handleSend}
                disabled={!input.trim() || loading}
                aria-label="보내기"
              >
                ↑
              </button>
            </div>

            <p className="chat-disclaimer">
              AI 안내입니다. 정확한 내용은 약관·영업점에서 확인하세요.
            </p>
          </section>
        )}
      </div>
    </div>
  );
}
