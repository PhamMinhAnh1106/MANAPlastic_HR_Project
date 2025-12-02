package com.manaplastic.backend.payrollengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SalaryRuleDTO implements Serializable {
    private String ruleCode;        // Mã quy tắc (VD: NET_SALARY)
    private String ruleName;        // Tên hiển thị
    private int priority;           // Thứ tự ưu tiên
    private ExpressionNode rootNode; // Cấu trúc công thức (Parsed từ JSON)

}
