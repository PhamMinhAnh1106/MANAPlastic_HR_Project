package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.AttendanceEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer> {
    List<AttendanceEntity> findAll(Specification<AttendanceEntity> spec);

    //Tuwj động lấy log dữ liệu chấm công vào bảng chấm cong ( AttendanceLogs -> Attendance)
    Optional<AttendanceEntity> findByUserIDAndDate(UserEntity user, LocalDate date);
}
