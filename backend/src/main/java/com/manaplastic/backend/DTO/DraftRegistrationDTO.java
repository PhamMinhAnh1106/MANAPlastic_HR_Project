package com.manaplastic.backend.DTO;

import java.time.LocalDate;

// đăng ký MÔỘT NGÀY
public record DraftRegistrationDTO (
    LocalDate date,
    Integer shiftId,
    boolean isDayOff
) {
}
