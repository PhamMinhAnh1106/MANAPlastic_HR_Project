package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.SalaryRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryRuleRepository extends JpaRepository<SalaryRuleEntity, Integer> {
    Optional<SalaryRuleEntity> findByRuleCode(String ruleCode);
}