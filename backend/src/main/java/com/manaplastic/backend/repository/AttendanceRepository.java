package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.AttendanceEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer> {
    List<AttendanceEntity> findAll(Specification<AttendanceEntity> spec);
}
