package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.RewardpunishmentdecisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RewardPunishmentRepository  extends JpaRepository<RewardpunishmentdecisionEntity, Integer>, JpaSpecificationExecutor<RewardpunishmentdecisionEntity> {

}
