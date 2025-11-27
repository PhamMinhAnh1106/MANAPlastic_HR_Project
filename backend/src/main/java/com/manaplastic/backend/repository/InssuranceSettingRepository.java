package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.InsurancesettingEntity;
import com.manaplastic.backend.entity.TaxsettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InssuranceSettingRepository extends JpaRepository<InsurancesettingEntity, Integer> {
    @Query("SELECT i FROM InsurancesettingEntity i " +
            "WHERE i.isActive = true " +
            "AND i.effectiveDate <= :effectiveDate")
    List<InsurancesettingEntity> findAllActiveSettings(@Param("effectiveDate") LocalDate effectiveDate);


}
