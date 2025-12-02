package com.manaplastic.backend.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollFilterCriteria {
    private Integer month;
    private Integer year;
    private Integer departmentId;
    private String username;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
}
