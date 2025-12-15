package com.manaplastic.backend.DTO.schedule;

import com.manaplastic.backend.constant.RequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class ShiftChangeListDTO {
    private Integer id;
    private Integer employeeId;
    private String employeeName;
    private String departmentName;
    private LocalDate targetDate;

    private String currentShiftName;
    private String newShiftName;

    private String reason;
    private RequestStatus status;
    private String approverName;
    private Instant createdAt;
}
