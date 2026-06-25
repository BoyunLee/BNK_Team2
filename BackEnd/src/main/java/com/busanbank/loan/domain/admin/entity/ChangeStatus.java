package com.busanbank.loan.domain.admin.entity;

/** 변경 신청서(결재문서) 상태머신. */
public enum ChangeStatus {
    DRAFT,      // 작성중
    PENDING,    // 결재대기(상신됨)
    APPROVED,   // 승인됨(배포예약)
    REJECTED,   // 반려
    DEPLOYED,   // 형상이행 완료(라이브 반영)
    CANCELLED   // 취소
}
