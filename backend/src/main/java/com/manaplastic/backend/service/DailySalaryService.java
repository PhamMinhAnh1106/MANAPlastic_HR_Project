package com.manaplastic.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.payrollengine.component.ExpressionEvaluator;
import com.manaplastic.backend.payrollengine.component.PayrollDataFetcher;
import com.manaplastic.backend.payrollengine.model.ExpressionNode;
import com.manaplastic.backend.payrollengine.repository.PayrollEngineRepository;
import com.manaplastic.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DailySalaryService {

    @Autowired private AttendanceRepository attendanceRepo;
    @Autowired private ContractRepository contractRepo;
    @Autowired private OvertimeRequestRepository otRequestRepo;
    @Autowired private OvertimeRequestDetailRepository otDetailRepo;
    @Autowired private ExpressionEvaluator evaluator;
    @Autowired private PayrollEngineRepository ruleRepo;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PayrollDataFetcher dataFetcher;


    @Transactional
    public void calculateAndSaveDailySalary(UserEntity user, LocalDate date) {
        // Lấy dữ liệu chấm công
        AttendanceEntity attendance = attendanceRepo.findByUserIDAndDate(user, date)
                .orElse(null);

        if (attendance == null) {
            System.out.println("Không tìm thấy dữ liệu chấm công ngày " + date + " cho User: " + user.getId());
            return;
        }

        // Lấy hợp đồng hiệu lực (Để đảm bảo nhân viên có HĐ Active)
        ContractEntity contract = contractRepo.findActiveContract(user.getId(), date);
        if (contract == null) {
            System.err.println("Nhân viên " + user.getId() + " chưa có hợp đồng ngày " + date);
            return;
        }

        // KHỞI TẠO CONTEXT (Kết hợp SQL DB và Dữ liệu ngày)
        Map<String, BigDecimal> context = buildDailyContext(user, attendance, date);

        // CHẠY ENGINE
        List<Map<String, Object>> rulesRaw = ruleRepo.fetchApprovedRules();
        System.out.println(">>> TÍNH LƯƠNG NGÀY " + date + " | NV: " + user.getFullname());

        for (Map<String, Object> ruleRow : rulesRaw) {
            String ruleCode = (String) ruleRow.get("rule_code");
            String dslJson = (String) ruleRow.get("dsl_json");

            try {
                ExpressionNode rootNode = objectMapper.readValue(dslJson, ExpressionNode.class);
                BigDecimal result = evaluator.evaluate(rootNode, context);
                context.put(ruleCode, result);
                System.out.println("   -> Calculated " + ruleCode + " = " + result);
            } catch (Exception e) {
                System.err.println("Lỗi tính rule " + ruleCode + ": " + e.getMessage());
                context.put(ruleCode, BigDecimal.ZERO);
            }
        }


        BigDecimal finalDailySalary = context.getOrDefault("TOTAL_INCOME", BigDecimal.ZERO);

        attendance.setEstimatedSalary(finalDailySalary);
        attendanceRepo.save(attendance);

        System.out.println("<<< ĐÃ LƯU (Gross Day): " + finalDailySalary + " VND");
    }

    private Map<String, BigDecimal> buildDailyContext(UserEntity user, AttendanceEntity attendance, LocalDate date) {
        //  Lấy toàn bộ biến tĩnh từ DB (Lương 1h, Hệ số đêm, Ngày chuẩn...)
        Map<String, BigDecimal> context = dataFetcher.fetchContext(user.getId(), date.getMonthValue(), date.getYear());

        // --- DEBUG LOG: Kiểm tra xem DB trả về gì ---
        System.out.println("DEBUG DB VARS -> Hourly: " + context.get("HOURLY_MONEY") + " | RateNight: " + context.get("RATE_NIGHT_SHIFT"));

        //  Tính toán lại các biến động theo ngày (GHI ĐÈ giá trị của tháng)
        context.put("TOTAL_REWARD", BigDecimal.ZERO);   // Không cộng thưởng tháng
        context.put("TOTAL_PENALTY", BigDecimal.ZERO);  // Không trừ phạt tháng
//        context.put("TOTAL_ALLOWANCE", BigDecimal.ZERO); // Không cộng phụ cấp tháng (đã nằm trong Hourly Rate nếu có)

        // Vô hiệu hóa tính Bảo hiểm & Thuế trong ngày (để tránh ra số âm)
        // Cách làm: Set lương đóng bảo hiểm = 0 để công thức BHXH nhân ra 0
        context.put("INSURANCE_SALARY", BigDecimal.ZERO);
        context.put("PERSONAL_DEDUCTION", BigDecimal.ZERO); // Mức giảm trừ gia cảnh

        //Giờ làm việc thực tế
        BigDecimal workHours = calculateWorkHours(attendance.getCheckin(), attendance.getCheckout());
        context.put("ACTUAL_WORK_HOURS", workHours); // Ghi đè

        //Ngày công thực tế (0.5 hay 1.0)
        BigDecimal realWorkDay = BigDecimal.ZERO;
        if (workHours.compareTo(BigDecimal.valueOf(7)) >= 0) realWorkDay = BigDecimal.ONE;
        else if (workHours.compareTo(BigDecimal.valueOf(3.5)) >= 0) realWorkDay = BigDecimal.valueOf(0.5);
        context.put("REAL_WORK_DAYS", realWorkDay); // Ghi đè

        //Tổng giờ OT quy đổi
        BigDecimal dailyOtConverted = calculateDailyOtConverted(user, date);
        context.put("TOTAL_OT_CONVERTED", dailyOtConverted); // Ghi đè

        //  Tổng giờ đêm hôm nay
        BigDecimal nightHours = calculateNightHours(attendance.getCheckin(), attendance.getCheckout());
        context.put("TOTAL_NIGHT_HOURS", nightHours); // Ghi đè

        System.out.println("DEBUG CALC -> WorkHours: " + workHours + " | NightHours: " + nightHours);

        return context;
    }

    // --- CÁC HÀM HELPER (Logic tính toán thời gian) ---

    private BigDecimal calculateWorkHours(LocalDateTime in, LocalDateTime out) {
        if (in == null || out == null) return BigDecimal.ZERO;
        long minutes = Duration.between(in, out).toMinutes();
        return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDailyOtConverted(UserEntity user, LocalDate date) {
        List<OvertimeRequestEntity> requests = otRequestRepo.findByUseridAndDateAndStatus(
                user, date, OvertimeRequestEntity.RequestStatus.APPROVED);

        BigDecimal totalConverted = BigDecimal.ZERO;

        for (OvertimeRequestEntity req : requests) {
            List<OvertimeRequestDetailEntity> details = otDetailRepo.findByRequestID(req);

            for (OvertimeRequestDetailEntity detail : details) {
                BigDecimal hours = BigDecimal.valueOf(detail.getHours());
                if (detail.getOvertimeTypeID() != null) {
                    BigDecimal rate = detail.getOvertimeTypeID().getRate();
                    totalConverted = totalConverted.add(hours.multiply(rate));
                }
            }
        }
        return totalConverted;
    }

    private BigDecimal calculateNightHours(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkIn == null || checkOut == null) return BigDecimal.ZERO;

        // Xác định khung đêm của ngày bắt đầu ca (22:00 hôm nay -> 06:00 hôm sau)
        // Lưu ý: Nếu checkIn là 01:00 sáng, ta phải hiểu nó thuộc ca đêm của ngày hôm qua
        LocalDate shiftDate = checkIn.toLocalDate();
        if (checkIn.getHour() < 6) {
            shiftDate = shiftDate.minusDays(1); // Lùi về ngày hôm trước nếu vào ca sớm
        }

        LocalDateTime nightStart = shiftDate.atTime(22, 0); // 22:00 ngày vào ca
        LocalDateTime nightEnd = shiftDate.plusDays(1).atTime(6, 0); // 06:00 ngày hôm sau

        // Tính giao thoa (Intersection) giữa thời gian làm việc và khung đêm
        // startOverlap = Max(CheckIn, NightStart)
        LocalDateTime startOverlap = checkIn.isAfter(nightStart) ? checkIn : nightStart;

        // endOverlap = Min(CheckOut, NightEnd)
        LocalDateTime endOverlap = checkOut.isBefore(nightEnd) ? checkOut : nightEnd;

        // Nếu Start < End thì mới có giờ đêm
        if (startOverlap.isBefore(endOverlap)) {
            long minutes = Duration.between(startOverlap, endOverlap).toMinutes();
            return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void autoCalculateDailySalary() {
        // ngày cần tính (Là ngày hôm qua)
        LocalDate targetDate = LocalDate.now().minusDays(1);

        System.out.println("Bắt đầu job tính lương tự động cho ngày: " + targetDate);

        List<AttendanceEntity> attendanceList = attendanceRepo.findAllByDate(targetDate);

        if (attendanceList.isEmpty()) {
            System.out.println("Không có dữ liệu chấm công ngày " + targetDate);
            return;
        }

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        attendanceList.forEach(att -> {
            try {

                calculateAndSaveDailySalary(att.getUserID(), targetDate);
                successCount.getAndIncrement();
            } catch (Exception e) {
                System.err.println("❌ Lỗi User " + att.getUserID().getId() + ": " + e.getMessage());
                errorCount.getAndIncrement();
            }
        });

        System.out.println(String.format("✅ Hoàn tất! Thành công: %d, Lỗi: %d",
                successCount.get(), errorCount.get()));
    }
}