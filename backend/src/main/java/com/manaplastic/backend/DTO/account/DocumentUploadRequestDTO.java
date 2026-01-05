package com.manaplastic.backend.DTO.account;

import com.manaplastic.backend.entity.EmployeeDocumentEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class DocumentUploadRequestDTO {
    private Integer userId; // ID nhân viên nộp
    private EmployeeDocumentEntity.DocumentType documentType;
    private String documentName;
    private String issuingAuthority;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate issueDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expiryDate;
    private String note;

}