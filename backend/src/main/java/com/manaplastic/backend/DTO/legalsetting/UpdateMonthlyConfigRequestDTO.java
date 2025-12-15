package com.manaplastic.backend.DTO.legalsetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMonthlyConfigRequestDTO {
    private LocalDate cycleStartDate;
    private LocalDate cycleEndDate;
    private Double standardWorkDays;
}
