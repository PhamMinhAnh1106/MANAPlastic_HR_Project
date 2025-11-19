package com.manaplastic.backend.service;

import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.repository.AttendanceLogRepository;
import com.manaplastic.backend.repository.AttendanceRepository;
import com.manaplastic.backend.repository.EmployeeOfficialScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Optional;


@Service
public class AttendanceLogService {

    @Autowired
    private AttendanceLogRepository attendancelogRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private EmployeeOfficialScheduleRepository scheduleRepository;

    // Cấu hình TimeZone (Việt Nam) để convert từ Instant sang LocalDate
    private final ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

    // Cho phép đi trễ/về sớm bao nhiêu phút
    private static final int TOLERANCE_MINUTES = 5; // 5p thôii

    // Thời gian tối thiểu giữa 2 lần chấm công (15p =  900s)
    private static final long SPAM_COOLDOWN_SECONDS = 900;

    @Transactional
    public AttendancelogEntity processAttendanceLog(AttendancelogEntity log) {
        AttendancelogEntity savedLog = attendancelogRepository.save(log);
        UserEntity user = savedLog.getUserID();
        LocalDate logDate = savedLog.getTimestamp().atZone(zoneId).toLocalDate();

        Optional<AttendancelogEntity> lastLogOpt = attendancelogRepository.findTopByUserIDOrderByTimestampDesc(user);
        if (lastLogOpt.isPresent()) {
            AttendancelogEntity lastLog = lastLogOpt.get();
            // Tính khoảng cách thời gian: Log Mới - Log Cũ
            long secondsDiff = Duration.between(lastLog.getTimestamp(), log.getTimestamp()).abs().getSeconds();

            if (secondsDiff < SPAM_COOLDOWN_SECONDS) {
                System.out.println("Phát hiện Spam log từ User " + user.getId() + ". Bỏ qua xử lý logic.");
                return attendancelogRepository.save(log); // Lưu log rồi thoát luôn hàm
            }
        }
        // Kiểm tra xem hôm nay nhân viên này đã có dòng chấm công nào chưa
        var existingAttendance = attendanceRepository.findByUserIDAndDate(user, logDate);

        if (existingAttendance.isPresent()) {
            // TRƯỜNG HỢP: ĐÃ CÓ BẢN GHI (Đã Check-in sáng nay) => Đây sẽ được tính là Check-out (Cập nhật giờ ra)
            AttendanceEntity attendance = existingAttendance.get();

            // Cập nhật thông tin Check-out
            attendance.setCheckout(savedLog.getTimestamp());
            attendance.setCheckoutImgUrl(savedLog.getImgUrl());
            attendance.setCheckOutLogID(savedLog);

            if (attendance.getShiftID() == null) {
                assignShiftToAttendance(attendance, user, logDate);
            }

            AttendanceEntity.AttendanceStatus calculatedStatus = calculateAttendanceStatus(attendance);
            attendance.setStatus(calculatedStatus);

            attendanceRepository.save(attendance);

        } else {
            // TRƯỜNG HỢP: CHƯA CÓ BẢN GHI (Lần đầu quét trong ngày) => Đây là Check-in
            AttendanceEntity newAttendance = new AttendanceEntity();

            // cập nhật thông tin check-in
            newAttendance.setUserID(user);
            newAttendance.setDate(logDate);
            newAttendance.setCheckin(savedLog.getTimestamp());
            newAttendance.setCheckinImgUrl(savedLog.getImgUrl());
            newAttendance.setCheckInLogID(savedLog);
            newAttendance.setStatus(AttendanceEntity.AttendanceStatus.MISSING_OUTPUT_DATA);

            assignShiftToAttendance(newAttendance, user, logDate);

            attendanceRepository.save(newAttendance);
        }

        return savedLog;
    }

    private void assignShiftToAttendance(AttendanceEntity attendance, UserEntity user, LocalDate date) { // gán ca làm từ lịch chính thức
        var scheduleOpt = scheduleRepository.findByEmployeeIDAndDate(user, date);

        if (scheduleOpt.isPresent()) {
            EmployeeofficialscheduleEntity schedule = scheduleOpt.get();

            if (Boolean.FALSE.equals(schedule.getIsDayOff()) && schedule.getShiftID() != null) {
                attendance.setShiftID(schedule.getShiftID());
            }
        }
    }

    private AttendanceEntity.AttendanceStatus calculateAttendanceStatus(AttendanceEntity attendance) {
        if (attendance.getShiftID() == null) {
            return AttendanceEntity.AttendanceStatus.ON_LEAVE;
        }

        ShiftEntity shift = attendance.getShiftID();

        LocalTime shiftStart = shift.getStarttime();
        LocalTime shiftEnd = shift.getEndtime();
        LocalDate workDate = attendance.getDate();

        // Ghép ngày làm việc với giờ bắt đầu ca
        LocalDateTime expectedStartTime = LocalDateTime.of(workDate, shiftStart);
        LocalDateTime expectedEndTime;

        // Xử lý ca qua đêm (Ví dụ: Start 22:00, End 06:00)
        if (shiftEnd.isBefore(shiftStart)) {
            // Nếu giờ kết thúc nhỏ hơn giờ bắt đầu => Ca làm việc kết thúc vào ngày hôm sau
            expectedEndTime = LocalDateTime.of(workDate.plusDays(1), shiftEnd);
        } else {
            // Ca làm việc trong ngày
            expectedEndTime = LocalDateTime.of(workDate, shiftEnd);
        }

        // Lấy giờ thực tế (Convert Instant sang LocalDateTime VN)
        LocalDateTime actualCheckIn = attendance.getCheckin().atZone(zoneId).toLocalDateTime();
        LocalDateTime actualCheckOut = attendance.getCheckout().atZone(zoneId).toLocalDateTime();

        // So sánh và gắn cờ (Cho phép trễ X phút tolerance, công ty cho 5p)
        boolean isLate = actualCheckIn.isAfter(expectedStartTime.plusMinutes(TOLERANCE_MINUTES));
        boolean isEarlyLeave = actualCheckOut.isBefore(expectedEndTime.minusMinutes(TOLERANCE_MINUTES));

        if (isLate || isEarlyLeave) {
            return AttendanceEntity.AttendanceStatus.LATE_AND_EARLY;
        } else {
            return AttendanceEntity.AttendanceStatus.PRESENT;
        }
    }
}