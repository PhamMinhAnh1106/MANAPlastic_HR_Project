package com.manaplastic.backend.DTO.legalsetting;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OvertimeTypeDTO {
    private String otCode;
    private String otName;
    private BigDecimal rate;
    private String calculationType;
    private Boolean isTaxExemptPart;
    private String taxExemptFormula;
    private BigDecimal taxExemptPercentage;
    private String description;
}
