package com.manaplastic.backend.DTO;

public record AttendanceFilterCriteria(
        Integer month,
        Integer year,
        Integer departmentId, // Chỉ dùng cho HR
        Integer userId, // Dùng để giới hạn cho Employee/Manager
        String status
) {}
