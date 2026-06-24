package com.busanbank.loan.domain.customer.dto.response;

import com.busanbank.loan.domain.customer.entity.Customer;
import com.busanbank.loan.global.util.MaskingUtil;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 화면 노출용 고객 응답 DTO.
 *
 * [복호화 흐름]
 *   DB(암호문) → JPA Converter 자동 복호화 → Customer Entity(평문)
 *   → CustomerResponse.from() → 화면(평문 or 마스킹)
 *
 * [노출 레벨]
 *   - full()   : 본인 확인 후 전체 정보 (마이페이지, 대출 신청 최종 확인)
 *   - masked() : 목록·요약 화면에서 개인정보 부분 마스킹
 */
@Getter
@Builder
public class CustomerResponse {

    private final Long customerId;
    private final String name;
    private final String phoneNo;
    private final LocalDate birthDate;
    private final String address;
    private final String email;
    private final String emailVerifiedYn;
    private final String status;

    /** 평문 전체 노출 — 본인 확인이 완료된 화면에서 사용 */
    public static CustomerResponse full(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .phoneNo(customer.getPhoneNo())
                .birthDate(customer.getBirthDate())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .emailVerifiedYn(customer.getEmailVerifiedYn())
                .status(customer.getStatus())
                .build();
    }

    /**
     * 마스킹 노출 — 목록·요약·대출 신청 중간 화면에서 사용
     *   이름:     홍길동 → 홍**
     *   전화번호: 010-1234-5678 → 010-****-5678
     *   주소:     부산시 중구 ... → 부산시 중구 ***
     *   이메일:   test@email.com → te**@email.com
     */
    public static CustomerResponse masked(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .name(MaskingUtil.maskName(customer.getName()))
                .phoneNo(MaskingUtil.maskPhone(customer.getPhoneNo()))
                .birthDate(customer.getBirthDate())           // 생년월일은 그대로 (화면 필요에 따라 마스킹 추가)
                .address(MaskingUtil.maskAddress(customer.getAddress()))
                .email(MaskingUtil.maskEmail(customer.getEmail()))
                .emailVerifiedYn(customer.getEmailVerifiedYn())
                .status(customer.getStatus())
                .build();
    }
}
