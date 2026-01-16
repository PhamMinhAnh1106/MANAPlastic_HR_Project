package com.manaplastic.backend.DTO.attendance;

import lombok.Data;

import java.time.LocalTime;

@Data
public class OvertimeDetailUpdateDTO {
    private Integer id;// Null = Thêm mới, Có số = Sửa
    private Integer overtimeTypeID;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double hours;
}
