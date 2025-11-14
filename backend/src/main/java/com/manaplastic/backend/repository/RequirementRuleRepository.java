package com.manaplastic.backend.repository;

import com.manaplastic.backend.DTO.RequirementRuleDTO;
import com.manaplastic.backend.entity.RequirementrulesEntity;
import com.manaplastic.backend.entity.SchedulerequirementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RequirementRuleRepository extends JpaRepository<RequirementrulesEntity, Integer> {


    void deleteByRequirementID(SchedulerequirementEntity entity);
}
