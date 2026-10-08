package com.sena.Backend_OneLanguage.permissions.repository;

import com.sena.Backend_OneLanguage.permissions.entity.UserPermission;
import com.sena.Backend_OneLanguage.permissions.entity.UserPermissionId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UserPermissionId> {
    List<UserPermission> findByUserIdUser(UUID userId);
}
