package com.manaplastic.backend.DTO.payroll;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractExpiringDTO {
    private Integer id;
    private String contractCode;
    private String employeeName;
    private LocalDate endDate;
    private long daysRemaining;
}
