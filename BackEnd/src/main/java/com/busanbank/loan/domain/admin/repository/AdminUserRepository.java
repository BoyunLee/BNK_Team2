package com.busanbank.loan.domain.admin.repository;

import com.busanbank.loan.domain.admin.entity.AdminRole;
import com.busanbank.loan.domain.admin.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByLoginId(String loginId);

    List<AdminUser> findAllByRole(AdminRole role);
}
