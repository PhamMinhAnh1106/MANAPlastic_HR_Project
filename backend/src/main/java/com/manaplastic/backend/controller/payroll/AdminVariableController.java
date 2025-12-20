package com.manaplastic.backend.controller.payroll;


import com.manaplastic.backend.DTO.account.AdminUserDTO;
import com.manaplastic.backend.DTO.payroll.VariableRuleRequest;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.MonthlypayrollconfigEntity;
import com.manaplastic.backend.entity.SalaryvariableEntity;
import com.manaplastic.backend.repository.MonthlyPayrollConfigsRepository;
import com.manaplastic.backend.service.AdminService;
import com.manaplastic.backend.service.AdminVariableService;
import com.manaplastic.backend.service.SqlGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/payEngine/variables")
@CrossOrigin(origins = "*")
public class AdminVariableController {

    @Autowired
    private AdminVariableService variableService;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private MonthlyPayrollConfigsRepository monthlyPayrollConfigsRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private SqlGeneratorService sqlGeneratorService;

    // Lấy danh sách biến
    @GetMapping
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    public ResponseEntity<List<SalaryvariableEntity>> getAllVariables() {
        return ResponseEntity.ok(variableService.getAllVariables());
    }

    // Tạo mới biến
//    @PostMapping
//    @PreAuthorize("hasAnyAuthority('HR','Admin')")
//    public ResponseEntity<?> saveVariable(@RequestBody SalaryvariableEntity payload) {
//        try {
//            SalaryvariableEntity saved = variableService.saveVariable(payload);
//            return ResponseEntity.ok(saved);
//        } catch (IllegalArgumentException e) {
//            // Lỗi validate (trùng code, thiếu code...)
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            // Lỗi hệ thống khác
//            return ResponseEntity.badRequest().body("Lỗi hệ thống: " + e.getMessage());
//        }
//    }
    @PostMapping("/builder")
    @LogActivity(action="CREATE_VARIABLE_SALARY", description = "Thêm mới biến/ giá trị đầu vào tính lương")
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<?> createVariable(@RequestBody VariableRuleRequest request) {
        try {
            return ResponseEntity.ok(variableService.createVariable(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cập nhật biến
    @PutMapping("/builder/{id}")
    @LogActivity(action="UPDATE_VARIABLE_SALARY", description = "Chỉnh sửa nguồn truy xuất biến tính lương",logType = LogType.WARNING)
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<?> updateVariable(@PathVariable Integer id, @RequestBody VariableRuleRequest request) {
        try {
            return ResponseEntity.ok(variableService.updateVariable(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy ds trươờng hỗ trợ
    @GetMapping("/fields")
    public ResponseEntity<Map<String, String>> getBuilderFields() {
        // Gọi hàm getAvailableFields() bạn vừa viết bên Service
        return ResponseEntity.ok(sqlGeneratorService.getAvailableFields());
    }

    // Xóa biến
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @LogActivity(action="DELETE_VARIABLE_SALARY", description = "Xóa biến/ giá trị đầu vào tính lương",logType = LogType.DANGER)
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<?> deleteVariable(@PathVariable int id) {
        try {
            variableService.deleteVariable(id);
            return ResponseEntity.ok("Đã xóa biến thành công.");
        } catch (Exception e) {
            // Thường là lỗi Foreign Key constraint nếu biến đang được dùng
            return ResponseEntity.badRequest().body("Không thể xóa biến này (có thể đang được sử dụng trong công thức hoặc lịch sử lương).");
        }
    }

    //Lấy ds user de audit
    @GetMapping("/userListAudit")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    public ResponseEntity<List<AdminUserDTO>> getUsersForDropdown() {
        try {
            List<AdminUserDTO> users = adminService.getAllUsersForDropdown();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // check xem lấy đúng ý đồ chưa
    @PostMapping("/audit")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    public ResponseEntity<?> auditVariable(@RequestBody Map<String, Object> payload) {
        try {
            Integer userId = payload.get("userId") != null ? Integer.parseInt(payload.get("userId").toString()) : 0;
            int month = payload.get("month") != null ? Integer.parseInt(payload.get("month").toString()) : LocalDate.now().getMonthValue();
            int year = payload.get("year") != null ? Integer.parseInt(payload.get("year").toString()) : LocalDate.now().getYear();

            String sqlToAudit = (String) payload.get("sql");

            if (userId == 0) {
                return ResponseEntity.badRequest().body("Vui lòng chọn nhân viên (userId) để kiểm tra.");
            }
            if (sqlToAudit == null || sqlToAudit.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Công thức/SQL cần kiểm tra đang bị trống.");
            }

            Optional<MonthlypayrollconfigEntity> configOpt = monthlyPayrollConfigsRepository.findByMonthAndYear(month, year);

            if (configOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Lỗi: Kỳ lương tháng " + month + "/" + year + " chưa được thiết lập (Config not found).");
            }

            MonthlypayrollconfigEntity payrollConfig = configOpt.get();

            LocalDate cycleStartDate = payrollConfig.getCycleStartDate();
            LocalDate cycleEndDate = payrollConfig.getCycleEndDate();
            Double standardWorkingDays = payrollConfig.getStandardWorkDays().doubleValue(); // Ví dụ: 24 hoặc 26 công

            String upperSql = sqlToAudit.trim().toUpperCase();
            if (upperSql.startsWith("UPDATE") || upperSql.startsWith("DELETE") || upperSql.startsWith("DROP") || upperSql.startsWith("INSERT")) {
                return ResponseEntity.badRequest().body("Chỉ cho phép chạy câu lệnh SELECT.");
            }

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("userId", userId);
            params.addValue("month", month);
            params.addValue("year", year);
            // Quan trọng: Truyền ngày bắt đầu/kết thúc chuẩn của kỳ lương vào params
            params.addValue("startDate", cycleStartDate);
            params.addValue("endDate", cycleEndDate);
            params.addValue("stdDays", standardWorkingDays);


            Object result;
            try {
                // queryForObject dành cho việc lấy 1 giá trị duy nhất (Scalar)
                result = namedParameterJdbcTemplate.queryForObject(sqlToAudit, params, Object.class);
            } catch (org.springframework.dao.EmptyResultDataAccessException e) {
                result = 0;
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body("Lỗi cú pháp hoặc dữ liệu khi chạy Audit: " + ex.getCause().getMessage());
            }


            Map<String, Object> response = new HashMap<>();
            response.put("result", result != null ? result : 0);
            response.put("auditContext", "Đã kiểm tra trên kỳ lương: " + month + "/" + year
                    + " (" + cycleStartDate + " đến " + cycleEndDate + ")");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}