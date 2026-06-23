// 챗봇 API 이음새. 백엔드(/api/chat) 연결 전에는 목업으로 동작한다.
// .env 에 VITE_CHAT_API 를 지정하면 실제 백엔드를 호출한다.

export interface ChatSource {
  productName: string;
  mkpd_cd: string;
  category?: string;
  sectionTitle?: string;
}

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  sources?: ChatSource[];
}

export interface ChatResponse {
  answer: string;
  sources?: ChatSource[];
}

const CHAT_ENDPOINT = import.meta.env.VITE_CHAT_API ?? '/api/chat';
/** 백엔드 미연결 시 목업. VITE_CHAT_API 지정되면 실제 호출. */
const USE_MOCK = import.meta.env.VITE_CHAT_API == null;

/**
 * 질문을 보내고 답변을 받는다.
 * 백엔드 RAG 가 준비되면 USE_MOCK 이 자동으로 false 가 되고
 * 동일한 시그니처로 /api/chat 을 호출한다(프론트 변경 불필요).
 */
export async function sendChat(
  message: string,
  history: ChatMessage[],
): Promise<ChatResponse> {
  if (USE_MOCK) return mockReply(message);

  const res = await fetch(CHAT_ENDPOINT, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message, history }),
  });
  if (!res.ok) throw new Error(`채팅 API 오류: HTTP ${res.status}`);
  return res.json();
}

/** 백엔드 연결 전 임시 응답. 실제 RAG 답변 형식(answer + sources)을 흉내낸다. */
async function mockReply(message: string): Promise<ChatResponse> {
  await new Promise((r) => setTimeout(r, 600));
  return {
    answer:
      '지금은 챗봇 UI 데모예요. 백엔드(RAG)가 연결되면 부산은행 여신상품 정보를 바탕으로 ' +
      '실제 답변과 근거 출처를 보여드릴게요.\n\n' +
      `· 받은 질문: "${message}"`,
  };
}
