package com.busanbank.loan.domain.admin.repository;

import com.busanbank.loan.domain.admin.entity.ChangeStatus;
import com.busanbank.loan.domain.admin.entity.ProductChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductChangeRequestRepository extends JpaRepository<ProductChangeRequest, Long> {

    List<ProductChangeRequest> findAllByOrderByIdDesc();

    List<ProductChangeRequest> findAllByStatusOrderByIdDesc(ChangeStatus status);

    List<ProductChangeRequest> findAllByStatusAndApproverIdOrderByIdDesc(ChangeStatus status, Long approverId);

    List<ProductChangeRequest> findAllByApproverIdOrderByIdDesc(Long approverId);

    List<ProductChangeRequest> findAllByStatus(ChangeStatus status);

    int countByProductIdAndStatus(Long productId, ChangeStatus status);
}
