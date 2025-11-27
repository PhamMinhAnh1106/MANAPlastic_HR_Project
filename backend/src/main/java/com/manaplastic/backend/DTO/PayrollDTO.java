package com.manaplastic.backend.DTO;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollDTO {
    private Integer userId;
    private String fullName;
    private String departmentName;
    private String jobTitle; // Cần kiểm tra xem bạn có cột này trong entity User không
    private String jobType;

    // Chi tiết lương
    private String payPeriod;        // Kỳ lương
    private BigDecimal baseSalary;   // Lương cơ bản
    private Double actualWorkDays;   // Ngày công thực tế

    // Các cột tính toán cơ bản
    private BigDecimal totalOvertimePay; // Tiền OT
    private BigDecimal totalAllowance;   // Tiền phụ cấp
    private BigDecimal totalIncome;      // Tổng thu nhập (Gross)
    private BigDecimal taxableIncome;    // Thu nhập tính thuế
    private BigDecimal pit;              // Thuế TNCN
    private BigDecimal netSalary;        // Thực lĩnh

    // --- CỘT BỔ SUNG: CHI TIẾT BẢO HIỂM ---

    // Mức lương cơ sở dùng để đóng bảo hiểm
    private BigDecimal insuranceBase;

    // Tổng BH nhân viên đóng (Cột cũ, giữ lại để tương thích nếu cần)
    private BigDecimal insuranceEmp;

    // Chi tiết Nhân viên đóng (Employee Contribution)
    private BigDecimal bhxhEmp;          // BHXH nhân viên đóng (SOCIAL)
    private BigDecimal bhytEmp;          // BHYT nhân viên đóng (HEALTH)
    private BigDecimal bhtnEmp;          // BHTN nhân viên đóng (UNEMPLOYMENT)

    // Chi tiết Công ty đóng (Company Contribution)
    private BigDecimal bhxhComp;         // BHXH công ty đóng (SOCIAL)
    private BigDecimal bhytComp;         // BHYT công ty đóng (HEALTH)
    private BigDecimal bhtnComp;         // BHTN công ty đóng (UNEMPLOYMENT)

    private BigDecimal otTaxExempt;

}