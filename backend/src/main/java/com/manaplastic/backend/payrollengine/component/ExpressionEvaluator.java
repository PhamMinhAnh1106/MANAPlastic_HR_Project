package com.manaplastic.backend.payrollengine.component;


import com.manaplastic.backend.payrollengine.model.ExpressionNode;
import com.manaplastic.backend.payrollengine.model.NodeType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class ExpressionEvaluator {

    public BigDecimal evaluate(ExpressionNode node, Map<String, BigDecimal> context) {
        if (node == null) return BigDecimal.ZERO;

        switch (node.getType()) {
            case CONSTANT:
                return node.getValue() != null ? node.getValue() : BigDecimal.ZERO;

            case VARIABLE:
            case REFERENCE:
                String varName = node.getName();
                BigDecimal val = context.getOrDefault(varName, BigDecimal.ZERO);
                return val;

            case ADD:
                return evaluate(node.getLeft(), context).add(evaluate(node.getRight(), context));

            case SUB:
                return evaluate(node.getLeft(), context).subtract(evaluate(node.getRight(), context));

            case MUL:
                return evaluate(node.getLeft(), context).multiply(evaluate(node.getRight(), context));

            case DIV:
                BigDecimal denominator = evaluate(node.getRight(), context);
                if (denominator.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO; // Tránh chia cho 0
                return evaluate(node.getLeft(), context).divide(denominator, 2, RoundingMode.HALF_UP);

            case IF_ELSE:
                // Nếu Condition > 0 thì coi là True
                BigDecimal condVal = evaluate(node.getCondition(), context);
                if (condVal.compareTo(BigDecimal.ZERO) > 0) {
                    return evaluate(node.getTrueCase(), context);
                } else {
                    return evaluate(node.getFalseCase(), context);
                }

            case GT: // Lớn hơn
                return evaluate(node.getLeft(), context).compareTo(evaluate(node.getRight(), context)) > 0
                        ? BigDecimal.ONE : BigDecimal.ZERO;
            case LT: // Nhỏ hơn
                return evaluate(node.getLeft(), context).compareTo(evaluate(node.getRight(), context)) < 0
                        ? BigDecimal.ONE : BigDecimal.ZERO;

            default:
                return BigDecimal.ZERO;
        }
    }
}
