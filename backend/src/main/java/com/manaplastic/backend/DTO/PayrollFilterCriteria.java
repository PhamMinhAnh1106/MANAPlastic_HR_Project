package com.manaplastic.backend.DTO;

import lombok.Data;

@Data
public class PayrollFilterCriteria {
    private Integer month;
    private Integer year;
    private Integer departmentId;
    private String userName;
}
