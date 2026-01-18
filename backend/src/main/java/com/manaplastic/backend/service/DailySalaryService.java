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

    @Autowired
    private AttendanceRepository attendanceRepo;
    @Autowired
    private ContractRepository contractRepo;
    @Autowired
    private OvertimeRequestRepository otRequestRepo;
    @Autowired
    private OvertimeRequestDetailRepository otDetailRepo;
    @Autowired
    private ExpressionEvaluator evaluator;
    @Autowired
    private PayrollEngineRepository ruleRepo;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PayrollDataFetcher dataFetcher;
    @Autowired
    private LeaveRequestRepository leaveRequestRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ShiftRepository shiftRepo;

    @Transactional
    public void calculateAndSaveDailySalary(UserEntity user, LocalDate date) {
        // Lấy dữ liệu chấm công
        AttendanceEntity attendance = attendanceRepo.findByUserIDAndDate(user, date)
                .orElse(null);
        LeaverequestEntity approvedLeave = leaveRequestRepo.findApprovedRequestForDate(user, date)
                .orElse(null);

        // XỬ LÝ LOGIC "KHÔNG CHẤM CÔNG" - ngày nghỉ
        if (attendance == null) {
            if (approvedLeave != null) {
                // Trường hợp: Nghỉ phép có đơn -> Tạo mới bản ghi Attendance
                attendance = new AttendanceEntity();
                attendance.setUserID(user);
                attendance.setDate(date);
                attendance.setStatus(AttendanceEntity.AttendanceStatus.ON_LEAVE);

                // Lưu tạm để có ID
                attendance = attendanceRepo.save(attendance);
                System.out.println("   -> Tạo attendance mới cho ngày nghỉ phép: " + user.getUsername());
            } else {
                // Trường hợp: Không chấm công + Không có đơn -> Nghỉ không phép (ABSENT)
                System.out.println("User " + user.getId() + " không đi làm, không có đơn ngày " + date);
                return;
            }
        }
        // Lấy hợp đồng hiệu lực (Để đảm bảo nhân viên có HĐ Active)
        ContractEntity contract = contractRepo.findActiveContract(user.getId(), date);
        if (contract == null) {
            System.err.println("Nhân viên " + user.getId() + " chưa có hợp đồng ngày " + date);
            return;
        }

        // KHỞI TẠO CONTEXT (Kết hợp SQL DB và Dữ liệu ngày)
        Map<String, BigDecimal> context = buildDailyContext(user, attendance, date, approvedLeave);

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

        BigDecimal finalDailySalary = context.getOrDefault("DAILY_EARNING", BigDecimal.ZERO);

        attendance.setEstimatedSalary(finalDailySalary);
        attendanceRepo.save(attendance);

        System.out.println("<<< ĐÃ LƯU (Gross Day): " + finalDailySalary + " VND");
    }

    private Map<String, BigDecimal> buildDailyContext(UserEntity user, AttendanceEntity attendance, LocalDate date, LeaverequestEntity approvedLeave) {
        Map<String, BigDecimal> context = dataFetcher.fetchContext(user.getId(), date.getMonthValue(), date.getYear());
        BigDecimal workHours = BigDecimal.ZERO;
        BigDecimal realWorkDay = BigDecimal.ZERO;
        boolean isPaidLeave = false;

        //  ƯU TIÊN KIỂM TRA ĐƠN NGHỈ PHÉP (Leave Request) ---
        // Truy vấn xem ngày hôm nay (date) user có đơn nào status = APPROVED không
        if (approvedLeave != null) {
            LeaverequestEntity.LeaveType type = approvedLeave.getLeavetype();

            if (type == LeaverequestEntity.LeaveType.ANNUAL ||
                    type == LeaverequestEntity.LeaveType.SICK ||
                    type == LeaverequestEntity.LeaveType.MATERNITY ||
                    type == LeaverequestEntity.LeaveType.PATERNITY) {

                isPaidLeave = true;
                workHours = BigDecimal.valueOf(8.0);
                realWorkDay = BigDecimal.ONE;
            }
        }

        //  CHẤM CÔNG THỰC TẾ (Chỉ chạy nếu KHÔNG phải nghỉ phép) ---
        if (!isPaidLeave && attendance != null && attendance.getCheckin() != null && attendance.getCheckout() != null) {
            workHours = calculateWorkHours(attendance.getCheckin(), attendance.getCheckout());
            if (workHours.compareTo(BigDecimal.valueOf(7)) >= 0) realWorkDay = BigDecimal.ONE;
            else if (workHours.compareTo(BigDecimal.valueOf(3.5)) >= 0) realWorkDay = BigDecimal.valueOf(0.5);
        }

        // GHI ĐÈ CÁC GIÁ TRỊ VÀO CONTEXT ---
        // Reset các biến tích lũy tháng về 0 (để tránh cộng dồn sai trong ngày)
        context.put("TOTAL_REWARD", BigDecimal.ZERO);
        context.put("TOTAL_PENALTY", BigDecimal.ZERO);
        context.put("INSURANCE_SALARY", BigDecimal.ZERO);
        context.put("PERSONAL_DEDUCTION", BigDecimal.ZERO);
        context.put("ACTUAL_WORK_HOURS", workHours);
        context.put("REAL_WORK_DAYS", realWorkDay);

        // TÍNH TOÁN CÁC KHOẢN PHỤ (OT, Đêm) ---
        BigDecimal dailyOtConverted = calculateDailyOtConverted(user, date);
        context.put("TOTAL_OT_CONVERTED", dailyOtConverted);

        // Tổng giờ đêm (Night Hours)
        // Chỉ tính phụ cấp đêm nếu đi làm thực tế. Nếu nghỉ phép thì không có phụ cấp đêm. Nếu có OT để phân biệt voới người tự nguyện cống hiến
        BigDecimal nightHours = BigDecimal.ZERO;
        if (!isPaidLeave && attendance != null && attendance.getCheckin() != null && attendance.getCheckout() != null) {

            LocalDateTime checkIn = attendance.getCheckin();
            LocalDateTime actualCheckOut = attendance.getCheckout();

            // Mặc định: Giờ ra tính lương là giờ ra thực tế
            LocalDateTime effectiveCheckOut = actualCheckOut;
            LocalDateTime shiftEndTime = getShiftEndTime(attendance, date);

            // NẾU về trễ hơn quy định VÀ KHÔNG có đơn OT (dailyOtConverted == 0)
            if (shiftEndTime != null && actualCheckOut.isAfter(shiftEndTime) && dailyOtConverted.compareTo(BigDecimal.ZERO) == 0) {
                // => Cắt giờ: Chỉ tính đến giờ hết ca
                effectiveCheckOut = shiftEndTime;
                System.out.println("   -> Cắt giờ ra từ " + actualCheckOut + " xuống " + shiftEndTime + " (Do không có OT)");
            }
            nightHours = calculateNightHours(checkIn, effectiveCheckOut);
        }
        context.put("TOTAL_NIGHT_HOURS", nightHours);

        System.out.println("DEBUG CALC -> WorkHours: " + workHours + " | RealDays: " + realWorkDay + " | Leave: " + isPaidLeave);

        return context;
    }

    // --- CÁC HÀM HELPER (Logic tính toán thời gian) ---
    private BigDecimal calculateWorkHours(LocalDateTime in, LocalDateTime out) {
        if (in == null || out == null) return BigDecimal.ZERO;
        long minutes = Duration.between(in, out).toMinutes();
        return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private LocalDateTime getShiftEndTime(AttendanceEntity attendance, LocalDate date) {
        if (attendance.getShiftID() == null) return null; // Hoặc logic lấy ca mặc định

        ShiftEntity shift = attendance.getShiftID();

        if (shift == null) return null;

        // Giả sử ShiftEntity có field endTime (LocalTime)
        LocalDateTime endDateTime = date.atTime(shift.getEndtime());

        // Xử lý ca đêm (Nếu End < Start -> Ca qua đêm -> Thuộc ngày hôm sau)
        if (shift.getEndtime().isBefore(shift.getStarttime())) {
            endDateTime = endDateTime.plusDays(1);
        }
        return endDateTime;
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
        LocalDate targetDate = LocalDate.now().minusDays(1);
        System.out.println("--- JOB TÍNH LƯƠNG NGÀY: " + targetDate + " ---");

        List<UserEntity> activeUsers = userRepo.findAllActiveUsers();
        AtomicInteger successCount = new AtomicInteger(0);

        activeUsers.forEach(user -> {
            try {
                // Gọi hàm tính:
                // - Có attendance -> Tính
                // - Không attendance nhưng có Phép -> Tự tạo attendance -> Tính
                // - Không có gì -> Bỏ qua
                calculateAndSaveDailySalary(user, targetDate);
                successCount.getAndIncrement();
            } catch (Exception e) {
                System.err.println("Lỗi User " + user.getUsername() + ": " + e.getMessage());
                e.printStackTrace();
            }
        });

        System.out.println("Job hoàn tất. Đã xử lý: " + successCount.get() + " nhân viên.");
    }
}