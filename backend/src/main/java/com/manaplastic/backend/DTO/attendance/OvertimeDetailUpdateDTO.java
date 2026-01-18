package com.manaplastic.backend.DTO.attendance;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class OvertimeDetailUpdateDTO {
    private Integer id;// Null = Thêm mới, Có số = Sửa
    private Integer overtimeTypeID;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double hours;
}
