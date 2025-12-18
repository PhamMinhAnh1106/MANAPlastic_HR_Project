package com.manaplastic.backend.payrollengine.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.manaplastic.backend.payrollengine.component.ExpressionEvaluator;
import com.manaplastic.backend.payrollengine.component.PayrollDataFetcher;
import com.manaplastic.backend.payrollengine.model.ExpressionNode;
import com.manaplastic.backend.payrollengine.repository.PayrollEngineRepository;
import com.manaplastic.backend.repository.UserRepository;
import com.manaplastic.backend.service.EmailService;
import com.manaplastic.backend.service.PdfService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PayrollEngineService {

    @Autowired
    private PayrollDataFetcher dataFetcher;
    @Autowired
    private ExpressionEvaluator evaluator;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PayrollEngineRepository payrollRepository;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private PayslipService payslipService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ObjectMapper objectMapper; // Dùng để parse JSON từ DB

    // Tính lương cho 1 nhân viên trong 1 tháng
    @Transactional
    public void calculateSalaryForEmployee(Integer employeeId, int month, int year) {

        Map<String, BigDecimal> context = dataFetcher.fetchContext(employeeId, month, year);
        String payPeriod = String.format("%d-%02d", year, month); // Format chuẩn yyyy-MM

        System.out.println("--- BẮT ĐẦU TÍNH LƯƠNG CHO NV: " + employeeId + " ---");

        for (Map.Entry<String, BigDecimal> entry : context.entrySet()) {
            payrollRepository.saveVariableInputToCache(entry.getKey(), entry.getValue(), employeeId, payPeriod);
        }

        List<Map<String, Object>> rulesRaw = payrollRepository.fetchApprovedRules();

        for (Map<String, Object> ruleRow : rulesRaw) {
            String ruleCode = (String) ruleRow.get("rule_code");
            String dslJson = (String) ruleRow.get("dsl_json");

            try {
                // Parse cây biểu thức
                ExpressionNode rootNode = objectMapper.readValue(dslJson, ExpressionNode.class);

                // Tính toán giá trị
                BigDecimal result = evaluator.evaluate(rootNode, context);
                context.put(ruleCode, result);

                // Cache kết quả tính toán
                payrollRepository.saveRuleResultToCache(ruleCode, result, employeeId, payPeriod);
                System.out.println("-> Đã tính " + ruleCode + ": " + result);
            } catch (Exception e) {
                System.err.println("Lỗi tính rule " + ruleCode + ": " + e.getMessage());
            }
        }

        payrollRepository.saveFinalPayroll(employeeId, payPeriod, context);

        System.out.println("--- HOÀN TẤT TÍNH LƯƠNG CHO NV: " + employeeId + " ---");
    }

    @Transactional
    public void finalizePayrollAndSendMail(int month, int year) {
        String payPeriod = String.format("%d-%02d", year, month);

        // Cập nhật trạng thái FINAL
        String updateSql = "UPDATE payrolls SET status = 'FINAL' WHERE payperiod = ? AND status = 'DRAFT'";
        jdbcTemplate.update(updateSql, payPeriod);

        // Lấy danh sách nhân viên cần gửi mail
        // Chỉ lấy những người thực sự có bản ghi lương trong tháng này
        String selectUsersSql = "SELECT p.userID, u.email, u.fullname " +
                "FROM payrolls p " +
                "JOIN users u ON p.userID = u.userID " +
                "WHERE p.payperiod = ?";

        List<Map<String, Object>> users = jdbcTemplate.queryForList(selectUsersSql, payPeriod);

        System.out.println("Tìm thấy " + users.size() + " nhân viên để gửi mail.");

        // Duyệt và gửi mail
        for (Map<String, Object> user : users) {
            Integer userId = (Integer) user.get("userID");
            String email = (String) user.get("email");
            String fullname = (String) user.get("fullname");

            try {

                Map<String, Object> payslipData = payslipService.getMyPayslipPDF(userId, month, year);

                if (payslipData == null) {
                    System.err.println("WARN: Không tìm thấy dữ liệu lương chi tiết cho UserID: " + userId + ". Bỏ qua.");
                    continue; // Bỏ qua vòng lặp này, chạy tiếp người sau
                }

                // Tạo PDF
                byte[] pdfBytes = pdfService.generatePayslipPdf(payslipData);

                if (pdfBytes != null) {
                    emailService.sendPayslipEmail(email, fullname, payPeriod, pdfBytes);
                }
            } catch (Exception e) {
                System.err.println("Lỗi gửi mail cho " + fullname + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

    }
}
