package com.busanbank.loan.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 챗봇 대화 이력 한 건(메시지 1개).
 * 멀티턴 컨텍스트 복원 및 컴플라이언스 로그(분쟁 대응)용. (CHATBOT_SPEC.md 6·8장)
 *
 * role: USER / ASSISTANT
 */
@Entity
@Table(name = "CHAT_MESSAGE", indexes = @Index(name = "idx_chat_session", columnList = "session_id, created_at"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long chatMessageId;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    /** 로그인 고객인 경우에만 채워짐(비로그인 상담 허용). */
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 응답 생성에 참고한 상품 코드 목록(쉼표 구분). USER 메시지는 null. */
    @Column(name = "referenced_products", length = 500)
    private String referencedProducts;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(String sessionId, Long customerId, String role, String content,
                       String referencedProducts) {
        this.sessionId = sessionId;
        this.customerId = customerId;
        this.role = role;
        this.content = content;
        this.referencedProducts = referencedProducts;
        this.createdAt = LocalDateTime.now();
    }

    public static final String ROLE_USER = "USER";
    public static final String ROLE_ASSISTANT = "ASSISTANT";

    public boolean isUser() {
        return ROLE_USER.equals(role);
    }
}
