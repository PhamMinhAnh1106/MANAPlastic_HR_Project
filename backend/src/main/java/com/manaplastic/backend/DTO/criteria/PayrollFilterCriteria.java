package com.manaplastic.backend.DTO.criteria;

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

    public void setPayPeriod(String payPeriod) {
        if (payPeriod != null && payPeriod.matches("\\d{4}-\\d{2}")) {
            String[] parts = payPeriod.split("-");
            this.year = Integer.parseInt(parts[0]);
            this.month = Integer.parseInt(parts[1]);
        }
    }
}
