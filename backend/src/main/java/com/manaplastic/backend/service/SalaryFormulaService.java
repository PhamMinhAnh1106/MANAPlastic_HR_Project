//package com.manaplastic.backend.service;
//
//import com.manaplastic.backend.entity.SalaryformulaEntity;
//import com.manaplastic.backend.repository.SalaryFormulaRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.expression.Expression;
//import org.springframework.expression.ExpressionParser;
//import org.springframework.expression.spel.standard.SpelExpressionParser;
//import org.springframework.expression.spel.support.StandardEvaluationContext;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Service
//public class SalaryFormulaService {
//
//    @Autowired
//    private SalaryFormulaRepository formulaRepo;
//
//    private final ExpressionParser parser = new SpelExpressionParser();
//
//    public Map<String, BigDecimal> calculateDynamicPayroll(Map<String, BigDecimal> context) {
//        List<SalaryformulaEntity> formulas = formulaRepo.findAllByIsActiveTrueOrderByPriorityAsc();
//
//        for (SalaryformulaEntity formula : formulas) {
//            String code = formula.getCode();
//            String rawCalculation = formula.getCalculation();
//
//            try {
//                // 1. Thay biến [VAR] bằng giá trị số
//                String evalExpression = replaceVariables(rawCalculation, context);
//
//                // 2. Tính toán (Sử dụng MathMethods làm Root Object)
//                BigDecimal result = evaluateExpression(evalExpression);
//
//                context.put(code, result);
//            } catch (Exception e) {
//                System.err.println("!!! Lỗi tính công thức [" + code + "]: " + e.getMessage());
//                context.put(code, BigDecimal.ZERO);
//            }
//        }
//        return context;
//    }
//
//    private String replaceVariables(String expression, Map<String, BigDecimal> context) {
//        if (expression == null || expression.isEmpty()) return "0";
//        Pattern pattern = Pattern.compile("\\[([a-zA-Z0-9_]+)\\]");
//        Matcher matcher = pattern.matcher(expression);
//        StringBuilder sb = new StringBuilder();
//        while (matcher.find()) {
//            String varKey = matcher.group(1);
//            BigDecimal value = context.getOrDefault(varKey, BigDecimal.ZERO);
//            matcher.appendReplacement(sb, value.toPlainString());
//        }
//        matcher.appendTail(sb);
//        return sb.toString();
//    }
//
//    // --- PHẦN SỬA LỖI ---
//    private BigDecimal evaluateExpression(String expressionString) {
//        try {
//            // 1. Tạo Context và set Root Object là class MathMethods chứa các hàm min, max...
//            // Điều này cho phép gọi min(a,b) trực tiếp mà không cần dấu #
//            StandardEvaluationContext evalContext = new StandardEvaluationContext(new MathMethods());
//
//            Expression exp = parser.parseExpression(expressionString);
//
//            // 2. Tính toán (SpEL sẽ tự ép kiểu int/double)
//            Double value = exp.getValue(evalContext, Double.class);
//
//            if (value == null || Double.isInfinite(value) || Double.isNaN(value)) {
//                return BigDecimal.ZERO;
//            }
//
//            return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
//        } catch (Exception e) {
//            System.err.println("Lỗi SpEL Parse: " + expressionString + " -> " + e.getMessage());
//            return BigDecimal.ZERO;
//        }
//    }
//
//    // --- CLASS BAO ĐÓNG CÁC HÀM TOÁN HỌC ---
//    // Class này phải là public static để SpEL truy cập được
//    public static class MathMethods {
//        // Cần định nghĩa nhận double để bao quát cả int và long
//        public double min(double a, double b) { return Math.min(a, b); }
//        public double max(double a, double b) { return Math.max(a, b); }
//        public double abs(double a) { return Math.abs(a); }
//        public double round(double a) { return Math.round(a); }
//        public double ceil(double a) { return Math.ceil(a); }
//        public double floor(double a) { return Math.floor(a); }
//        public double sqrt(double a) { return Math.sqrt(a); }
//    }
//}