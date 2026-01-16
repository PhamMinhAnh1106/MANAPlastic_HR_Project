package com.manaplastic.backend.payrollengine.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.manaplastic.backend.DTO.payroll.PayrollDetailDTO;
import com.manaplastic.backend.entity.PayrollEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.payrollengine.component.ExpressionEvaluator;
import com.manaplastic.backend.payrollengine.component.PayrollDataFetcher;
import com.manaplastic.backend.payrollengine.model.ExpressionNode;
import com.manaplastic.backend.payrollengine.repository.PayrollEngineRepository;
import com.manaplastic.backend.repository.PayrollsRepository;
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
    @Autowired private PayrollsRepository payrollsJPARepo;

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
    public void finalizePayrollAndSendMail(int month, int year) { // ĐANG LỖI HEADDER cho PDF
        String payPeriod = String.format("%d-%02d", year, month);
        System.out.println(">>> BẮT ĐẦU CHỐT LƯƠNG & GỬI MAIL KỲ: " + payPeriod);

        //  Lấy danh sách bảng lương bằng JPA (Thay vì SQL thuần)
        List<PayrollEntity> payrolls = payrollsJPARepo.findAllByPayperiod(payPeriod);

        if (payrolls.isEmpty()) {
            System.out.println("Không có dữ liệu bảng lương nào để xử lý.");
            return;
        }

        System.out.println("Tìm thấy " + payrolls.size() + " nhân viên.");

        for (PayrollEntity payroll : payrolls) {
            // Lấy UserEntity trực tiếp từ quan hệ (Không cần query lại)
            UserEntity user = payroll.getUserID();

            try {
                // A. CẬP NHẬT TRẠNG THÁI FINAL
                // Giả sử status là Enum hoặc String. Nếu là Enum thì dùng PayrollEntity.Status.FINAL
                if (!"FINAL".equalsIgnoreCase(String.valueOf(payroll.getStatus()))) {

                    payroll.setStatus("FINAL");
                    payrollsJPARepo.save(payroll);
                }

                // B. LẤY DỮ LIỆU LƯƠNG CHI TIẾT (DTO)
                // Gọi hàm mới nhận vào UserEntity
                PayrollDetailDTO payslipDTO = payslipService.getMyPayslipPDF(user, month, year);

                if (payslipDTO == null) {
                    System.err.println("WARN: DTO null cho User " + user.getId());
                    continue;
                }

                // C. CHUYỂN ĐỔI DTO -> MAP (Để tương thích với PdfService cũ)
                // Nếu PdfService của bạn chưa sửa để nhận DTO, ta convert sang Map
                Map<String, Object> mapData = objectMapper.convertValue(payslipDTO, Map.class);

                // D. TẠO PDF VÀ GỬI MAIL
                byte[] pdfBytes = pdfService.generatePayslipPdf(mapData);

                if (pdfBytes != null) {
                    emailService.sendPayslipEmail(
                            user.getEmail(),
                            user.getFullname(),
                            payPeriod,
                            pdfBytes
                    );
                    System.out.println("Đã gửi mail cho: " + user.getFullname());
                }

            } catch (Exception e) {
                System.err.println("Lỗi xử lý cho nhân viên " + user.getFullname() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("<<< HOÀN TẤT QUÁ TRÌNH.");
    }
}
