package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.AttendanceLogRequest;
import com.manaplastic.backend.entity.AttendancelogEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.UserRepository;
import com.manaplastic.backend.service.AttendanceLogService;
import com.manaplastic.backend.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/checkInApp/attendanceLog")
public class AttendanceLogController {
    @Autowired
    private AttendanceLogService attendanceLogService;

    @Autowired
    private UserRepository userRepository;

    //Tên thư mục lưu ảnh
//    private static final String UPLOAD_DIR = "uploads/"; ==> hard-code

    @Value("${app.upload.attendance}")
    private String uploadDir;

    @PostMapping("/log")
    public ResponseEntity<?> receiveAttendanceLog(@RequestBody AttendanceLogRequest request) {
        try {
            // Tìm User - App đang gửi String (QR Code), cần parse sang Integer nếu ID trong DB là số
            Integer userId = Integer.parseInt(request.getUserId().toString());
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại!"));

            // Xử lý lưu ảnh từ Base64
            String imgPath = saveImageFromBase64(request.getImageBase64());

            AttendancelogEntity logEntity = new AttendancelogEntity();
            logEntity.setUserID(user);
            logEntity.setTimestamp(Instant.now());
            logEntity.setImgUrl(imgPath);

            attendanceLogService.processAttendanceLog(logEntity);

            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Chấm công thành công\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // Hàm phụ trợ để lưu file (Giống file_put_contents của PHP)
    private String saveImageFromBase64(String base64Str) {
        try {
            Path uploadPath = Paths.get(uploadDir);
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
            return "/uploads/attendance" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi lưu ảnh: " + e.getMessage());
        }
    }
}
