package com.manaplastic.backend.DTO.schedule;

import java.time.LocalDate;

// đăng ký MÔỘT NGÀY
public record DraftRegistrationDTO (
    LocalDate date,
    Integer shiftId,
    String shiftName,
    boolean isDayOff
) {
}
