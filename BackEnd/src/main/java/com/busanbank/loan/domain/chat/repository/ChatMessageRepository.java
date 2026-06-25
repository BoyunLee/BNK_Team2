package com.busanbank.loan.domain.chat.repository;

import com.busanbank.loan.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /** 세션의 최근 메시지를 최신순으로 조회(서비스에서 역순 정렬해 사용). */
    List<ChatMessage> findBySessionIdOrderByCreatedAtDesc(String sessionId, Pageable pageable);
}
