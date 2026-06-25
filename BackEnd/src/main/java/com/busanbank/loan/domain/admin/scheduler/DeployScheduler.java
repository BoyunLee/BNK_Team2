package com.busanbank.loan.domain.admin.scheduler;

import com.busanbank.loan.domain.admin.service.ChangeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 형상이행 배치 — 승인(APPROVED)되고 예약시각이 도래한 변경 신청서를 라이브에 반영한다.
 * 1분마다 점검. (실제 운영에선 특정 배포 윈도우 cron 으로 조정 가능)
 */
@Component
@RequiredArgsConstructor
public class DeployScheduler {

    private final ChangeRequestService changeRequestService;

    @Scheduled(fixedDelayString = "${admin.deploy.interval-ms:60000}")
    public void deployDue() {
        changeRequestService.runDueDeployments();
    }
}
