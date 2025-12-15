package com.manaplastic.backend.DTO.schedule;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ShiftChangeFilterCriteria {
    private String status;
    private Integer departmentId;
    private Integer employeeId;
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
}
