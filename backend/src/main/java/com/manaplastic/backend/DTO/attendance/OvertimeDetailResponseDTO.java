package com.manaplastic.backend.DTO.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OvertimeDetailResponseDTO {
    private Integer id;
    private String overtimeTypeName;
    private Double hours;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
