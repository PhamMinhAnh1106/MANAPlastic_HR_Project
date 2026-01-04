package com.manaplastic.backend.DTO.payroll;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractsAllowanceDTO {
    private Integer allowanceCategoryId;

    private String allowanceName;
    private String allowanceType;
    private BigDecimal amount;

    private Boolean isTaxable;
    private Boolean isInsuranceBase;
    private BigDecimal taxFreeAmount;
}
