package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.account.AdminUserDTO;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private LeavePolicyRepository leavePolicyRepository;
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Transactional(rollbackFor = Exception.class)
    public UserEntity createUser(UserEntity newUser) {
        String password = String.valueOf(newUser.getCccd()); // lấy số cccd làm pass

        if (password == null && password.trim().length() != 12 && userRepository.existsByCccd(newUser.getCccd())) {
            throw new IllegalArgumentException("Trường 'cccd' không hợp lệ hoặc đã tồn tại");
        }

        if (password != null) {
            newUser.setPassword(passwordEncoder.encode(password));
        }

        if (newUser.getFullname() == null || newUser.getFullname().trim().isEmpty()) {
            throw new IllegalArgumentException("Trường 'fullName' không được để trống.");
        }

        if (newUser.getGender() == null) {
            throw new IllegalArgumentException("Trường 'gender' là bắt buộc để tính toán ngày phép.");
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
        UserEntity savedUser = userRepository.save(newUser);

        createInitialLeaveBalances(savedUser);

        return savedUser;
    }

    private void createInitialLeaveBalances(UserEntity user) {
        int currentYear = LocalDate.now().getYear();

        List<ShiftEntity> leaveTypes = shiftRepository.findAllLeaveTypes();

        for (ShiftEntity leaveType : leaveTypes) {
            int daysToGrant = 0;

            List<LeavepolicyEntity> policies = leavePolicyRepository.findPolicyMatches(
                    leaveType.getShiftnameAsEnum(),
                    0, // Nhân viên mới tinh thì thâm niên là 0
                    user.getJobtype(),
                    user.getGender()
            );

            Optional<LeavepolicyEntity> policyOpt = policies.stream().findFirst();

            if (policyOpt.isPresent()) {
                daysToGrant = policyOpt.get().getDays();
            }

            LeavebalanceEntityId id = new LeavebalanceEntityId();
            id.setUserID(user.getId());      // Lấy ID từ user vừa save xong
            id.setLeavetypeid(leaveType.getId());
            id.setYear(currentYear);

            // Tạo bản ghi số dư
            LeavebalanceEntity newBalance = new LeavebalanceEntity();
            newBalance.setId(id);
            newBalance.setUserID(user);
            newBalance.setLeaveType(leaveType);
//            newBalance.setTotalGranted(daysToGrant);
            if (leaveType.getShiftname().startsWith("AL")) {
                newBalance.setTotalGranted(0); // Chưa làm tháng nào thì chưa tích dc AL
            } else {
                newBalance.setTotalGranted(daysToGrant);
            }
            newBalance.setCarriedOver(0); // Mới vào chưa có phép tồn
            newBalance.setDaysUsed(0);    // Mới vào chưa dùng phép
            newBalance.setLastUpdated(Instant.now());

            leaveBalanceRepository.save(newBalance);
        }
    }

    public List<AdminUserDTO> getAllUsersForDropdown() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(u -> new AdminUserDTO(
                        u.getId(),
                        u.getFullname(),
                        u.getUsername(),
                        u.getDepartmentID() != null ? u.getDepartmentID().getDepartmentname() : "N/A"
                ))
                .collect(Collectors.toList());
    }
}
