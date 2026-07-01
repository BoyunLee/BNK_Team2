package com.busanbank.loan.domain.loan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "SUITABILITY_RESPONSE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SuitabilityResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @Column(name = "loan_account_no", nullable = false, length = 30)
    private String loanAccountNo;

    @Column(name = "question_code")
    private String questionCode;

    @Column(name = "question")
    private String question;

    @Column(name = "answer")
    private String answer;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public SuitabilityResponse(String loanAccountNo, String questionCode, String question, String answer) {
        this.loanAccountNo = loanAccountNo;
        this.questionCode = questionCode;
        this.question = question;
        this.answer = answer;
        this.createdAt = LocalDateTime.now();
    }
}
