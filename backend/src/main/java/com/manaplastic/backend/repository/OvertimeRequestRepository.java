package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.OvertimeRequestEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRequestRepository extends JpaRepository<OvertimeRequestEntity, Integer>, JpaSpecificationExecutor<OvertimeRequestEntity> {

    boolean existsByUseridAndDate(UserEntity targetUser, LocalDate date);

    @Query("SELECT r FROM OvertimeRequestEntity r WHERE r.userid = :user AND r.date = :date AND r.status = :status")
    List<OvertimeRequestEntity> findApprovedRequests(@Param("user") UserEntity user, @Param("date") LocalDate date, @Param("status") OvertimeRequestEntity.RequestStatus status);

    List<OvertimeRequestEntity> findByUseridAndDateAndStatus(UserEntity user, LocalDate date, OvertimeRequestEntity.RequestStatus status);
    @Query("SELECT r FROM OvertimeRequestEntity r " +
            "WHERE r.userid = :userId " +
            "AND MONTH(r.date) = :month " +
            "AND YEAR(r.date) = :year " +
            "AND r.status = 'APPROVED'")
    List<OvertimeRequestEntity> findApprovedRequestsByMonth(@Param("userId") UserEntity userId,
                                                            @Param("month") int month,
                                                            @Param("year") int year);
}
