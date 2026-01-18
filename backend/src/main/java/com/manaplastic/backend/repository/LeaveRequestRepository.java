package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.LeaverequestEntity;
import com.manaplastic.backend.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaverequestEntity, Integer>, JpaSpecificationExecutor<LeaverequestEntity> {
    /**
     * Tìm tất cả đơn nghỉ phép ĐÃ DUYỆT của một nhóm nhân viên
     * mà có ngày BẮT ĐẦU <= ngày cuối tháng VÀ ngày KẾT THÚC >= ngày đầu tháng.
     * (Tìm tất cả các đơn nghỉ phép có chồng chéo với tháng này)
     */
    @Query("SELECT l FROM LeaverequestEntity l WHERE l.userID IN :employees AND l.status = 'APPROVED' AND l.startdate <= :monthEnd AND l.enddate >= :monthStart")
    List<LeaverequestEntity> findApprovedLeavesIntersectingMonth(
            @Param("employees") List<UserEntity> employees,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd
    ); // Dùng cho chức năng auto xếp ca (pool đơn nghỉ pheps)
    List<LeaverequestEntity> findByUserIDOrderByRequestdateDesc(UserEntity currentUserId);

    @Modifying
    @Transactional
    int deleteByIdAndUserIDAndStatus(Integer id, UserEntity userID, LeaverequestEntity.LeaverequestStatus status);

    // check trùng lịch
    @Query("SELECT COUNT(l) > 0 FROM LeaverequestEntity l " +
            "WHERE l.userID = :user " +
            "AND l.status IN ('PENDING', 'APPROVED') " +
            "AND ((:start BETWEEN l.startdate AND l.enddate) " +
            "OR (:end BETWEEN l.startdate AND l.enddate) " +
            "OR (l.startdate BETWEEN :start AND :end))")
    boolean existsOverlappingRequest(UserEntity user, LocalDate start, LocalDate end);

    // Tìm đơn nghỉ phép đã duyệt của user bao trùm ngày hiện tại - cho chức năng tính lương hàng ngày (ngày nghỉ)
    @Query("SELECT r FROM LeaverequestEntity r " +
            "WHERE r.userID = :user " +
            "AND :date BETWEEN r.startdate AND r.enddate " +
            "AND r.status = 'APPROVED'")
    Optional<LeaverequestEntity> findApprovedRequestForDate(
            @Param("user") UserEntity user,
            @Param("date") LocalDate date
    );
}
