package com.manaplastic.backend.DTO.payroll;

import lombok.Data;

@Data
public class ApproveRequestDTO {
    private String status;
    private String adminNote;
}