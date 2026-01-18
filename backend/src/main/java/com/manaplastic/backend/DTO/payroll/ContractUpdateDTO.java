package com.manaplastic.backend.DTO.payroll;

import com.manaplastic.backend.constant.Gender;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ContractUpdateDTO {
    private String contractName;
    private String type; // FIXED_TERM hoặc INDEFINITE
    private String workType; // FULLTIME hoặc PART_TIME

    private BigDecimal baseSalary;
    private BigDecimal insuranceSalary;
    private Double insurancePercent;    // % đóng BHXH
    private BigDecimal standardHours;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate signDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private String status;
    private Integer contractTemplateId;

    private String fullname;
    private String cccd;
    private String email;
    private String phone;
    private String address;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;
    private Gender gender;
    private Integer departmentId;
    private Integer roleId;

    private List<ContractsAllowanceDTO> allowances;
}