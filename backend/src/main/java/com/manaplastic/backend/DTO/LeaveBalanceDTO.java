package com.manaplastic.backend.DTO;

public record LeaveBalanceDTO(
        String leaveType,
        Integer leaveTypeId,
        Integer year,
        Integer totalGranted,
        Integer carriedOver,
        Integer daysUsed,
        Integer remaining // Còn lại nhieeu ngày ( totalGranted trừ daysUsed)
) {
}
