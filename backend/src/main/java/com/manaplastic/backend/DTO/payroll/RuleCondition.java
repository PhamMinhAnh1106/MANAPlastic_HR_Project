package com.manaplastic.backend.DTO.payroll;

import lombok.Data;

@Data
public class RuleCondition {
    private String field;    // Key từ UI: "SENIORITY", "JOB_TYPE", "GENDER"...
    private String operator; // Key từ UI: "GT" (>), "EQ" (=), "GTE" (>=)...
    private String value;    // Giá trị so sánh: "365", "DANGER", "1"...
}
