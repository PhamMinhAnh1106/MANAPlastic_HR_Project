package com.manaplastic.backend.DTO;

import java.time.LocalDate;

public record LeaverequestDTO(
        Integer leaverequestID,
        String username,
        String fullname,
        String leavetype,
        LocalDate startdate,
        LocalDate enddate,
        String reason,
        String status,
        LocalDate requestdate

) {
}
