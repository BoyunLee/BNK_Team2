package com.busanbank.loan.domain.admin.controller;

import com.busanbank.loan.domain.admin.dto.request.ApproveRequest;
import com.busanbank.loan.domain.admin.dto.request.CreateChangeRequest;
import com.busanbank.loan.domain.admin.dto.request.RejectRequest;
import com.busanbank.loan.domain.admin.dto.request.SubmitApprovalRequest;
import com.busanbank.loan.domain.admin.dto.request.UpdateChangeRequest;
import com.busanbank.loan.domain.admin.dto.response.AdminUserResponse;
import com.busanbank.loan.domain.admin.dto.response.ChangeRequestResponse;
import com.busanbank.loan.domain.admin.dto.response.LiveProductResponse;
import com.busanbank.loan.domain.admin.entity.ChangeStatus;
import com.busanbank.loan.domain.admin.service.AdminUserService;
import com.busanbank.loan.domain.admin.service.ChangeRequestService;
import com.busanbank.loan.global.response.ApiResponse;
import com.busanbank.loan.global.util.AdminSessionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 상품 변경 결재 — 신청서 작성/상신/승인/반려/배포. 프론트 lib/admin.ts 계약과 정렬. */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminChangeRequestController {

    private final ChangeRequestService changeRequestService;
    private final AdminUserService adminUserService;

    // ── 참조 데이터 ──

    @GetMapping("/approvers")
    public ApiResponse<List<AdminUserResponse>> approvers() {
        return ApiResponse.ok(adminUserService.listApprovers().stream()
                .map(AdminUserResponse::from).toList());
    }

    @GetMapping("/products")
    public ApiResponse<List<LiveProductResponse>> liveProducts() {
        return ApiResponse.ok(changeRequestService.listLiveProducts());
    }

    // ── 신청서 ──

    @GetMapping("/change-requests")
    public ApiResponse<List<ChangeRequestResponse>> list(
            @RequestParam(required = false) ChangeStatus status,
            @RequestParam(required = false) Long approverId) {
        return ApiResponse.ok(changeRequestService.list(status, approverId));
    }

    @GetMapping("/change-requests/{id}")
    public ApiResponse<ChangeRequestResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(changeRequestService.get(id));
    }

    @PostMapping("/change-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChangeRequestResponse> create(@Valid @RequestBody CreateChangeRequest request) {
        Long adminId = AdminSessionUtil.getCurrentAdminId();
        return ApiResponse.created(changeRequestService.createDraft(adminId, request), "신청서가 작성되었습니다.");
    }

    @PutMapping("/change-requests/{id}")
    public ApiResponse<ChangeRequestResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateChangeRequest request) {
        Long adminId = AdminSessionUtil.getCurrentAdminId();
        return ApiResponse.ok(changeRequestService.updateDraft(adminId, id, request), "신청서가 수정되었습니다.");
    }

    @PostMapping("/change-requests/{id}/submit")
    public ApiResponse<ChangeRequestResponse> submit(@PathVariable Long id,
                                                     @Valid @RequestBody SubmitApprovalRequest request) {
        Long adminId = AdminSessionUtil.getCurrentAdminId();
        return ApiResponse.ok(changeRequestService.submit(adminId, id, request.approverId()), "결재 상신되었습니다.");
    }

    @PostMapping("/change-requests/{id}/approve")
    public ApiResponse<ChangeRequestResponse> approve(@PathVariable Long id,
                                                      @Valid @RequestBody ApproveRequest request) {
        Long adminId = AdminSessionUtil.getCurrentAdminId();
        return ApiResponse.ok(changeRequestService.approve(adminId, id, request), "승인되었습니다.");
    }

    @PostMapping("/change-requests/{id}/reject")
    public ApiResponse<ChangeRequestResponse> reject(@PathVariable Long id,
                                                     @Valid @RequestBody RejectRequest request) {
        Long adminId = AdminSessionUtil.getCurrentAdminId();
        return ApiResponse.ok(changeRequestService.reject(adminId, id, request.comment()), "반려되었습니다.");
    }

    @PostMapping("/change-requests/{id}/cancel")
    public ApiResponse<ChangeRequestResponse> cancel(@PathVariable Long id) {
        Long adminId = AdminSessionUtil.getCurrentAdminId();
        return ApiResponse.ok(changeRequestService.cancel(adminId, id), "신청이 취소되었습니다.");
    }

    @PostMapping("/change-requests/{id}/deploy-now")
    public ApiResponse<ChangeRequestResponse> deployNow(@PathVariable Long id) {
        Long adminId = AdminSessionUtil.getCurrentAdminId();
        return ApiResponse.ok(changeRequestService.deployNow(adminId, id), "즉시 배포되었습니다.");
    }
}
