package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.AdminUserDTO;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.RoleRepository;
import com.manaplastic.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity createUser(UserEntity newUser) {
        String password = String.valueOf(newUser.getCccd()); // lấy số cccd làm pass

        if (password == null && password.trim().length() != 12) {
            throw new IllegalArgumentException("Trường 'cccd' không hợp lệ");
        }

        if (password != null) {
            newUser.setPassword(passwordEncoder.encode(password));
        }

        if (newUser.getFullname() == null || newUser.getFullname().trim().isEmpty()) {
            throw new IllegalArgumentException("Trường 'fullName' không được để trống.");
        }

        newUser.setHiredate(LocalDate.now());
        newUser.setJobtype("NORMAL");
        newUser.setSkillGrade(1);

        // lấy username = random + ngày hienej tại
//        final int MAX_RETRIES = 5;
//        for (int i = 0; i < MAX_RETRIES; i++) {
//            int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
//            String randomNumericId = String.valueOf(randomNumber);
//            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd"));
//            String generatedUsername = randomNumericId + currentDate;
//
//            newUser.setUsername(generatedUsername);
//
//            try {
//                return userRepository.save(newUser);
//            } catch (DataIntegrityViolationException e) {
//                if (i == MAX_RETRIES - 1) {
//                    throw new RuntimeException("Không thể tạo username duy nhất sau " + MAX_RETRIES + " lần thử. Vui lòng thử lại sau.", e);
//                }
//            }
//        }
//       throw new RuntimeException("Không thể tạo người dùng.");
        Integer maxId = userRepository.findMaxId();
        int nextId = (maxId == null) ? 1 : maxId + 1;

        // Format username 6 số
        String generatedUsername = String.format("%06d", nextId);
        newUser.setUsername(generatedUsername);

        return userRepository.save(newUser);
    }
    public List<AdminUserDTO> getAllUsersForDropdown() {
        // Lấy tất cả user đang hoạt động (active)
        // Giả sử trong UserEntity có phương thức getStatus()
        List<UserEntity> users = userRepository.findAll();

        return users.stream()
                // Chỉ map những trường cần thiết
                .map(u -> new AdminUserDTO(
                        u.getId(), // Hoặc u.getId() tùy entity của bạn
                        u.getFullname(),
                        u.getUsername(),
                        u.getDepartmentID() != null ? u.getDepartmentID().getDepartmentname() : "N/A"
                ))
                .collect(Collectors.toList());
    }
}
