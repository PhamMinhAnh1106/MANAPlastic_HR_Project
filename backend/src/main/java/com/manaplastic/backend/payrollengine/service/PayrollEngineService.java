package com.manaplastic.backend.payrollengine.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.manaplastic.backend.payrollengine.component.ExpressionEvaluator;
import com.manaplastic.backend.payrollengine.component.PayrollDataFetcher;
import com.manaplastic.backend.payrollengine.model.ExpressionNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PayrollEngineService {

    @Autowired
    private PayrollDataFetcher dataFetcher;

    @Autowired
    private ExpressionEvaluator evaluator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper; // Dùng để parse JSON từ DB

    // Hàm chính: Tính lương cho 1 nhân viên trong 1 tháng

    @Transactional
    public void calculateSalaryForEmployee(Integer employeeId, int month, int year) {
        Map<String, BigDecimal> context = dataFetcher.fetchContext(employeeId, month, year);
        String payPeriod = year + "-" + (month < 10 ? "0" + month : month);

        System.out.println("--- BẮT ĐẦU TÍNH LƯƠNG CHO NV: " + employeeId + " ---");
        System.out.println("Dữ liệu thô: " + context);

        String sqlRules =
                "SELECT r.rule_code, r.name, v.dsl_json " +
                        "FROM salary_rule r " +
                        "JOIN salary_rule_version v ON r.current_version_id = v.version_id " +
                        "WHERE r.status = 'APPROVED' " +
                        "ORDER BY r.priority ASC";

        List<Map<String, Object>> rulesRaw = jdbcTemplate.queryForList(sqlRules);

        for (Map<String, Object> ruleRow : rulesRaw) {
            String ruleCode = (String) ruleRow.get("rule_code");
            String dslJson = (String) ruleRow.get("dsl_json");

            try {
                // Parse JSON thành ExpressionNode
                ExpressionNode rootNode = objectMapper.readValue(dslJson, ExpressionNode.class);

                // Tính toán
                BigDecimal result = evaluator.evaluate(rootNode, context);

                // Quan trọng: Đưa kết quả vừa tính vào Context để các rule sau dùng lại
                // Ví dụ: Tính xong TOTAL_INCOME thì đưa vào map để Rule TAX dùng
                context.put(ruleCode, result);

                // Lưu vào Cache (bảng salary_variable_cache) để truy vết sau này
                saveToCache(ruleCode, result, employeeId, payPeriod);

                System.out.println("-> Đã tính " + ruleCode + ": " + result);

            } catch (Exception e) {
                System.err.println("Lỗi khi tính rule " + ruleCode + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        //  Lưu kết quả cuối cùng vào bảng payrolls
        saveFinalPayroll(employeeId, payPeriod, context);
    }

    private void saveToCache(String ruleCode, BigDecimal value, Integer empId, String period) {
        // Xóa cũ insert mới (hoặc update)
        String sql = "INSERT INTO salary_variable_cache (variable_id, employee_id, payperiod, value, evaluated_at) " +
                "VALUES ((SELECT rule_id FROM salary_rule WHERE rule_code = ? LIMIT 1), ?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE value = VALUES(value), evaluated_at = NOW()";
        try {
            jdbcTemplate.update(sql, ruleCode, empId, period, value);
        } catch (Exception e) {
            // Có thể rule_code chưa map đúng ID, bỏ qua hoặc log
        }
    }

    private void saveFinalPayroll(Integer empId, String period, Map<String, BigDecimal> ctx) {
        String sql = "UPDATE payrolls SET " +
                "netsalary = ?, totalincome = ?, PIT = ?, basesalary = ?, " +
                "bhxh_emp = ?, bhyt_emp = ?, bhtn_emp = ? " +
                "WHERE userID = ? AND payperiod = ?";

        jdbcTemplate.update(sql,
                ctx.getOrDefault("NET_SALARY", BigDecimal.ZERO),
                ctx.getOrDefault("TOTAL_INCOME", BigDecimal.ZERO),
                ctx.getOrDefault("PIT_TAX", BigDecimal.ZERO),
                ctx.getOrDefault("BASE_SALARY", BigDecimal.ZERO),
                ctx.getOrDefault("BHXH_EMP", BigDecimal.ZERO),
                ctx.getOrDefault("BHYT_EMP", BigDecimal.ZERO),
                ctx.getOrDefault("BHTN_EMP", BigDecimal.ZERO),
                empId, period
        );
    }
}
