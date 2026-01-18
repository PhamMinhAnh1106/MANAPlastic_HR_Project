package com.manaplastic.backend.DTO.attendance;

import com.manaplastic.backend.entity.OvertimeRequestEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class OvertimeResponseDTO {
    private Integer requestId;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalHours;
    private Double finalPaidHours;
    private OvertimeRequestEntity.RequestStatus status;
    private String reason;
    private Boolean isSystemGenerated;
    private String employeeId;     // username hoặc mã NV
    private String employeeName;
    private String departmentName;
    private String managerName;
    private String hrName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OvertimeDetailResponseDTO> details;
}

