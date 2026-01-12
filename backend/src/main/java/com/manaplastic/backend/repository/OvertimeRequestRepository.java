package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.OvertimeRequestEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface OvertimeRequestRepository extends JpaRepository<OvertimeRequestEntity, Integer>, JpaSpecificationExecutor<OvertimeRequestEntity> {

    boolean existsByUseridAndDate(UserEntity targetUser, LocalDate date);
}
