package com.manaplastic.backend.DTO.payroll;

import com.manaplastic.backend.constant.Gender;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ContractCreateDTO {
    private Long userId; // Nếu NULL -> Hệ thống tự hiểu là Nhân viên mới

    // --- Phần thông tin cá nhân (Bắt buộc nếu userId = null) ---
    private String fullname;
    private String cccd;
    private String email;
    private String phone;
    private String address;
    private LocalDate dob; // Ngày sinh
    private Gender gender; // MALE/FEMALE

    // --- Phần thông tin Hợp đồng ---
    private String contractType; // FIXED_TERM (Xác định TH) hoặc INDEFINITE (Vô thời hạn)
    private LocalDate startDate;
    private LocalDate endDate;   // Null nếu là Vô thời hạn
    private BigDecimal baseSalary;
    private String workType;
    private Double insurancePercent;
    private BigDecimal standardHours;
    private Long departmentId;
    private Long roleId;
    private Integer templateId; // ID của mẫu hợp đồng được chọn

    // Danh sách phụ cấp đi kèm
    private List<ContractsAllowanceDTO> allowances;
}

