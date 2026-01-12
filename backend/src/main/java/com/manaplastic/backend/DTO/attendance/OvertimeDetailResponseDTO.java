package com.manaplastic.backend.DTO.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OvertimeDetailResponseDTO {
    private String overtimeTypeName;
    private Double hours;
    private LocalTime startTime;
    private LocalTime endTime;
}
