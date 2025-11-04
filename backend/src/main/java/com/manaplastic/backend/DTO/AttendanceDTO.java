package com.manaplastic.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceDTO {
    private int attendanceId;
    private String attendanceDate;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String checkInImg;
    private String checkOutImg;
    private int shiftId;
    private String status;

}
