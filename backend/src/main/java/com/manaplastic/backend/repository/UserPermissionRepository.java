package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.UserpermissionEntity;
import com.manaplastic.backend.entity.UserpermissionEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserpermissionEntity, UserpermissionEntityId> {
    List<UserpermissionEntity> findByUserID_Id(Integer userId);
}
