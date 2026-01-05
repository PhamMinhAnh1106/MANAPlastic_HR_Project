package com.manaplastic.backend.DTO.account;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeDocumentDTO {
    private Integer documentID;
    private String employeeName;
    private String type;
    private String name;
    private String status;
    private String fileUrl;
    private LocalDate expiryDate;
}