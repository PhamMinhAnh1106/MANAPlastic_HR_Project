package com.manaplastic.backend.DTO.schedule;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BatchScheduleRegistrationDTO {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer rangeShiftId; // Ca làm việc áp dụng chung cho khoảng này

    // ngày ngoại lệ trong khoảng đó
    private List<DraftRegistrationDTO> specificDays;
}