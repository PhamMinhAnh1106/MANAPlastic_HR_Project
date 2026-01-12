package com.manaplastic.backend.DTO.criteria;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OvertimeFilterCriteria {
    private Integer departmentId;
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String keyword;
}
