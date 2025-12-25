package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.AttendanceRequestEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface AttendanceRequestRepository extends JpaRepository<AttendanceRequestEntity, Integer>,
        JpaSpecificationExecutor<AttendanceRequestEntity> {

    boolean existsByUseridAndDateAndStatus(UserEntity user, LocalDate date, AttendanceRequestEntity.RequestStatus status);
}
