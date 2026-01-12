package com.manaplastic.backend.service;

import com.manaplastic.backend.entity.AttendanceEntity; // Giả sử bạn có entity này
import com.manaplastic.backend.entity.ShiftEntity;
import com.manaplastic.backend.repository.AttendanceRepository; // Repo chấm công
import com.manaplastic.backend.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceScannerService {

    private final AttendanceRepository attendanceRepo;
    private final OvertimeService overtimeService;

    // Ngưỡng tối thiểu để tính OT tự động (ví dụ: 30 phút)
    private static final long MIN_OT_MINUTES = 30;

    /**
     * Hàm quét và sinh đơn OT cho một ngày cụ thể.
     * Thường chạy cho ngày hôm qua (yesterday).
     */
    public void scanAndGenerateOvertime(LocalDate date) {
        log.info("Bắt đầu quét OT tự động cho ngày: {}", date);

        // 1. Lấy tất cả bản ghi chấm công của ngày đó
        // Giả sử AttendanceEntity có field: date, user, shift, checkOutTime (LocalDateTime)
        List<AttendanceEntity> attendanceList = attendanceRepo.findAllByDate(date);

        for (AttendanceEntity att : attendanceList) {
            try {
                processSingleAttendance(att);
            } catch (Exception e) {
                log.error("Lỗi khi quét OT cho user {}: {}", att.getUserID().getId(), e.getMessage());
            }
        }

        log.info("Hoàn tất quét OT cho ngày: {}", date);
    }

    private void processSingleAttendance(AttendanceEntity att) {
        // Validation cơ bản
        if (att.getCheckout() == null || att.getShiftID() == null) {
            return; // Chưa check-out hoặc không có ca -> Bỏ qua
        }

        ShiftEntity shift = att.getShiftID();
        LocalTime shiftEndTime = shift.getEndtime(); // Ví dụ 17:00
        LocalDateTime actualCheckOut = att.getCheckout(); // Ví dụ 2026-01-20 19:30

        // 2. Tính toán thời gian chênh lệch
        // Cần convert shiftEndTime sang LocalDateTime để so sánh
        LocalDateTime scheduledEndTime = att.getDate().atTime(shiftEndTime);

        // Xử lý ca đêm (Shift End < Shift Start -> End Time là ngày hôm sau)
        if (shift.getEndtime().isBefore(shift.getStarttime())) {
            scheduledEndTime = scheduledEndTime.plusDays(1);
        }

        // Nếu về sớm hơn hoặc đúng giờ -> Không có OT
        if (!actualCheckOut.isAfter(scheduledEndTime)) {
            return;
        }

        // Tính số phút dư
        long excessMinutes = Duration.between(scheduledEndTime, actualCheckOut).toMinutes();

        // 3. Kiểm tra Ngưỡng (Threshold)
        if (excessMinutes >= MIN_OT_MINUTES) {

            // Convert sang giờ (double)
            // Ví dụ: 90 phút -> 1.5 giờ
            // Có thể làm tròn xuống 30 phút tùy chính sách công ty (ví dụ 1h50 -> 1.5h)
            // Ở đây tính thô:
            Double detectedHours = (double) excessMinutes / 60.0;

            // Làm tròn 2 chữ số thập phân
            detectedHours = Math.round(detectedHours * 100.0) / 100.0;

            log.info("Phát hiện OT: User {} - Dư {} phút ({} giờ)", att.getUserID().getUsername(), excessMinutes, detectedHours);

            // 4. GỌI OVERTIME SERVICE
            // Đây chính là chỗ kết nối 2 module lại với nhau
            overtimeService.autoGenerateSystemRequest(
                    att.getUserID(),
                    att.getDate(),
                    actualCheckOut,
                    detectedHours
            );
        }
    }

    // ==================================================================
    // CRON JOB: TỰ ĐỘNG CHẠY VÀO 7:00 SÁNG HÀNG NGÀY
    // ==================================================================
    @Scheduled(cron = "0 0 7 * * ?")
    public void scheduledDailyScan() {
        // Quét dữ liệu của ngày hôm qua
        LocalDate yesterday = LocalDate.now().minusDays(1);
        scanAndGenerateOvertime(yesterday);
    }
}