package com.manaplastic.backend.DTO.payroll;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ContractRequestDTO {
    private Integer contractId;
    private MultipartFile file;
    private String reason;
}
