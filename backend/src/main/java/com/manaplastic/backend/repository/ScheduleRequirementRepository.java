package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.DepartmentEntity;
import com.manaplastic.backend.entity.SchedulerequirementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRequirementRepository extends JpaRepository<SchedulerequirementEntity, Integer> {
    List<SchedulerequirementEntity> findByDepartmentID_Id(Integer departmentId);
    List<SchedulerequirementEntity> findByDepartmentID(DepartmentEntity department);
}
