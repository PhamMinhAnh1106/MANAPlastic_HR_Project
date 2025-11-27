package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.DependentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DependentsRepository extends JpaRepository<DependentEntity, Integer> {
    @Query("SELECT COUNT(d) FROM DependentEntity d WHERE d.userID.id = :userId AND d.isTaxDeductible = true")
    long countTaxDeductibleDependents(int userId);
}
