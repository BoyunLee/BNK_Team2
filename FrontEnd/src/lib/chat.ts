// 챗봇 API — 백엔드 /api/chat 연동 (CHATBOT_SPEC.md 계약).
// 요청:  { sessionId, message }   (sessionId 비면 서버가 발급)
// 응답:  ApiResponse<{ sessionId, answer, referencedProducts: string[], fallback }>
// referencedProducts(상품코드 mkpd_cd)는 프론트에서 상품목록으로 이름/상세링크로 변환한다.

import { apiFetch } from './api';
import { fetchProducts } from './products';

export interface ChatSource {
  productCode: string; // mkpd_cd
  productName: string;
  productId: number | null; // 상세 페이지 링크용 (목록에서 해석)
}

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  sources?: ChatSource[];
}

/** sendChat 결과 — 위젯이 화면 갱신에 사용. */
export interface ChatTurn {
  sessionId: string;
  answer: string;
  sources: ChatSource[];
  fallback: boolean;
}

/** 백엔드 응답 data 형태. */
interface ChatData {
  sessionId: string;
  answer: string;
  referencedProducts: string[];
  fallback: boolean;
}

// 상품코드(mkpd_cd) → { productId, productName } 캐시 (최초 1회만 목록 조회)
let productMapPromise: Promise<Map<string, { productId: number; productName: string }>> | null = null;

function productMap() {
  if (!productMapPromise) {
    productMapPromise = fetchProducts()
      .then((list) => {
        const m = new Map<string, { productId: number; productName: string }>();
        for (const p of list) m.set(p.mkpdCd, { productId: p.productId, productName: p.productName });
        return m;
      })
      .catch(() => new Map<string, { productId: number; productName: string }>());
  }
  return productMapPromise;
}

/**
 * 질문을 보내고 답변을 받는다.
 * @param message  사용자 질문
 * @param sessionId  직전 응답에서 받은 세션ID(첫 호출은 빈 문자열)
 */
export async function sendChat(message: string, sessionId: string): Promise<ChatTurn> {
  const data = await apiFetch<ChatData>('/api/chat', {
    method: 'POST',
    body: JSON.stringify({ sessionId: sessionId || '', message }),
  });

  let sources: ChatSource[] = [];
  if (!data.fallback && data.referencedProducts?.length) {
    const map = await productMap();
    sources = data.referencedProducts.map((code) => {
      const hit = map.get(code);
      return {
        productCode: code,
        productName: hit?.productName ?? code,
        productId: hit?.productId ?? null,
      };
    });
  }

  return {
    sessionId: data.sessionId,
    answer: data.answer,
    sources,
    fallback: data.fallback,
  };
}
