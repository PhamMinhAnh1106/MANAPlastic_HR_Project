package com.manaplastic.backend.payrollengine.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PayrollDataFetcher {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    public Map<String, BigDecimal> fetchContext(Integer employeeId, int month, int year) {
        Map<String, BigDecimal> context = new HashMap<>();

        String sqlConfig = "SELECT CycleStartDate, CycleEndDate, StandardWorkDays FROM monthlypayrollconfigs WHERE Month = :month AND Year = :year";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("month", month);
        params.addValue("year", year);
        params.addValue("userId", employeeId);

        try {
            Map<String, Object> config = namedJdbcTemplate.queryForMap(sqlConfig, params);

            Date startDateSql = (Date) config.get("CycleStartDate");
            Date endDateSql = (Date) config.get("CycleEndDate");

            // Chuyển đổi an toàn cho StandardWorkDays
            BigDecimal stdDays = convertToBigDecimal(config.get("StandardWorkDays"));

            params.addValue("startDate", startDateSql);
            params.addValue("endDate", endDateSql);
            params.addValue("stdDays", stdDays);

            context.put("STD_DAYS", stdDays);

        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Lỗi: Chưa cấu hình kỳ lương cho tháng " + month + "/" + year);
        }

        String sqlGetVariables = "SELECT Code, SQLQuery FROM salaryvariables WHERE SQLQuery IS NOT NULL AND SQLQuery != ''";

        List<Map<String, Object>> variables = namedJdbcTemplate.queryForList(sqlGetVariables, new MapSqlParameterSource());

        for (Map<String, Object> varConfig : variables) {
            String code = (String) varConfig.get("Code");
            String query = (String) varConfig.get("SQLQuery");

            BigDecimal value = BigDecimal.ZERO;

            try {
                // Lấy về Object trước (để hứng cả Long, Double, Integer, BigDecimal)
                // queryForObject(..., BigDecimal.class) rất dễ lỗi nếu DB trả về Long (COUNT)
                Object rawResult = namedJdbcTemplate.queryForObject(query, params, Object.class);

                value = convertToBigDecimal(rawResult);

            } catch (EmptyResultDataAccessException e) {
                value = BigDecimal.ZERO; // Không có dữ liệu -> 0
            } catch (Exception e) {
                System.err.println("!!! Lỗi tính biến [" + code + "]: " + e.getMessage());
                value = BigDecimal.ZERO; // Lỗi cú pháp -> 0 (để không crash luồng tính lương)
            }

            context.put(code, value);
        }

        return context;
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;

        BigDecimal result;
        if (value instanceof BigDecimal) {
            result = (BigDecimal) value;
        } else if (value instanceof Long) {
            result = BigDecimal.valueOf((Long) value); // Xử lý cho hàm COUNT(*)
        } else if (value instanceof Integer) {
            result = BigDecimal.valueOf((Integer) value);
        } else if (value instanceof Double) {
            result = BigDecimal.valueOf((Double) value); // Xử lý cho hàm SUM(float)
        } else if (value instanceof Float) {
            result = BigDecimal.valueOf((Float) value);
        } else {
            // Trường hợp chuỗi hoặc dạng lạ
            try {
                result = new BigDecimal(value.toString());
            } catch (NumberFormatException e) {
                result = BigDecimal.ZERO;
            }
        }

        // Luôn làm tròn 4 chữ số thập phân để tránh sai số (VD: 10.33333 -> 10.3333)
        return result.setScale(4, RoundingMode.HALF_UP);
    }
}