package com.busanbank.loan.domain.loan.scheduler;

import com.busanbank.loan.domain.loan.entity.LoanApplication;
import com.busanbank.loan.domain.loan.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanExpireScheduler {

    private final LoanApplicationRepository loanApplicationRepository;

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void expireApplications() {
        List<LoanApplication> expired = loanApplicationRepository
                .findAllByExpireAtBeforeAndStatusCodeNotIn(LocalDateTime.now(), List.of("9", "X", "R"));
        expired.forEach(app -> app.updateStatus("X"));
    }
}
