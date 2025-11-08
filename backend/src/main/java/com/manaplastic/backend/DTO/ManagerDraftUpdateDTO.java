package com.manaplastic.backend.DTO;

import java.time.LocalDate;

public record ManagerDraftUpdateDTO(
        Integer employeeId,
        LocalDate date,
        Integer shiftId,
        Boolean isDayOff
) {
}
