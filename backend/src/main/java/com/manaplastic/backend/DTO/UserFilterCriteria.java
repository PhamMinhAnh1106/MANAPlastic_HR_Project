package com.manaplastic.backend.DTO;


import java.time.LocalDate;

public record UserFilterCriteria(
        String keyword,
        Integer departmentId,
        Integer roleId,
        String status,
        Integer gender,
        LocalDate hireDateStart,
        LocalDate hireDateEnd
) {}