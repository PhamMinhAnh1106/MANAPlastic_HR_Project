package com.manaplastic.backend.controller;


import com.manaplastic.backend.DTO.AdminUserDTO;
import com.manaplastic.backend.entity.MonthlypayrollconfigEntity;
import com.manaplastic.backend.entity.SalaryvariableEntity;
import com.manaplastic.backend.repository.MonthlyPayrollConfigsRepository;
import com.manaplastic.backend.repository.SalaryVariableRepository;
import com.manaplastic.backend.service.AdminService;
import com.manaplastic.backend.service.AdminVariableService;
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

    // Lấy danh sách biến
    @GetMapping
    @PreAuthorize("hasAuthority('HR') or hasAuthority('Admin')")
    public ResponseEntity<List<SalaryvariableEntity>> getAllVariables() {
        return ResponseEntity.ok(variableService.getAllVariables());
    }

    // Tạo mới hoặc Cập nhật biến
    @PostMapping
    @PreAuthorize("hasAuthority('HR') or hasAuthority('Admin')")
    public ResponseEntity<?> saveVariable(@RequestBody SalaryvariableEntity payload) {
        try {
            SalaryvariableEntity saved = variableService.saveVariable(payload);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            // Lỗi validate (trùng code, thiếu code...)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Lỗi hệ thống khác
            return ResponseEntity.badRequest().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // Xóa biến
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('HR') or hasAuthority('Admin')")
    public ResponseEntity<?> deleteVariable(@PathVariable int id) {
        try {
            variableService.deleteVariable(id);
            return ResponseEntity.ok("Đã xóa biến thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            // Thường là lỗi Foreign Key constraint nếu biến đang được dùng
            return ResponseEntity.badRequest().body("Không thể xóa biến này (có thể đang được sử dụng trong công thức hoặc lịch sử lương).");
        }
    }

    @GetMapping("/userListAudit")
    @PreAuthorize("hasAuthority('HR') or hasAuthority('Admin')")
    public ResponseEntity<List<AdminUserDTO>> getUsersForDropdown() {
        try {
            List<AdminUserDTO> users = adminService.getAllUsersForDropdown();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/audit")
    @PreAuthorize("hasAuthority('HR') or hasAuthority('Admin')")
    public ResponseEntity<?> auditVariable(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Lấy tham số đầu vào
            Integer userId = payload.get("userId") != null ? Integer.parseInt(payload.get("userId").toString()) : 0;
            // Mặc định lấy tháng hiện tại nếu không truyền
            int month = payload.get("month") != null ? Integer.parseInt(payload.get("month").toString()) : LocalDate.now().getMonthValue();
            int year = payload.get("year") != null ? Integer.parseInt(payload.get("year").toString()) : LocalDate.now().getYear();

            // Lấy câu SQL hoặc công thức cần test (từ payload)
            String sqlToAudit = (String) payload.get("sql");

            if (userId == 0) {
                return ResponseEntity.badRequest().body("Vui lòng chọn nhân viên (userId) để kiểm tra.");
            }
            if (sqlToAudit == null || sqlToAudit.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Công thức/SQL cần kiểm tra đang bị trống.");
            }

            // --- QUAN TRỌNG: KIỂM TRA CHU KỲ LƯƠNG TỪ DB ---

            // 2. Gọi Repository để lấy cấu hình kỳ lương
            Optional<MonthlypayrollconfigEntity> configOpt = monthlyPayrollConfigsRepository.findByMonthAndYear(month, year);

            if (configOpt.isEmpty()) {
                // Nếu không tìm thấy -> Chưa thiết lập kỳ lương -> Không cho phép Audit
                return ResponseEntity.badRequest().body("Lỗi: Kỳ lương tháng " + month + "/" + year + " chưa được thiết lập (Config not found).");
            }

            MonthlypayrollconfigEntity payrollConfig = configOpt.get();

            // Kiểm tra thêm trạng thái nếu cần (Ví dụ: Nếu kỳ lương đã KHÓA thì có cho audit không?)
            // if ("LOCKED".equals(payrollConfig.getStatus())) { ... }

            // 3. Sử dụng dữ liệu THỰC TẾ từ Config để bind vào tham số
            // Thay vì tự tính YearMonth.atDay(1), ta lấy từ Entity.
            // Điều này quan trọng nếu kỳ lương của bạn tính từ ngày 26 tháng trước đến 25 tháng này.

            // Giả sử Entity có getStartDate() và getEndDate()
            LocalDate cycleStartDate = payrollConfig.getCycleStartDate();
            LocalDate cycleEndDate = payrollConfig.getCycleEndDate();
            Double standardWorkingDays = payrollConfig.getStandardWorkDays().doubleValue(); // Ví dụ: 24 hoặc 26 công

            // 4. Chuẩn bị chạy Audit (Test công thức)
            // Ngăn chặn các lệnh nguy hiểm (SQL Injection cơ bản cho Audit)
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
            params.addValue("standardDays", standardWorkingDays);

            // 5. Thực thi Query Audit
            Object result;
            try {
                // queryForObject dành cho việc lấy 1 giá trị duy nhất (Scalar)
                result = namedParameterJdbcTemplate.queryForObject(sqlToAudit, params, Object.class);
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body("Lỗi cú pháp hoặc dữ liệu khi chạy Audit: " + ex.getCause().getMessage());
            }

            // Trả về kết quả kèm thông tin kỳ lương để UI hiển thị ngữ cảnh
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