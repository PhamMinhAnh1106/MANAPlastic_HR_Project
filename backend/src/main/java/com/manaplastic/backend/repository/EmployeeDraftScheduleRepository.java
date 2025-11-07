package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.EmployeedraftscheduleEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeDraftScheduleRepository extends JpaRepository<EmployeedraftscheduleEntity, Integer> {
    Optional<EmployeedraftscheduleEntity> findByEmployeeIDAndDate(UserEntity employeeID, LocalDate date); // POST đăng ký ca - nháp
    List<EmployeedraftscheduleEntity> findByEmployeeIDAndMonthYear(UserEntity employeeID, String monthYear); // GET xem đăng ký ca - nháp
}
