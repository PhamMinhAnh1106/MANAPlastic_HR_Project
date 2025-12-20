package com.manaplastic.backend.controller.payroll;

import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.payrollengine.service.PayrollEngineService;
import com.manaplastic.backend.repository.UserRepository; // Giả định bạn có repo này
// import com.manaplastic.backend.service.PayrollService; // Bỏ service cũ
import com.manaplastic.backend.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payroll")
@CrossOrigin(origins = "*")
public class PayrollController {

    @Autowired
    private PayrollEngineService payrollEngineService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PdfService pdfService;


    @PostMapping("/calculate")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @LogActivity(action ="PAYROLL_CALCULATE", description = "Chạy tính lương")
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<?> calculatePayroll(
            @RequestParam int month,
            @RequestParam int year) {
            if (month < 1 || month > 12) {
                return ResponseEntity.badRequest().body("Tháng không hợp lệ!");
            }

//            List<UserEntity> employees = userRepository.findAll(); // TEST
            List<UserEntity> employees = userRepository.findAllActiveUsers();
            int successCount = 0;
            StringBuilder errors = new StringBuilder();
            for (UserEntity emp : employees) {
                try {
                    payrollEngineService.calculateSalaryForEmployee(emp.getId(), month, year);
                    successCount++;
                } catch (Exception e) {
                    errors.append("Lỗi NV ID ").append(emp.getId()).append(": ").append(e.getMessage()).append("; ");
                }
            }

            String message = "Đã hoàn thành tính lương. Thành công: " + successCount + "/" + employees.size();
            if (errors.length() > 0) {
                message += ". Chi tiết lỗi: " + errors.toString();
            }
            return ResponseEntity.ok().body(message);
    }


    // TEST xem lương, công thức
    @GetMapping("/debug-details")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @RequiredPermission(PermissionConst.PAYROLL_VIEW_ALL)
    public ResponseEntity<?> getPayrollDebugDetails(
            @RequestParam int employeeId,
            @RequestParam int month,
            @RequestParam int year) {

        String payPeriod = year + "-" + (month < 10 ? "0" + month : month);

        String sql = """
                    SELECT 
                        -- Xác định Code: Ưu tiên Rule Code, nếu null thì lấy Variable Code
                        CASE 
                            WHEN svc.rule_id IS NOT NULL THEN r.rule_code
                            WHEN svc.variable_id IS NOT NULL THEN v.Code
                            ELSE 'UNKNOWN'
                        END as code,
                
                        -- Xác định Name
                        CASE 
                            WHEN svc.rule_id IS NOT NULL THEN r.name
                            WHEN svc.variable_id IS NOT NULL THEN v.Name
                            ELSE 'Không xác định'
                        END as name,
                
                        svc.value,
                        svc.evaluated_at,
                
                        -- Lấy công thức DSL (Chỉ có nếu là Rule)
                        (SELECT dsl_json FROM salary_rule_version srv WHERE srv.version_id = r.current_version_id) as formula_dsl,
                
                        -- Lấy mô tả/SQL gốc (Chỉ có nếu là Variable)
                        v.Description as input_desc,
                        v.SQLQuery as input_sql
                
                    FROM salary_variable_cache svc
                    -- Join Bảng Rule nếu dòng cache này lưu Rule
                    LEFT JOIN salary_rule r ON svc.rule_id = r.rule_id
                    -- Join Bảng Variable nếu dòng cache này lưu Variable
                    LEFT JOIN salaryvariables v ON svc.variable_id = v.VariableID
                
                    WHERE svc.employee_id = ? 
                      AND svc.payperiod = ?
                    ORDER BY svc.evaluated_at ASC, svc.cache_id ASC
                """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, employeeId, payPeriod);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/finalize")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @LogActivity(action = "FINALIZE_PAYROLL", description = "Chốt lương tháng", logType = LogType.DANGER)
     @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<?> finalizePayroll(@RequestParam int month, @RequestParam int year) {
        try {
            payrollEngineService.finalizePayrollAndSendMail(month, year);
            return ResponseEntity.ok("Đã chốt lương và bắt đầu tiến trình gửi email.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi chốt lương: " + e.getMessage());
        }
    }
}