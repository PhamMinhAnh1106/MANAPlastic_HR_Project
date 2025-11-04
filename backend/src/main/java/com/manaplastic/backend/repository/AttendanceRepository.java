package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.AttendanceEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer> {
    List<AttendanceEntity> findAll(Specification<AttendanceEntity> spec);
}
