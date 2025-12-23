package com.manaplastic.backend.DTO.account;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityLogDTO {
    private Integer logID;
    private String action;
    private String logType;
    private String details;
    private LocalDateTime actionTime;

    private String performedBy; // username
    private String executorName; // tên người làm
}
