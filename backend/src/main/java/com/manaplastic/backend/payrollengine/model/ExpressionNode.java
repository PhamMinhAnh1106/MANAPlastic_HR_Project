package com.manaplastic.backend.payrollengine.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các trường null khi xuất ra JSON
public class ExpressionNode {

    @JsonProperty("type")
    private NodeType type;

    // Dùng cho CONSTANT
    @JsonProperty("value")
    private BigDecimal value;

    // Dùng cho VARIABLE (tên biến) hoặc REFERENCE (mã rule)
    @JsonProperty("name")
    private String name;

    // Dùng cho phép toán 2 vế (Left + Right)
    @JsonProperty("left")
    private ExpressionNode left;

    @JsonProperty("right")
    private ExpressionNode right;

    // Dùng cho IF_ELSE: condition -> trueCase -> falseCase
    @JsonProperty("condition")
    private ExpressionNode condition;

    @JsonProperty("true_case")
    private ExpressionNode trueCase;

    @JsonProperty("false_case")
    private ExpressionNode falseCase;

    public ExpressionNode(NodeType type) {
        this.type = type;
    }
}