package com.manaplastic.backend.controller.payroll;


import com.manaplastic.backend.entity.AttendanceEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.AttendanceRepository;
import com.manaplastic.backend.service.DailySalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/user/DailySalary")
public class SalaryDailyController {

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private DailySalaryService dailySalaryService;

    @PostMapping("/recalculate")
    @PreAuthorize("hasAuthority('HR') or hasAuthority('Admin')")
    public ResponseEntity<?> recalculateAllSalary(@RequestParam int month, @RequestParam int year) {

        // Lấy tất cả bản ghi chấm công trong tháng
        List<AttendanceEntity> attendanceList = attendanceRepo.findAllByMonthAndYear(month, year);

        if (attendanceList.isEmpty()) {
            return ResponseEntity.ok("Không tìm thấy dữ liệu chấm công nào trong tháng " + month + "/" + year);
        }

        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        // Chạy vòng lặp tính toán lại từng người
        attendanceList.forEach(att -> {
            try {
                dailySalaryService.calculateAndSaveDailySalary(att.getUserID(), att.getDate());
                count.getAndIncrement();
            } catch (Exception e) {
                System.err.println("Lỗi tính lương ID " + att.getId() + ": " + e.getMessage());
                errorCount.getAndIncrement();
            }
        });

        return ResponseEntity.ok(String.format(
                "Đã tính toán lại thành công cho %d bản ghi. Lỗi: %d bản ghi.",
                count.get(), errorCount.get()
        ));
    }
    //API Lấy lương dự kiến của một ngày cụ thể
    @GetMapping("/daily")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDailySalary(@AuthenticationPrincipal UserEntity user,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (date == null) date = LocalDate.now();

        Optional<AttendanceEntity> attOpt = attendanceRepo.findByUserIDAndDate(user, date);

        if (attOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Chưa có dữ liệu chấm công", "amount", 0));
        }

        AttendanceEntity attendance = attOpt.get();

        if (date.equals(LocalDate.now())
                && attendance.getCheckout() != null
                && (attendance.getEstimatedSalary() == null || attendance.getEstimatedSalary().compareTo(BigDecimal.ZERO) == 0)) {

            try {
                dailySalaryService.calculateAndSaveDailySalary(user, date);

                attendance = attendanceRepo.findByUserIDAndDate(user, date).orElse(attendance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Trả về kết quả JSON
        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("status", attendance.getStatus());
        response.put("checkIn", attendance.getCheckin());
        response.put("checkOut", attendance.getCheckout());
        response.put("estimatedSalary", attendance.getEstimatedSalary() != null ? attendance.getEstimatedSalary() : BigDecimal.ZERO);

        return ResponseEntity.ok(response);
    }

   // Lấy tổng lương dự kiến của tháng
    @GetMapping("/monthlyTotal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMonthlyTotal(@AuthenticationPrincipal UserEntity user,
                                             @RequestParam int month,
                                             @RequestParam int year) {


        BigDecimal totalSalary = attendanceRepo.sumEstimatedSalaryByMonth(user.getId(), month, year);

        Map<String, Object> response = new HashMap<>();
        response.put("month", month);
        response.put("year", year);
        response.put("userId", user.getId());
        response.put("totalEstimatedSalary", totalSalary);

        return ResponseEntity.ok(response);
    }

    // test trigger
    @PostMapping("/trigger-cron")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<?> triggerCronManually() {
        dailySalaryService.autoCalculateDailySalary();
        return ResponseEntity.ok("Đã kích hoạt Cron Job thủ công!");
    }
}
