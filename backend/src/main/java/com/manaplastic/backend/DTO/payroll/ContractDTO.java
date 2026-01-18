package com.manaplastic.backend.DTO.payroll;

import com.manaplastic.backend.constant.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDTO {
    // --- Thông tin cơ bản Hợp đồng ---
    private Integer id;
    private Integer contractTemplateId;
    private String contractTemplateName;
    private String contractname;
    private String type; // Loại hợp đồng
    private BigDecimal basesalary;
    private BigDecimal insuranceSalary;
    private BigDecimal standardHours;
    private String fileurl;
    private LocalDate signdate;
    private LocalDate startdate;
    private LocalDate enddate;
    private String status;

    // --- Thông tin Nhân viên (Bổ sung) ---
    private Integer userId;
    private String username;
    private String fullname;
    private String cccd;
    private String email;
    private String phone;
    private String address;
    private LocalDate dob;
    private Gender gender;

    // --- Thông tin Tổ chức ---
    private Integer departmentId;
    private String departmentName;
    private String roleName;

    // --- Thông tin bổ sung của HĐ ---
    private String workType;
    private BigDecimal insurancePercent;

    // --- Danh sách phụ cấp ---
    private List<ContractsAllowanceDTO> allowances;
}