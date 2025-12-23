package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.AttendanceRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttendanceRequestRepository extends JpaRepository<AttendanceRequestEntity, Integer>,
        JpaSpecificationExecutor<AttendanceRequestEntity> {
}
