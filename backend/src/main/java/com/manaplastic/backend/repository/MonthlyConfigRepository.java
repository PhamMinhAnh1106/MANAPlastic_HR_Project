package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.MonthlypayrollconfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyConfigRepository extends JpaRepository<MonthlypayrollconfigEntity, Integer> {
    // Kiểm tra trùng lặp cho tính năng Generate
    boolean existsByMonthAndYear(int month, int year);

    // Lọc theo năm
    List<MonthlypayrollconfigEntity> findByYear(int year);

    // Lọc năm tháng
    Optional<MonthlypayrollconfigEntity> findByMonthAndYear(int month, int year);

}
