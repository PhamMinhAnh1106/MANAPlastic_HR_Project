package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.ActivitylogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivitylogEntity, Integer>, JpaSpecificationExecutor<ActivitylogEntity> {
}
