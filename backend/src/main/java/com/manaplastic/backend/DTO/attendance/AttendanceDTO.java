package com.manaplastic.backend.DTO.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceDTO {
    private Integer attendanceId;
    private String userName;
    private String fullNameUser;
    private String departmentName;
    private String attendanceDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String checkInImg;
    private String checkOutImg;
    private Integer shiftId;
    private String shiftName;
    private String status;
    private BigDecimal estimatedSalary;

}
