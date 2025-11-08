package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.EmployeedraftscheduleEntity;
import com.manaplastic.backend.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeDraftScheduleRepository extends JpaRepository<EmployeedraftscheduleEntity, Integer> {
    //Nhân viên
    Optional<EmployeedraftscheduleEntity> findByEmployeeIDAndDate(UserEntity employeeID, LocalDate date); // POST đăng ký ca - nháp

    List<EmployeedraftscheduleEntity> findByEmployeeIDAndMonthYear(UserEntity employeeID, String monthYear); // GET xem đăng ký ca - nháp

    //Quản lý
    List<EmployeedraftscheduleEntity> findByEmployeeIDInAndMonthYear(List<UserEntity> employees, String monthYear); // kiếm nhân viên  in nhóm nhân diên

    // Xóa tất cả bản nháp của MỘT NHÓM nhân viên cho một tháng (sau khi hoàn tất), tránh làm nặng db
    @Transactional
    @Modifying
    void deleteByEmployeeIDInAndMonthYear(List<UserEntity> employees, String monthYear);
}

