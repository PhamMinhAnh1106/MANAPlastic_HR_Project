package com.manaplastic.backend.controller.payroll;

import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.service.SalaryRuleAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/payEngine/rules")
@CrossOrigin(origins = "*")
public class AdminRuleController {

    @Autowired
    private SalaryRuleAdminService adminService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    public ResponseEntity<?> getAllRules() {
        // Ép kiểu created_at sang String (DATE_FORMAT) để tránh lỗi Java Time
        String sql = """
            SELECT 
                r.rule_id as ruleId, 
                r.rule_code as ruleCode, 
                r.name as name, 
                r.status as status, 
                r.priority as priority,
                v.dsl_json as dslJson,
                DATE_FORMAT(r.created_at, '%Y-%m-%d %H:%i:%s') as createdAt
            FROM salary_rule r 
            LEFT JOIN salary_rule_version v ON r.current_version_id = v.version_id
            ORDER BY r.priority ASC, r.rule_id ASC
        """;

        List<Map<String, Object>> rules = jdbcTemplate.queryForList(sql);
        return ResponseEntity.ok(rules);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @LogActivity(action="CREATE_RULE_SALARY", description = "Thêm mới công thức tính lương")
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<?> saveRule(@RequestBody Map<String, String> payload) {
        try {
            String code = payload.get("code");
            String name = payload.get("name");
            String dsl = payload.get("dsl");

            adminService.saveRule(code, name, dsl);
            return ResponseEntity.ok("Lưu công thức thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @LogActivity(action="DELETE_RULE_SALARY", description = "Xóa (ẩn) công thức tính lương", logType = LogType.DANGER)
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<?> deleteRule(@PathVariable int id) {
        adminService.deleteRule(id);
        return ResponseEntity.ok("Đã ẩn công thức này.");
    }
}