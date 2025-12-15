package com.manaplastic.backend.DTO.payroll;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VariableRuleRequest {
    // Các thông tin cơ bản để lưu vào bảng salaryvariables
    private String code;
    private String name;
    private String description;

    // --- Phần quan trọng: DSL JSON ---
    // Nếu mode = "BUILDER" thì dùng các field dưới để sinh SQL
    // Nếu mode = "RAW_SQL" thì dùng sqlQuery nhập tay (cho dev)
    private String mode; // "BUILDER" hoặc "RAW_SQL"

    // Logic: IF (field operator value) THEN trueValue ELSE falseValue
    private RuleCondition condition;
    private BigDecimal trueValue;
    private BigDecimal falseValue;
    private String sqlQuery; // Dùng khi mode = RAW_SQL
}

