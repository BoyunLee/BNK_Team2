package com.busanbank.loan.domain.loan.service;

import com.busanbank.loan.domain.loan.entity.ApplicationDocumentLog;
import com.busanbank.loan.domain.loan.entity.MydataConsent;
import com.busanbank.loan.domain.loan.repository.ApplicationDocumentLogRepository;
import com.busanbank.loan.domain.loan.repository.MydataConsentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 간편인증(SIMPLE_CERT) 전자서명 시, 해당 단계에서 사용한 데이터를 서버측에서 조립해
 * JSON 문자열로 반환한다. 반환값은 SIGNATURE.signed_data 에 AES-256 암호화되어 저장된다.
 *
 * 단계별 수집 데이터:
 *   - PRE_PROCESS : 공공마이데이터 동의 내역 + 동의한 서류 목록
 *   - CONTRACT    : 약정 확정 내역(상품·금액·금리·상환·만기·우대금리 등)
 *   - 그 외(LIMIT_INQUIRY 등) : 기본 컨텍스트(단계·계좌·고객·시각)만
 */
@Component
@RequiredArgsConstructor
public class SignaturePayloadAssembler {

    private static final Logger log = LoggerFactory.getLogger(SignaturePayloadAssembler.class);
    private static final String SIMPLE_CERT = "SIMPLE_CERT";

    private final MydataConsentRepository mydataConsentRepository;
    private final ApplicationDocumentLogRepository applicationDocumentLogRepository;
    private final ContractService contractService;
    private final ObjectMapper objectMapper;

    /**
     * @return 간편인증(SIMPLE_CERT)이면 단계별 데이터 JSON, 그 외 서명 방식이면 null
     */
    @Transactional(readOnly = true)
    public String assemble(String loanAccountNo, Long customerId, String signStep, String signType) {
        if (!SIMPLE_CERT.equals(signType)) {
            return null;   // 간편인증 단계만 데이터 저장 (요구사항 범위)
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("signStep", signStep);
        payload.put("signType", signType);
        payload.put("loanAccountNo", loanAccountNo);
        payload.put("customerId", customerId);
        payload.put("assembledAt", LocalDateTime.now().toString());

        try {
            switch (signStep == null ? "" : signStep) {
                case "PRE_PROCESS" -> putPreProcessData(payload, loanAccountNo);
                case "CONTRACT" -> payload.put("contract", contractService.getConfirmation(loanAccountNo));
                default -> { /* LIMIT_INQUIRY 등은 기본 컨텍스트만 저장 */ }
            }
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            // 서명 데이터 저장 실패가 서명 자체를 막지 않도록 graceful 처리
            log.warn("서명 데이터 조립 실패 loanAccountNo={} signStep={}", loanAccountNo, signStep, e);
            return null;
        }
    }

    private void putPreProcessData(Map<String, Object> payload, String loanAccountNo) {
        List<Map<String, Object>> consents = mydataConsentRepository.findAllByLoanAccountNo(loanAccountNo).stream()
                .map(this::consentToMap)
                .toList();
        List<Map<String, Object>> documents = applicationDocumentLogRepository.findAllByLoanAccountNo(loanAccountNo).stream()
                .filter(d -> "Y".equals(d.getAgreedYn()))
                .map(this::documentToMap)
                .toList();
        payload.put("mydataConsents", consents);
        payload.put("agreedDocuments", documents);
    }

    private Map<String, Object> consentToMap(MydataConsent c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("consentType", c.getConsentType());
        m.put("dataProvider", c.getDataProvider());
        m.put("consentYn", c.getConsentYn());
        m.put("consentAt", c.getConsentAt() != null ? c.getConsentAt().toString() : null);
        return m;
    }

    private Map<String, Object> documentToMap(ApplicationDocumentLog d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("documentType", d.getDocumentType());
        m.put("termsSeq", d.getTermsSeq());
        m.put("agreedYn", d.getAgreedYn());
        m.put("agreedAt", d.getAgreedAt() != null ? d.getAgreedAt().toString() : null);
        return m;
    }
}
