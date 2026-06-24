package com.busanbank.loan.domain.loan.service;

import com.busanbank.loan.domain.loan.dto.request.SuitabilityRequest;
import com.busanbank.loan.domain.loan.entity.SuitabilityResponse;
import com.busanbank.loan.domain.loan.repository.SuitabilityResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuitabilityService {

    private final SuitabilityResponseRepository suitabilityResponseRepository;
    private final LoanApplicationService loanApplicationService;

    @Transactional
    public void saveSuitabilityResponses(String loanAccountNo, List<SuitabilityRequest.SuitabilityItem> responses) {
        loanApplicationService.findAndValidate(loanAccountNo, "1");

        List<SuitabilityResponse> entities = responses.stream()
                .map(item -> SuitabilityResponse.builder()
                        .loanAccountNo(loanAccountNo)
                        .questionCode(item.questionCode())
                        .question(item.question())
                        .answer(item.answer())
                        .build())
                .toList();

        suitabilityResponseRepository.saveAll(entities);
    }
}
