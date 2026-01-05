package com.manaplastic.backend.DTO.account;

import com.manaplastic.backend.entity.EmployeeDocumentEntity;
import lombok.Data;

@Data
public class DocumentApprovalRequestDTO {
    private EmployeeDocumentEntity.DocumentStatus status;
    private String note;
}
