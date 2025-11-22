package com.manaplastic.backend.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractCreateDTO {
    private Integer userId;
    private String contractName;
    private String type; // Loại hdld => kiểm tra 2 lần ký hdld CÓ THỜI HẠN
    private BigDecimal baseSalary;
    private BigDecimal insuranceSalary;
    private String allowanceToxicType;
    private LocalDate signDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private MultipartFile file; // File scan hdld
}

