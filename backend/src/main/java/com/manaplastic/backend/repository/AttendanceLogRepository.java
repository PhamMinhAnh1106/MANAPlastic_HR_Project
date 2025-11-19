package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.AttendancelogEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceLogRepository extends JpaRepository<AttendancelogEntity, Integer> {

    // Tránh trường hợp spam liên tục của nhân sự
    Optional<AttendancelogEntity> findTopByUserIDOrderByTimestampDesc(UserEntity user);
}
