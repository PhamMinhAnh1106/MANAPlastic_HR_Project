package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.SalaryRuleVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRuleVersionRepository extends JpaRepository<SalaryRuleVersionEntity, Integer> {
}
