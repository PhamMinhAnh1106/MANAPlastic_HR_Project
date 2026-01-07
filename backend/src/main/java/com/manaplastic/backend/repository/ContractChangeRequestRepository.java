package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.ContractchangerequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractChangeRequestRepository extends JpaRepository<ContractchangerequestEntity, Integer>, JpaSpecificationExecutor<ContractchangerequestEntity> {
}
