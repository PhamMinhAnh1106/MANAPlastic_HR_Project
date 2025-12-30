package com.manaplastic.backend.DTO.payroll;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractUpdateDTO {
    private String contractName;
    private String type; // FIXED_TERM hoặc INDEFINITE
    private BigDecimal baseSalary;
    private BigDecimal insuranceSalary;
    private String allowanceToxicType;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate signDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private String status;
    private MultipartFile file;
}
