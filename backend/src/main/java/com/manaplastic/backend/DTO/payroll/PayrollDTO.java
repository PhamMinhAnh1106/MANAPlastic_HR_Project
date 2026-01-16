package com.manaplastic.backend.DTO.payroll;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollDTO {
    private Integer userId;
    private String fullName;
    private String departmentName;
    private String jobType;

    private String payPeriod;
    private BigDecimal baseSalary;
    private BigDecimal actualWorkDays;

    private BigDecimal totalOvertimePay;
    private BigDecimal totalAllowance;
    private BigDecimal totalIncome;
    private BigDecimal taxableIncome;
    private BigDecimal pit;
    private BigDecimal netSalary;


    private BigDecimal bhxhEmp;
    private BigDecimal bhytEmp;
    private BigDecimal bhtnEmp;

    private BigDecimal bhxhComp;
    private BigDecimal bhytComp;
    private BigDecimal bhtnComp;


}