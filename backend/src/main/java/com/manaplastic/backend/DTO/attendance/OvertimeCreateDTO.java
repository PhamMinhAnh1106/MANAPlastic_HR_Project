package com.manaplastic.backend.DTO.attendance;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class OvertimeCreateDTO {
    private Integer targetUserId; // Nếu null -> Tạo cho chính mình. Nếu có ID -> Tạo hộ (chỉ dành cho Manager)
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer overtimetypeid;
    private String reason;
}
