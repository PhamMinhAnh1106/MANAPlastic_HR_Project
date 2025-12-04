package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.PayrollDTO;
import com.manaplastic.backend.DTO.PayrollFilterCriteria;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.payrollengine.service.PayrollEngineService;
import com.manaplastic.backend.repository.UserRepository; // Giả định bạn có repo này
// import com.manaplastic.backend.service.PayrollService; // Bỏ service cũ
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/calculate")
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> calculatePayroll(
            @RequestParam int month,
            @RequestParam int year) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi hệ thống: " + e.getMessage());
        }
    }


    @GetMapping("/debug-details") // TEST xem lương, công thức
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> getPayrollDebugDetails(
            @RequestParam int employeeId,
            @RequestParam int month,
            @RequestParam int year) {
        try {
            String payPeriod = year + "-" + (month < 10 ? "0" + month : month);

            String sql = """
                SELECT 
                    COALESCE(r.rule_code, v.Code) as code,
                    COALESCE(r.name, v.Name) as name,
                    svc.value,
                    svc.evaluated_at,
                    -- Lấy công thức từ Rule Version mới nhất
                    (SELECT dsl_json FROM salary_rule_version srv WHERE srv.version_id = r.current_version_id) as formula_dsl,
                    v.Description as input_desc,
                    v.SQLQuery as input_sql
                FROM salary_variable_cache svc
                LEFT JOIN salary_rule r ON svc.variable_id = r.rule_id
                LEFT JOIN salaryvariables v ON r.rule_code = v.Code OR (r.rule_id IS NULL AND svc.variable_id IS NULL) -- Logic join tạm thời
                WHERE svc.employee_id = ? AND svc.payperiod = ?
                ORDER BY svc.evaluated_at ASC
            """;

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, employeeId, payPeriod);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi debug: " + e.getMessage());
        }
    }
}