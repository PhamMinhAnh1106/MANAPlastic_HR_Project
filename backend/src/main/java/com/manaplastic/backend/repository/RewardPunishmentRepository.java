package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.RewardpunishmentdecisionEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RewardPunishmentRepository  extends JpaRepository<RewardpunishmentdecisionEntity, Integer>, JpaSpecificationExecutor<RewardpunishmentdecisionEntity> {
    @Query("SELECT r FROM RewardpunishmentdecisionEntity r " +
            "WHERE r.user = :userId " +
            "AND r.decisionDate BETWEEN :startDate AND :endDate " +
            "AND r.status IN ('APPROVED', 'PROCESSED')")
    List<RewardpunishmentdecisionEntity> findByMonth(@Param("userId") UserEntity userId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
}
