package com.manaplastic.backend.DTO.schedule;

import lombok.Data;

import java.time.LocalDate;

// đăng ký MÔỘT NGÀY
@Data
public class DraftRegistrationDTO {
    LocalDate date;
    Integer shiftId;
    String shiftName;
    boolean isDayOff;
}
