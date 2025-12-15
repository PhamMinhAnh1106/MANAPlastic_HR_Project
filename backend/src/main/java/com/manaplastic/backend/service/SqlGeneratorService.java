package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.payroll.RuleCondition;
import com.manaplastic.backend.DTO.payroll.VariableRuleRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SqlGeneratorService {

    // CẤU HÌNH MAPPING
    // Key: Tên cột trong VIEW (v_employee_profile_flat) -> Dùng để sinh SQL
    // Value: Tên hiển thị trên UI (Tiếng Việt) -> Dùng để trả về cho Dropdown
    private static final Map<String, String> FIELD_MAPPING = new LinkedHashMap<>();
    static {
        // --- Nhóm Thời gian / Thâm niên ---
        FIELD_MAPPING.put("SENIORITY_DAYS", "Thâm niên (Ngày)");
        FIELD_MAPPING.put("SENIORITY_MONTHS", "Thâm niên (Tháng)");
        FIELD_MAPPING.put("AGE", "Tuổi đời");

        // --- Nhóm Hợp đồng / Lương (Lấy từ View, không cần join tay nữa) ---
        FIELD_MAPPING.put("BASE_SALARY", "Lương cơ bản (HĐ)");
        FIELD_MAPPING.put("CONTRACT_TYPE", "Loại hợp đồng");
        FIELD_MAPPING.put("TOXIC_TYPE", "Loại độc hại");
        FIELD_MAPPING.put("INSURANCE_BASE", "Lương đóng BHXH");

        // --- Nhóm Tổ chức ---
        FIELD_MAPPING.put("DEPT_NAME", "Tên phòng ban");
        FIELD_MAPPING.put("IS_OFFICE", "Là khối văn phòng?"); // 1 hoặc 0
        FIELD_MAPPING.put("ROLE_NAME", "Chức vụ");

        // --- Nhóm Thông tin cá nhân ---
        FIELD_MAPPING.put("GENDER", "Giới tính");     // 'MALE', 'FEMALE'
        FIELD_MAPPING.put("JOB_TYPE", "Hình thức công việc"); // 'NORMAL', 'DANGER'
        FIELD_MAPPING.put("SKILL_GRADE", "Bậc thợ");
        FIELD_MAPPING.put("DEPENDENT_COUNT", "Số người phụ thuộc");
    }

    // MAPPING TOÁN TỬ
    private static final Map<String, String> OPERATOR_MAPPING = Map.of(
            "GT", ">",
            "GTE", ">=",
            "LT", "<",
            "LTE", "<=",
            "EQ", "=",
            "NEQ", "!="
    );

    public Map<String, String> getAvailableFields() {
        return FIELD_MAPPING;
    }


    public String generateSql(VariableRuleRequest request) {
        // Chế độ RAW SQL (Cho Developer/Admin nhập tay)
        if ("RAW_SQL".equalsIgnoreCase(request.getMode())) {
            return request.getSqlQuery();
        }

        // Chế độ BUILDER (Kéo thả)
        RuleCondition cond = request.getCondition();
        validateCondition(cond);

        // Lấy thông tin để ghép chuỗi
        // Lưu ý: Lấy cond.getField() làm tên cột (VD: SENIORITY_DAYS)
        String dbColumn = cond.getField();
        String dbOperator = OPERATOR_MAPPING.get(cond.getOperator());
        String dbValue = sanitizeValue(cond.getValue()); // Xử lý số hoặc chuỗi

        // Sinh câu SQL chuẩn trỏ vào VIEW
        // SELECT CASE WHEN [COLUMN] [OP] [VALUE] THEN [TRUE] ELSE [FALSE] END FROM v_employee_profile_flat ...
        return String.format(
                "SELECT CASE WHEN %s %s %s THEN %s ELSE %s END FROM v_employee_profile_flat WHERE userID = :userId",
                dbColumn,
                dbOperator,
                dbValue,
                request.getTrueValue(),  // BigDecimal tự toString() thành số
                request.getFalseValue()
        );
    }


    private void validateCondition(RuleCondition cond) {
        if (cond == null) {
            throw new IllegalArgumentException("Điều kiện không được để trống trong chế độ Builder");
        }
        // Kiểm tra xem Field gửi lên có nằm trong Whitelist không (Chống SQL Injection)
        if (!FIELD_MAPPING.containsKey(cond.getField())) {
            throw new IllegalArgumentException("Trường dữ liệu không hợp lệ: " + cond.getField());
        }
        // Kiểm tra toán tử
        if (!OPERATOR_MAPPING.containsKey(cond.getOperator())) {
            throw new IllegalArgumentException("Toán tử không hợp lệ: " + cond.getOperator());
        }
    }

    private String sanitizeValue(String value) {
        if (value == null) return "''";

        // Nếu là số (nguyên hoặc thập phân) -> Giữ nguyên
        if (value.matches("-?\\d+(\\.\\d+)?")) {
            return value;
        }

        // Nếu là chuỗi (VD: 'MALE', 'NORMAL') -> Thêm dấu nháy đơn
        // Escape dấu nháy đơn bên trong nếu có để tránh lỗi SQL
        return "'" + value.replace("'", "''") + "'";
    }
}