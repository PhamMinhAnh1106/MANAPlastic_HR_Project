package com.manaplastic.backend.DTO.legalsetting;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenegatedYearlyConfigRequestDTO {
    private Integer year;
    private Double standardWorkDays;
    private Integer cycleStartDay;
    private Boolean isCyclePreviousMonth;
}
