package com.manaplastic.backend.constant.customAnotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.entity.ActivitylogEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.ActivityLogRepository;
import com.manaplastic.backend.repository.UserRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
public class ActivityLogAspect {

    @Autowired
    private ActivityLogRepository logRepo;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @AfterReturning(pointcut = "@annotation(logActivity)", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, LogActivity logActivity, Object result) {
        try {
            ActivitylogEntity log = new ActivitylogEntity();

            // Lấy thông tin Action từ Annotation
            log.setAction(logActivity.action());
//            log.setLogType(LogType.valueOf("INFO"));
            log.setLogType(LogType.valueOf(logActivity.logType().name()));
            log.setActiontime(LocalDateTime.now());

            // Lấy người dùng hiện tại từ Security Context
            try {
                String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
                log.setUsername(currentUsername);
                Optional<UserEntity> userOpt = userRepository.findByUsername(currentUsername);

                if (userOpt.isPresent()) {
                    log.setUserID(userOpt.get()); // Set cả object UserEntity vào
                } else {
                    // Trường hợp không tìm thấy user (hiếm khi xảy ra nếu đã login)
                    log.setDetails("User ID not found for: " + currentUsername);
                }
            } catch (Exception e) {
                log.setUsername("Unknown/System");
            }

            // Convert kết quả trả về (Object result) thành JSON để lưu vào details
            // Nếu hàm trả về String (như trong MonthlyConfigService), nó sẽ lưu chuỗi đó
            // Nếu hàm trả về Entity, nó sẽ lưu full JSON của Entity
            String detailsJson = "";
            if (result != null) {
                try {
                    detailsJson = objectMapper.writeValueAsString(result);
                } catch (Exception e) {
                    detailsJson = "Result: " + result.toString();
                }
            } else {
                detailsJson = "Thao tác thành công (Không có dữ liệu trả về)";
            }

            if (!logActivity.description().isEmpty()) {
                detailsJson = "Desc: " + logActivity.description() + " | Data: " + detailsJson;
            }

            log.setDetails(detailsJson);

            logRepo.save(log);

        } catch (Exception e) {
            System.err.println("Lỗi khi lưu Activity Log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
