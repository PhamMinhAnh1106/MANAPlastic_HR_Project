package com.manaplastic.backend.payrollengine.service;

import com.manaplastic.backend.DTO.payroll.PayrollDTO;
import com.manaplastic.backend.DTO.criteria.PayrollFilterCriteria;
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
        String sqlItems = """
                SELECT r.rule_code, r.name, svc.value
                FROM salary_variable_cache svc
                JOIN salary_rule r ON svc.rule_id = r.rule_id  -- <--- JOIN theo rule_id
                WHERE svc.employee_id = ?
                  AND svc.payperiod = ?
                  AND svc.rule_id IS NOT NULL -- Chỉ lấy kết quả Rule, không lấy Variable input
                ORDER BY r.priority ASC
                """;

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


    public Map<String, Object> getMyPayslipPDF(Integer userId, int month, int year) {
        String payPeriod = String.format("%d-%02d", year, month);
        Map<String, Object> payslip = new HashMap<>();

        // HEADER
        String sqlHeader = """
            SELECT 
                p.userID, u.username, p.payperiod, p.netsalary, p.totalincome,
                u.fullname, u.email, d.departmentname, u.jobtype as job_type 
            FROM payrolls p
            LEFT JOIN users u ON p.userID = u.userID
            LEFT JOIN departments d ON u.departmentID = d.departmentID
            WHERE p.userID = ? AND p.payperiod = ?
        """;

        try {
            payslip.put("header", jdbcTemplate.queryForMap(sqlHeader, userId, payPeriod));
        } catch (Exception e) {
            return null;
        }

        //  ITEMS (Đã lọc sạch các biến rác từ DB )
        String sqlItems = """
            SELECT 
                COALESCE(r.rule_code, v.Code) as code,
                COALESCE(r.name, v.Name, 'Điều chỉnh') as item_name,
                svc.value as item_value
                
            FROM salary_variable_cache svc
            LEFT JOIN salary_rule r ON svc.rule_id = r.rule_id
            LEFT JOIN salaryvariables v ON svc.variable_id = v.VariableID
            WHERE svc.employee_id = ? 
              AND svc.payperiod = ?
              AND svc.value != 0
              
              --  (BLACKLIST)
          
              AND (
                  -- Lấy code từ Rule hoặc Variable
                  COALESCE(r.rule_code, v.Code) NOT IN (
                      -- Các biến cấu hình hệ thống (Config)
                      'BASIC_SALARY_STATE',       -- Lương cơ sở nhà nước
                      'REGION_MIN_SALARY',        -- Lương tối thiểu vùng
                      'INSURANCE_CAP_MULTIPLIER', -- Hệ số trần bảo hiểm
                      'LUNCH_ALLOWANCE_LIMIT',    -- Giới hạn ăn ca miễn thuế
                      'STD_DAYS',                 -- Ngày công chuẩn (thường hiện ở header rồi)
                      
                      -- Các tỷ lệ phần trăm (Rates)
                      'RATE_BHXH_EMP', 'RATE_BHYT_EMP', 'RATE_BHTN_EMP', 
                      'RATE_BHXH_COMP', 'RATE_BHYT_COMP', 'RATE_BHTN_COMP',
                      'RATE_NIGHT_SHIFT',
                      
                      -- Các biến tính toán trung gian (Intermediate)
                      'HOURLY_MONEY',             -- Lương 1 giờ (quy đổi)
                      'TAX_EXEMPT_INCOME',        -- Thu nhập miễn thuế (ẩn đi cho gọn)
                      'TAXABLE_INCOME',        -- Thu nhập tính thuế (ẩn đi cho gọn)
                      'INSURANCE_AMT',            -- Tiền BH tổng (đã có chi tiết từng loại)
                      'FAMILY_DEDUCTION',         -- Tổng giảm trừ gia cảnh (đã hiện số người phụ thuộc)
                      'PERSONAL_DEDUCTION',       -- Mức giảm trừ bản thân (cố định 11tr)
                      'DEPENDENT_DEDUCTION'       -- Mức giảm trừ NPT (cố định 4.4tr)
                  )
                  
                  -- Loại bỏ các biến bắt đầu bằng từ khóa hệ thống
                  AND COALESCE(r.rule_code, v.Code) NOT LIKE 'TEMP_%'  -- Chặn biến tạm
              )
              
            ORDER BY svc.evaluated_at ASC
        """;

        List<Map<String, Object>> items = jdbcTemplate.queryForList(sqlItems, userId, payPeriod);
        payslip.put("items", items);

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

                .baseSalary(entity.getBasesalarysnapshot())
                .actualWorkDays(entity.getActualworkdays())
                .totalIncome(entity.getTotalincome())   // Tổng thu nhập
                .netSalary(entity.getNetsalary())       // Thực lĩnh

                .bhxhEmp(entity.getBhxhEmp())
                .bhytEmp(entity.getBhytEmp())
                .bhtnEmp(entity.getBhtnEmp())

                .bhxhComp(entity.getBhxhComp())
                .bhytComp(entity.getBhytComp())
                .bhtnComp(entity.getBhtnComp())

                .pit(entity.getPit())
                .build()
        );
    }
}