package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.TaxsettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaxSettingRepository extends JpaRepository<TaxsettingEntity, Integer> {

    boolean existsBySettingKeyAndEffectiveDate(String settingKey, LocalDate effectiveDate);

    boolean existsBySettingKeyAndEffectiveDateAndIdNot(String settingKey, LocalDate effectiveDate, Integer settingID);

    @Query("SELECT t FROM TaxsettingEntity t WHERE t.effectiveDate = " +
            "(SELECT MAX(t2.effectiveDate) FROM TaxsettingEntity t2 " +
            "WHERE t2.settingKey = t.settingKey AND t2.effectiveDate <= :targetDate AND t2.isActive = true)")
    List<TaxsettingEntity> findCurrentEffectiveSettings(LocalDate targetDate);

    List<TaxsettingEntity> findBySettingKeyContaining(String keyword);

}