package com.manaplastic.backend.service;

import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;


@Service
public class AttendanceLogService {

    @Autowired
    private AttendanceLogRepository attendancelogRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private EmployeeOfficialScheduleRepository scheduleRepository;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private EmployeeDocumentRepository documentRepo;

    // Cấu hình TimeZone (Việt Nam) để convert từ Instant sang LocalDate
    private final ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
    // Cho phép đi trễ/về sớm bao nhiêu phút
    private static final int TOLERANCE_MINUTES = 5; // 5p thôii
    private static final int TOLERANCE_MATERNITY_MINUTES = 30; // thai sản 30
    // Thời gian tối thiểu giữa 2 lần chấm công (15p =  900s)
    private static final long SPAM_COOLDOWN_SECONDS = 900;
    private static final Integer OFFICE_SHIFT_ID = 36;

    @Value("${app.upload.attendance}")
    private String uploadDir;

    @Transactional
    public AttendancelogEntity processAttendanceLog(AttendancelogEntity log) {
//        AttendancelogEntity savedLog = attendancelogRepository.save(log);
//        UserEntity user = savedLog.getUserID();
////        LocalDate logDate = savedLog.getTimestamp().atZone(zoneId).toLocalDate();
//        LocalDate logDate = savedLog.getTimestamp().toLocalDate();

        UserEntity user = log.getUserID();
        //KIỂM TRA SPAM (Dùng log chưa lưu để so sánh với log trong DB) ---
        Optional<AttendancelogEntity> lastLogOpt = attendancelogRepository.findTopByUserIDOrderByTimestampDesc(user);

        if (lastLogOpt.isPresent()) {
            AttendancelogEntity lastLog = lastLogOpt.get();

            // Tính khoảng cách thời gian
            long secondsDiff = Duration.between(lastLog.getTimestamp(), log.getTimestamp()).abs().getSeconds();

            if (secondsDiff < SPAM_COOLDOWN_SECONDS) {
                System.out.println("Phát hiện Spam log từ User " + user.getId() + ". Bỏ qua xử lý logic.");

//                return attendancelogRepository.save(log);
                throw new RuntimeException("Vui lòng đợi 15 phút giữa các lần chấm công!");
            }
        }

        AttendancelogEntity savedLog = attendancelogRepository.save(log);
        LocalDate logDate = savedLog.getTimestamp().toLocalDate();

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

    // Hàm phụ trợ để lưu file (Giống file_put_contents của PHP)
    public String saveImageFromBase64(String base64Str) {
        try {
            Path uploadPath = Paths.get(uploadDir.trim());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + ".jpg";
            Path filePath = uploadPath.resolve(fileName);

            // Giải mã Base64 và ghi ra file
            byte[] imageBytes = Base64.getDecoder().decode(base64Str);
            try (OutputStream stream = new FileOutputStream(filePath.toFile())) {
                stream.write(imageBytes);
            }

            // Trả về đường dẫn để lưu vào DB
//            return "/uploads/attendance" + fileName;
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi lưu ảnh: " + e.getMessage());
        }
    }

    private void assignShiftToAttendance(AttendanceEntity attendance, UserEntity user, LocalDate date) {
        boolean isOfficeUser = false;
        if (user.getDepartmentID() != null && Boolean.TRUE.equals(user.getDepartmentID().getIsoffice())) {
            isOfficeUser = true;
        }

        if (isOfficeUser) {
            Optional<ShiftEntity> officeShift = shiftRepository.findById(OFFICE_SHIFT_ID);
            if (officeShift.isPresent()) {
                attendance.setShiftID(officeShift.get());
            } else {
                System.err.println("Lỗi: Không tìm thấy Shift ID.");
            }
        } else {
            var scheduleOpt = scheduleRepository.findByEmployeeIDAndDate(user, date);
            if (scheduleOpt.isPresent()) {
                EmployeeofficialscheduleEntity schedule = scheduleOpt.get();
                if (Boolean.FALSE.equals(schedule.getIsDayOff()) && schedule.getShiftID() != null) {
                    attendance.setShiftID(schedule.getShiftID());
                }
            }
        }
    }

    private AttendanceEntity.AttendanceStatus calculateAttendanceStatus(AttendanceEntity attendance) {
        if (attendance.getShiftID() == null) {
            return AttendanceEntity.AttendanceStatus.ON_LEAVE;
        }

        ShiftEntity shift = attendance.getShiftID();
        LocalDate workDate = attendance.getDate();
        boolean isPregnant = isMaternity(attendance.getUserID(), workDate);

        int currentTolerance = isPregnant ? TOLERANCE_MATERNITY_MINUTES : TOLERANCE_MINUTES;

        LocalTime shiftStart = shift.getStarttime();
        LocalTime shiftEnd = shift.getEndtime();

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
//        LocalDateTime actualCheckIn = attendance.getCheckin().atZone(zoneId).toLocalDateTime();
//        LocalDateTime actualCheckOut = attendance.getCheckout().atZone(zoneId).toLocalDateTime();
        LocalDateTime actualCheckIn = attendance.getCheckin();
        LocalDateTime actualCheckOut = attendance.getCheckout();

        // So sánh và gắn cờ (Cho phép trễ X phút tolerance, công ty cho 5p)
        boolean isLate = actualCheckIn.isAfter(expectedStartTime.plusMinutes(currentTolerance));
        boolean isEarlyLeave = (actualCheckOut != null) &&
                                actualCheckOut.isBefore(expectedEndTime.minusMinutes(currentTolerance));

        if (isLate || isEarlyLeave) {
            return AttendanceEntity.AttendanceStatus.LATE_AND_EARLY;
        } else {
            return AttendanceEntity.AttendanceStatus.PRESENT;
        }
    }

    public boolean isMaternity(UserEntity user, LocalDate date) {
        if (user == null || user.getId() == null || date == null) {
            return false;
        }

        return documentRepo.isDocumentActive(
                user.getId(),
                date,
                EmployeeDocumentEntity.DocumentType.MEDICAL_PREGNANCY,
                EmployeeDocumentEntity.DocumentStatus.APPROVED
        );
    }
}