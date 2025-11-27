package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.TaxsettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TaxSettingRepository extends JpaRepository<TaxsettingEntity, Integer> {
    @Query("SELECT t FROM TaxsettingEntity t " +
            "WHERE t.settingKey = :key " +
            "AND t.effectiveDate <= :effectiveDate " +
            "AND t.isActive = true " +
            "ORDER BY t.effectiveDate DESC " +
            "LIMIT 1")
    Optional<TaxsettingEntity> findActiveSettingByKey(
            @Param("key") String key,
            @Param("effectiveDate") LocalDate effectiveDate
    );


}
