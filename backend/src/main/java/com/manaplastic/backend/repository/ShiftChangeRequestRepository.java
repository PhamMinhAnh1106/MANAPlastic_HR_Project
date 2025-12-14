package com.manaplastic.backend.repository;

import com.manaplastic.backend.constant.RequestStatus;
import com.manaplastic.backend.entity.ShiftChangeRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftChangeRequestRepository extends JpaRepository<ShiftChangeRequestEntity, Integer>, JpaSpecificationExecutor<ShiftChangeRequestEntity> {
    boolean existsByEmployeeID_IdAndDateAndStatus(Integer employeeId, LocalDate date, RequestStatus status);

}
