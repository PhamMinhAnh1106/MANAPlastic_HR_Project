package com.manaplastic.backend.payrollengine.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class PayrollEngineRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //Lấy danh sách các Rule đang ở trạng thái APPROVED
//    public List<Map<String, Object>> fetchApprovedRules() {
//        String sql = """
//                    SELECT r.rule_code, r.name, v.dsl_json
//                    FROM salary_rule r
//                    JOIN salary_rule_version v ON r.current_version_id = v.version_id
//                    WHERE r.status = 'APPROVED'
//                    ORDER BY r.priority ASC
//                """;
//        return jdbcTemplate.queryForList(sql);
//    }
    public List<Map<String, Object>> fetchApprovedRules() {
        String sql = """
            SELECT 
                r.rule_code, 
                r.name, 
                v.dsl_json,
                r.priority
            FROM salary_rule r 
            JOIN salary_rule_version v ON r.current_version_id = v.version_id 
            WHERE r.status = 'APPROVED' 
            ORDER BY r.priority ASC
        """;

        // Sử dụng queryForList để trả về List<Map>, dễ dàng loop trong Service
        return jdbcTemplate.queryForList(sql);
    }
    // Lưu cache cho Biến đầu vào (Variable Input)
    public void saveVariableInputToCache(String variableCode, BigDecimal value, Integer empId, String period) {
        String sql = """
                    INSERT INTO salary_variable_cache (rule_id, variable_id, employee_id, payperiod, value, evaluated_at)
                    VALUES (
                        NULL, 
                        (SELECT VariableID FROM salaryvariables WHERE Code = ? LIMIT 1), 
                        ?, ?, ?, NOW()
                    )
                    ON DUPLICATE KEY UPDATE value = VALUES(value), evaluated_at = NOW()
                """;
        // Lưu ý thứ tự tham số khớp với dấu ? trong SQL
        jdbcTemplate.update(sql, variableCode, empId, period, value);
    }

    // Lưu cache cho Kết quả tính toán (Rule Result)
    public void saveRuleResultToCache(String ruleCode, BigDecimal value, Integer empId, String period) {
        String sql = """
                    INSERT INTO salary_variable_cache (rule_id, variable_id, employee_id, payperiod, value, evaluated_at)
                    VALUES (
                        (SELECT rule_id FROM salary_rule WHERE rule_code = ? LIMIT 1), 
                        NULL, 
                        ?, ?, ?, NOW()
                    )
                    ON DUPLICATE KEY UPDATE value = VALUES(value), evaluated_at = NOW()
                """;
        jdbcTemplate.update(sql, ruleCode, empId, period, value);
    }

    // Lưu bảng lương cuối cùng (Payroll Final/Draft)
    public void saveFinalPayroll(Integer empId, String period, Map<String, BigDecimal> ctx) {
        String sql = """
                    INSERT INTO payrolls (
                        userID, payperiod, status,
                        netsalary, totalincome, 
                        pit, 
                        bhxh_emp, bhyt_emp, bhtn_emp,
                        bhxh_comp, bhyt_comp, bhtn_comp, 
                        basesalarysnapshot, insurancesalarysnapshot, 
                        standardworkdays, actualworkdays, othours, dependentcount
                    ) VALUES (
                        ?, ?, 'DRAFT',
                        ?, ?, 
                        ?, 
                        ?, ?, ?, 
                        ?, ?, ?, 
                        ?, ?, 
                        ?, ?, ?, ?
                    )
                    ON DUPLICATE KEY UPDATE
                        netsalary = VALUES(netsalary),
                        totalincome = VALUES(totalincome),
                        pit = VALUES(pit),
                        bhxh_emp = VALUES(bhxh_emp),
                        bhyt_emp = VALUES(bhyt_emp),
                        bhtn_emp = VALUES(bhtn_emp),
                
                        bhxh_comp = VALUES(bhxh_comp),
                        bhyt_comp = VALUES(bhyt_comp),
                        bhtn_comp = VALUES(bhtn_comp),
                
                        basesalarysnapshot = VALUES(basesalarysnapshot),
                        insurancesalarysnapshot = VALUES(insurancesalarysnapshot),
                        standardworkdays = VALUES(standardworkdays),
                        actualworkdays = VALUES(actualworkdays),
                        othours = VALUES(othours),
                        dependentcount = VALUES(dependentcount),
                        status = 'DRAFT'
                """;

        jdbcTemplate.update(sql,
                empId, period,
                // Các giá trị tính toán
                ctx.getOrDefault("NET_SALARY", BigDecimal.ZERO),
                ctx.getOrDefault("TOTAL_INCOME", BigDecimal.ZERO),
                ctx.getOrDefault("PIT_TAX", BigDecimal.ZERO),
                ctx.getOrDefault("BHXH_EMP", BigDecimal.ZERO),
                ctx.getOrDefault("BHYT_EMP", BigDecimal.ZERO),
                ctx.getOrDefault("BHTN_EMP", BigDecimal.ZERO),

                // Các giá trị công ty đóng
                ctx.getOrDefault("BHXH_COMP", BigDecimal.ZERO),
                ctx.getOrDefault("BHYT_COMP", BigDecimal.ZERO),
                ctx.getOrDefault("BHTN_COMP", BigDecimal.ZERO),

                // Các cột snapshot
                ctx.getOrDefault("BASE_SALARY", BigDecimal.ZERO),
                ctx.getOrDefault("INSURANCE_SALARY", BigDecimal.ZERO),
                ctx.getOrDefault("STD_DAYS", BigDecimal.ZERO),
                ctx.getOrDefault("REAL_WORK_DAYS", BigDecimal.ZERO),
                ctx.getOrDefault("TOTAL_OT_HOURS", BigDecimal.ZERO),
                ctx.getOrDefault("DEPENDENT_COUNT", BigDecimal.ZERO)
        );
    }
}