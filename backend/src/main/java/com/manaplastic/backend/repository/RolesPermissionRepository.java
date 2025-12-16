package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.RolespermissionEntity;
import com.manaplastic.backend.entity.RolespermissionEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolesPermissionRepository extends JpaRepository<RolespermissionEntity, RolespermissionEntityId> {
    // Tìm tất cả config quyền của role cụ thể
    List<RolespermissionEntity> findByRoleID_Id(Integer roleId);

    List<RolespermissionEntity> findByRoleID_IdAndPermissionID_IdIn(Integer roleId, List<Integer> permissionIds);
}