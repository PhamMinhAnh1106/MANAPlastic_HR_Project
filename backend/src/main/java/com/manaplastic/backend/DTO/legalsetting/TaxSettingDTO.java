package com.manaplastic.backend.DTO.legalsetting;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TaxSettingDTO {
    private String settingKey;
    private BigDecimal value;
    private LocalDate effectiveDate;
    private Boolean isActive;
    private String description;
}