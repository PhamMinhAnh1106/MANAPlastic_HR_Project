package com.manaplastic.backend.payrollengine.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PayrollDataFetcher {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Lấy toàn bộ dữ liệu đầu vào cho 1 nhân viên để chạy công thức.

    public Map<String, BigDecimal> fetchContext(Integer employeeId, int month, int year) {
        Map<String, BigDecimal> context = new HashMap<>();

        // Lấy cấu hình chu kỳ lương (Ngày bắt đầu, ngày kết thúc, ngày công chuẩn) - Bảng: monthlypayrollconfigs
        String sqlConfig = "SELECT CycleStartDate, CycleEndDate, StandardWorkDays FROM monthlypayrollconfigs WHERE Month = ? AND Year = ?";
        Map<String, Object> config;
        try {
            config = jdbcTemplate.queryForMap(sqlConfig, month, year);
        } catch (Exception e) {
            throw new RuntimeException("Chưa cấu hình kỳ lương cho tháng " + month + "/" + year);
        }

        LocalDate startDate = ((java.sql.Date) config.get("CycleStartDate")).toLocalDate();
        LocalDate endDate = ((java.sql.Date) config.get("CycleEndDate")).toLocalDate();
        context.put("STD_DAYS", convertToBigDecimal(config.get("StandardWorkDays")));

        //  Lấy Lương cơ bản & Lương đóng bảo hiểm - Bảng: contracts
        String sqlContract = "SELECT basesalary, InsuranceSalary FROM contracts WHERE userID = ? AND Status = 'ACTIVE' LIMIT 1";
        try {
            Map<String, Object> contract = jdbcTemplate.queryForMap(sqlContract, employeeId);
            context.put("BASE_SALARY", convertToBigDecimal(contract.get("basesalary")));
            context.put("INSURANCE_SALARY", convertToBigDecimal(contract.get("InsuranceSalary")));
        } catch (Exception e) {
            context.put("BASE_SALARY", BigDecimal.ZERO);
            context.put("INSURANCE_SALARY", BigDecimal.ZERO);
        }

        // Tính Tổng phụ cấp (Lấy từ bảng con contractallowances) - Bảng: contractallowances JOIN contracts
        String sqlAllowance = "SELECT SUM(ca.Amount) FROM contractallowances ca " +
                "JOIN contracts c ON ca.ContractID = c.contractID " +
                "WHERE c.userID = ? AND c.Status = 'ACTIVE'";
        BigDecimal totalAllowance = jdbcTemplate.queryForObject(sqlAllowance, BigDecimal.class, employeeId);
        context.put("TOTAL_ALLOWANCE", totalAllowance != null ? totalAllowance : BigDecimal.ZERO);

        // Tính Tổng phụ cấp ăn trưa (để tính miễn thuế) - Type = 'MEAL'
        String sqlMeal = "SELECT SUM(ca.TaxFreeAmount) FROM contractallowances ca " +
                "JOIN contracts c ON ca.ContractID = c.contractID " +
                "WHERE c.userID = ? AND c.Status = 'ACTIVE' AND ca.AllowanceType = 'MEAL'";
        BigDecimal mealAllowance = jdbcTemplate.queryForObject(sqlMeal, BigDecimal.class, employeeId);
        context.put("MEAL_ALLOWANCE_TAX_FREE", mealAllowance != null ? mealAllowance : BigDecimal.ZERO);


        // Đếm ngày công thực tế (Status = 'PRESENT')- Bảng: attendances
        String sqlAttendance = "SELECT COUNT(*) FROM attendances " +
                "WHERE userID = ? AND status = 'PRESENT' AND date BETWEEN ? AND ?";
        Integer workDays = jdbcTemplate.queryForObject(sqlAttendance, Integer.class, employeeId, startDate, endDate);
        context.put("REAL_WORK_DAYS", new BigDecimal(workDays != null ? workDays : 0));

        //  Tính tổng giờ tăng ca quy đổi (Giờ * Hệ số) - Bảng: overtime JOIN overtimetypes
        // Lưu ý: Chỉ lấy đơn đã duyệt (status = 'APPROVED')
        String sqlOt = "SELECT SUM(o.hours * ot.Rate) FROM overtime o " +
                "JOIN overtimetypes ot ON o.overtimetypeid = ot.OvertimeTypeID " +
                "WHERE o.userid = ? AND o.status = 'APPROVED' AND o.date BETWEEN ? AND ?";
        BigDecimal totalOtHours = jdbcTemplate.queryForObject(sqlOt, BigDecimal.class, employeeId, startDate, endDate);
        context.put("TOTAL_OT_HOURS_CONVERTED", totalOtHours != null ? totalOtHours : BigDecimal.ZERO);

        // Thưởng và Phạt - Bảng: rewardpunishmentdecisions
        String sqlReward = "SELECT SUM(Amount) FROM rewardpunishmentdecisions " +
                "WHERE UserID = ? AND Type = 'REWARD' AND Status IN ('APPROVED','PROCESSED') " +
                "AND DecisionDate BETWEEN ? AND ?";
        BigDecimal rewards = jdbcTemplate.queryForObject(sqlReward, BigDecimal.class, employeeId, startDate, endDate);
        context.put("TOTAL_REWARD", rewards != null ? rewards : BigDecimal.ZERO);

        String sqlPenalty = "SELECT SUM(Amount) FROM rewardpunishmentdecisions " +
                "WHERE UserID = ? AND Type = 'PUNISHMENT' AND Status IN ('APPROVED','PROCESSED') " +
                "AND DecisionDate BETWEEN ? AND ?";
        BigDecimal penalties = jdbcTemplate.queryForObject(sqlPenalty, BigDecimal.class, employeeId, startDate, endDate);
        context.put("TOTAL_PENALTY", penalties != null ? penalties : BigDecimal.ZERO);

        // Số người phụ thuộc (để tính thuế) - Bảng: dependents (istaxdeductible = 1)
        String sqlDependents = "SELECT COUNT(*) FROM dependents WHERE userID = ? AND istaxdeductible = 1";
        Integer depCount = jdbcTemplate.queryForObject(sqlDependents, Integer.class, employeeId);
        context.put("DEPENDENT_COUNT", new BigDecimal(depCount != null ? depCount : 0));

        // Các biến hệ thống (Global Settings ) - Bảng: taxsettings
        // Lấy tất cả setting đang active
        String sqlSettings = "SELECT SettingKey, Value FROM taxsettings WHERE IsActive = 1";
        List<Map<String, Object>> settings = jdbcTemplate.queryForList(sqlSettings);
        for (Map<String, Object> s : settings) {
            String key = (String) s.get("SettingKey");
            BigDecimal val = convertToBigDecimal(s.get("Value"));
            context.put(key, val);
        }

        // Bảng: insurancesettings (Lấy tỷ lệ đóng BHXH của nhân viên)
        try {
            String sqlBhxh = "SELECT EmployeeRate FROM insurancesettings WHERE Type = 'SOCIAL' AND IsActive = 1 LIMIT 1";
            BigDecimal bhxhRate = jdbcTemplate.queryForObject(sqlBhxh, BigDecimal.class);
            context.put("RATE_BHXH", bhxhRate != null ? bhxhRate.divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO);
        } catch(Exception e) { context.put("RATE_BHXH", BigDecimal.valueOf(0.08)); } // Mặc định 8%

        return context;
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        return new BigDecimal(value.toString());
    }
}
