package com.manaplastic.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceDTO {
    private int attendanceId;
    private String userName;
    private String fullNameUser;
    private String attendanceDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String checkInImg;
    private String checkOutImg;
    private int shiftId;
    private String shiftName;
    private String status;

}
