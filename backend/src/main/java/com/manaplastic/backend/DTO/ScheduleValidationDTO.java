package com.manaplastic.backend.DTO;

import java.time.LocalDate;

public record ScheduleValidationDTO (
        LocalDate date,
        Integer shiftId,
        String shiftName,
        boolean isCompliant, // true = ổn, false = có vấn đề
        String message
){
}
