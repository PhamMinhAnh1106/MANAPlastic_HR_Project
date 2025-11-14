package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.LeavebalanceEntity;
import com.manaplastic.backend.entity.LeavebalanceEntityId;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeavebalanceEntity, LeavebalanceEntityId> {
    // Lấy TẤT CẢ số dư (AL, SL, PL, ML) của một nhân viên
    List<LeavebalanceEntity> findByUserID(UserEntity user);
}
