package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.AttendanceEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer>, JpaSpecificationExecutor<AttendanceEntity> {

    //Tuwj động lấy log dữ liệu chấm công vào bảng chấm cong ( AttendanceLogs -> Attendance)
    Optional<AttendanceEntity> findByUserIDAndDate(UserEntity user, LocalDate date);

    @Query("SELECT COUNT(a) FROM AttendanceEntity a " +
            "WHERE a.userID.id = :userId " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.status = 'PRESENT'")
    Double countPresentDays(@Param("userId") int userId,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(a.estimatedSalary), 0) FROM AttendanceEntity a " +
            "WHERE a.userID.id = :userId " +
            "AND MONTH(a.date) = :month " +
            "AND YEAR(a.date) = :year")
    BigDecimal sumEstimatedSalaryByMonth(@Param("userId") Integer userId,
                                         @Param("month") int month,
                                         @Param("year") int year);

    List<AttendanceEntity> findAllByDate(LocalDate date);

    @Query("SELECT a FROM AttendanceEntity a " +
            "WHERE MONTH(a.date) = :month AND YEAR(a.date) = :year")
    List<AttendanceEntity> findAllByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
