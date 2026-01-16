package com.manaplastic.backend.DTO.payroll;

import com.manaplastic.backend.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDetailDTO {

    // --- 1. THÔNG TIN CHUNG (HEADER) ---
    private Integer userId;
    private String fullName;
    private String departmentName;
    private String jobType;
    private String payPeriod;       // "2025-01"
    private String status;          // FINAL, DRAFT

    // --- 2. NGÀY CÔNG & LƯƠNG CƠ BẢN ---
    private BigDecimal baseSalary;       // Lương cơ bản tháng này
    private BigDecimal standardWorkDays; // Ngày công chuẩn (26)
    private BigDecimal actualWorkDays;   // Ngày công thực tế

    // --- 3. TỔNG HỢP THU NHẬP (INCOME SUMMARY) ---
    private BigDecimal totalAllowance;   // Tổng phụ cấp (đã prorate)
    private BigDecimal totalOvertimePay; // Tổng tiền OT
    private BigDecimal totalBonus;       // Tổng thưởng
    private BigDecimal totalPunishment;  // Tổng phạt (để trừ)
    private BigDecimal totalIncome;      // Tổng thu nhập (Gross từ DB)

    // --- 4. THUẾ & BẢO HIỂM (TAX & INSURANCE) ---
    private BigDecimal taxableIncome;    // Thu nhập tính thuế (nếu có lưu)
    private BigDecimal pit;              // Thuế TNCN

    private BigDecimal bhxhEmp;
    private BigDecimal bhytEmp;
    private BigDecimal bhtnEmp;
    private BigDecimal totalInsuranceEmp; // Tổng BH nhân viên đóng

    // Phần công ty đóng (Cost)
    private BigDecimal bhxhComp;
    private BigDecimal bhytComp;
    private BigDecimal bhtnComp;

    // --- 5. THỰC LĨNH (NET) ---
    private BigDecimal netSalary;

    // --- 6. CHI TIẾT (LISTS) ---
    private List<PayrollComponentDTO> allowanceDetails;    // List phụ cấp
    private List<PayrollComponentDTO> overtimeDetails;     // List OT
    private List<PayrollComponentDTO> bonusDetails;        // List Thưởng
    private List<PayrollComponentDTO> punishmentDetails;   // List Phạt
}