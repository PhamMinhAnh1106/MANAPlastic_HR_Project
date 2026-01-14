package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.OvertimeRequestDetailEntity;
import com.manaplastic.backend.entity.OvertimeRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OvertimeRequestDetailRepository extends JpaRepository<OvertimeRequestDetailEntity, Integer> {

    List<OvertimeRequestDetailEntity> findByRequestID(OvertimeRequestEntity request);
}
