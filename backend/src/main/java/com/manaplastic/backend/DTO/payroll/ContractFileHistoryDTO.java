package com.manaplastic.backend.DTO.payroll;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContractFileHistoryDTO {
    private Integer historyId;
    private String oldFileUrl;
    private LocalDateTime archivedAt;
    private String reasonForChange;
    private String requestedBy; // tên người yc đổi
    private Integer contractId;
    private String approvedBy; // tên người duyệt
}
