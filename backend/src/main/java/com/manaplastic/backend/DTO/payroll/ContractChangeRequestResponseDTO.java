package com.manaplastic.backend.DTO.payroll;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContractChangeRequestResponseDTO {
    private Integer requestId;
    private String reason;
    private String newFileUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private String adminNote;

    private Integer requesterId;
    private String requesterName;

    private String adminName;

    private Integer contractId;
    private String oldFileUrl;
    private String contractOwnerName;
}