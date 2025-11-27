package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.OvertimeEntity;
import com.manaplastic.backend.entity.OvertimetypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRepository extends JpaRepository<OvertimeEntity, Integer> {

    // Tìm các phiếu OT của nhân viên trong khoảng thời gian và đã được duyệt
    @Query("SELECT o FROM OvertimeEntity o WHERE o.userid.id = :userId " +
            "AND o.date BETWEEN :startDate AND :endDate " +
            "AND o.status = 'APPROVED'")
    List<OvertimeEntity> findApprovedOvertime(@Param("userId") Integer userId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}
