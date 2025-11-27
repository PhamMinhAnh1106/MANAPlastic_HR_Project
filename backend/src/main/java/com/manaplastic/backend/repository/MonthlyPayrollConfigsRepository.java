package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.MonthlypayrollconfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlyPayrollConfigsRepository extends JpaRepository<MonthlypayrollconfigEntity, Integer> {
    Optional<MonthlypayrollconfigEntity> findByMonthAndYear(int month, int year);
}
