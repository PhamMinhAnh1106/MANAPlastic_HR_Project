package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.RewardpunishmentdecisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RewardPunishmentRepository  extends JpaRepository<RewardpunishmentdecisionEntity, Integer> {
    @Query("SELECT r FROM RewardpunishmentdecisionEntity r " +
            "WHERE r.userID.id = :userId " +
            "AND r.status = 'APPROVED' " +
            "AND r.decisionDate BETWEEN :fromDate AND :toDate")
    List<RewardpunishmentdecisionEntity> findApprovedDecisions(
            @Param("userId") int userId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}
