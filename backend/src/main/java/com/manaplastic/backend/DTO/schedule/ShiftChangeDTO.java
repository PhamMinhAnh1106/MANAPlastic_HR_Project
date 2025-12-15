package com.manaplastic.backend.DTO.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftChangeDTO {
    LocalDate targetDate;
    Integer newShiftId;
    String reason;
}
