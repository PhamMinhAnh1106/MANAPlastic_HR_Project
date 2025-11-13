package com.manaplastic.backend.DTO;

import java.time.LocalDate;

public record LeaveRequestFilterCriteria (
        Integer departmentId,
        String username,
        String status,
        LocalDate fromDate,
        LocalDate toDate
) {
}
