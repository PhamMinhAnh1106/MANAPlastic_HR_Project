package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.EmployeeofficialscheduleEntity;
import com.manaplastic.backend.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeOfficialScheduleRepository extends JpaRepository<EmployeeofficialscheduleEntity, Integer> {
    //Nhân viên
    List<EmployeeofficialscheduleEntity> findByEmployeeIDAndMonthYear(UserEntity employeeID, String monthYear);

    //Quản lý
    @Transactional
    @Modifying
    void deleteByEmployeeIDInAndMonthYear(List<UserEntity> employees, String monthYear); // xóa lịch chính thức Cũ (nếu có, vì có thể sửa lịch trong quá trình làm)

    List<EmployeeofficialscheduleEntity> findByEmployeeIDInAndMonthYear(List<UserEntity> employeesInDept, String monthYear);

    Optional<EmployeeofficialscheduleEntity> findByEmployeeIDAndDate(UserEntity employee, LocalDate date);
}
