package com.manaplastic.backend.payrollengine.service;

import com.manaplastic.backend.DTO.PayrollDTO;
import com.manaplastic.backend.DTO.PayrollFilterCriteria;
import com.manaplastic.backend.entity.PayrollEntity;
import com.manaplastic.backend.filters.PayrollFilter;
import com.manaplastic.backend.repository.PayrollsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayslipService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PayrollsRepository payrollRepository;

   // Lấy phiếu lương chi tiết
    public Map<String, Object> getMyPayslip(Integer userId, int month, int year) {
        String payPeriod = String.format("%d-%02d", year, month);
        Map<String, Object> payslip = new HashMap<>();

        String sqlHeader = """
            SELECT p.*, 
                   u.fullname, 
                   u.username, 
                   u.jobtype, 
                   d.departmentname
            FROM payrolls p
            JOIN users u ON p.userID = u.userID
            LEFT JOIN departments d ON u.departmentID = d.departmentID
            WHERE p.userID = ? AND p.payperiod = ?
        """;

        List<Map<String, Object>> headerList = jdbcTemplate.queryForList(sqlHeader, userId, payPeriod);

        if (headerList.isEmpty()) {
            payslip.put("status", "ESTIMATED");
            // Nếu chưa có bảng lương, query thông tin user để hiển thị tạm
            String sqlUser = "SELECT u.fullname,u.username, u.jobtype, d.departmentname FROM users u LEFT JOIN departments d ON u.departmentID = d.departmentID WHERE u.userID = ?";
            try {
                Map<String, Object> userInfo = jdbcTemplate.queryForMap(sqlUser, userId);
                Map<String, Object> mockHeader = new HashMap<>(userInfo);
                mockHeader.put("payperiod", payPeriod);
                mockHeader.put("userID", userId);
                payslip.put("header", mockHeader);
            } catch (Exception e) {
                payslip.put("header", Map.of("payperiod", payPeriod, "userID", userId));
            }
        } else {
            payslip.put("header", headerList.get(0));
            payslip.put("status", "FINAL");
        }

        //  Lấy chi tiết các biến
        String sqlItems =
                "SELECT r.rule_code, r.name, svc.value " +
                        "FROM salary_variable_cache svc " +
                        "JOIN salary_rule r ON svc.variable_id = r.rule_id " +
                        "WHERE svc.employee_id = ? AND svc.payperiod = ? " +
                        "ORDER BY r.priority ASC";

        List<Map<String, Object>> items = jdbcTemplate.queryForList(sqlItems, userId, payPeriod);

        List<Map<String, Object>> incomes = new ArrayList<>();
        List<Map<String, Object>> deductions = new ArrayList<>();
        List<Map<String, Object>> companyCosts = new ArrayList<>();
        BigDecimal netSalary = BigDecimal.ZERO;

        for (Map<String, Object> item : items) {
            String code = (String) item.get("rule_code");
            BigDecimal value = (BigDecimal) item.get("value");

            if (value.compareTo(BigDecimal.ZERO) == 0) continue;

            if ("NET_SALARY".equals(code)) {
                netSalary = value;
            } else if (code.endsWith("_COMP")) {
                companyCosts.add(item);
            } else if (code.endsWith("_EMP") || code.contains("TAX") || code.contains("PENALTY") || code.contains("DEDUCT")) {
                deductions.add(item);
            } else if (code.startsWith("TOTAL_") || code.endsWith("_SALARY") || code.endsWith("_DAYS") || code.equals("OT_TAX_EXEMPT")) {
                // Giữ lại các biến quan trọng nếu muốn hiển thị (như Lương ngày công), còn lại ẩn
                if (code.equals("WORK_SALARY")) incomes.add(item);
            } else {
                incomes.add(item);
            }
        }

        payslip.put("incomes", incomes);
        payslip.put("deductions", deductions);
        payslip.put("company_contributions", companyCosts);
        payslip.put("net_salary", netSalary);

    // chi tiết thưởng phạt
        String startDate = year + "-" + month + "-01";
        String endDate = year + "-" + month + "-31";
        String sqlDetails = "SELECT Type, Reason, Amount, DecisionDate FROM rewardpunishmentdecisions WHERE UserID = ? AND Status IN ('APPROVED', 'PROCESSED') AND DecisionDate BETWEEN ? AND ?";
        payslip.put("explanations", jdbcTemplate.queryForList(sqlDetails, userId, startDate, endDate));

        return payslip;
    }

    public Page<PayrollDTO> getPayrollList(PayrollFilterCriteria criteria, Pageable pageable) {
        Specification<PayrollEntity> spec = PayrollFilter.filterBy(criteria);
        Page<PayrollEntity> pageResult = payrollRepository.findAll(spec, pageable);
        return pageResult.map(entity -> PayrollDTO.builder()

                .userId(entity.getUserID().getId())
                .fullName(entity.getUserID().getFullname())
                .departmentName(entity.getUserID().getDepartmentID() != null ? entity.getUserID().getDepartmentID().getDepartmentname() : "")
                .jobType(entity.getUserID().getJobtype())
                .payPeriod(entity.getPayperiod())

                .baseSalary(entity.getBasesalary())
                .actualWorkDays(entity.getActualworkdays())
                .totalIncome(entity.getTotalincome())   // Tổng thu nhập
                .netSalary(entity.getNetsalary())       // Thực lĩnh

                .totalAllowance(entity.getTotalallowance())
                .totalOvertimePay(entity.getTotalovertimepay())
                .pit(entity.getPit())
                .build()
        );
    }
}