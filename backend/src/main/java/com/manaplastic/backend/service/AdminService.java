package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.account.AdminUserDTO;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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
    @Autowired
    private EmailService emailService;
    @Autowired
    private ContractRepository contractRepository;
    @Value("${app.upload.contracts}")
    private String uploadDir;

    // @Transactional(rollbackFor = Exception.class)
//    public UserEntity createUser(UserEntity newUser) {
////        String password = String.valueOf(newUser.getCccd()); // lấy số cccd làm pass
//
////        if (password == null || password.trim().length() != 12 || userRepository.existsByCccd(newUser.getCccd())) {
////            throw new IllegalArgumentException("Trường 'cccd' không hợp lệ hoặc đã tồn tại");
////        }
//
////        if (newUser.getCccd() == null || newUser.getCccd().trim().length() != 12 || userRepository.existsByCccd(newUser.getCccd())) {
////            throw new IllegalArgumentException("Trường 'cccd' không hợp lệ hoặc đã tồn tại");
////        }
//
//        if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty()) {
//            throw new IllegalArgumentException("Trường 'email' là bắt buộc để nhận thông tin tài khoản.");
//        }
//
////        if (password != null) {
////            newUser.setPassword(passwordEncoder.encode(password));
////        }
//
//        if (newUser.getFullname() == null || newUser.getFullname().trim().isEmpty()) {
//            throw new IllegalArgumentException("Trường 'fullName' không được để trống.");
//        }
//
//        if (newUser.getGender() == null) {
//            throw new IllegalArgumentException("Trường 'gender' là bắt buộc để tính toán ngày phép.");
//        }
//
//        String rawPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
//        newUser.setPassword(passwordEncoder.encode(rawPassword));
//
//        newUser.setHiredate(LocalDate.now());
//        newUser.setJobtype("NORMAL");
//        newUser.setSkillGrade(1);
//
//        // lấy username = random + ngày hienej tại

    /// /        final int MAX_RETRIES = 5;
    /// /        for (int i = 0; i < MAX_RETRIES; i++) {
    /// /            int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
    /// /            String randomNumericId = String.valueOf(randomNumber);
    /// /            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd"));
    /// /            String generatedUsername = randomNumericId + currentDate;
    /// /
    /// /            newUser.setUsername(generatedUsername);
    /// /
    /// /            try {
    /// /                return userRepository.save(newUser);
    /// /            } catch (DataIntegrityViolationException e) {
    /// /                if (i == MAX_RETRIES - 1) {
    /// /                    throw new RuntimeException("Không thể tạo username duy nhất sau " + MAX_RETRIES + " lần thử. Vui lòng thử lại sau.", e);
    /// /                }
    /// /            }
    /// /        }
    /// /       throw new RuntimeException("Không thể tạo người dùng.");
//        Integer maxId = userRepository.findMaxId();
//        int nextId = (maxId == null) ? 1 : maxId + 1;
//
//        // Format username 6 số
//        String generatedUsername = String.format("%06d", nextId);
//        newUser.setUsername(generatedUsername);
//        UserEntity savedUser = userRepository.save(newUser);
//
//        createInitialLeaveBalances(savedUser);
//        emailService.sendAccountInfo(savedUser.getEmail(), savedUser.getUsername(), rawPassword);
//        return savedUser;
//    }
    @Transactional(rollbackFor = Exception.class)
    public void activateContractAndAccount(Integer contractId, MultipartFile file) throws Exception {
        String uploadedFileName = null; // Biến để lưu tên file, dùng để xóa nếu lỗi

        try {
            ContractEntity contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            if (!"DRAFT".equals(contract.getStatus())) {
                throw new IllegalArgumentException("Hợp đồng này không ở trạng thái nháp, không thể kích hoạt.");
            }

            // Validate Overlap: Kiểm tra xem ngày bắt đầu có bị trùng với HĐ cũ đang Active không
            int conflictCount = contractRepository.countOverlappingActiveContracts(
                    contract.getUserID().getId(),
                    contract.getStartdate(),
                    contractId
            );

            if (conflictCount > 0) {
                throw new IllegalArgumentException("Ngày bắt đầu của HĐ mới bị trùng với thời gian hiệu lực của HĐ cũ đang Active.");
            }

            //Upload file
            uploadedFileName = storeFile(file);
            contract.setFileurl(uploadedFileName);
            contract.setSigndate(LocalDate.now());

            // QUYẾT ĐỊNH TRẠNG THÁI
            LocalDate today = LocalDate.now();

            // Trường hợp 1: Hợp đồng tương lai (Ngày bắt đầu > Hôm nay)
            // Ví dụ: Hôm nay 15/03, ký cho hợp đồng bắt đầu 01/04
            if (contract.getStartdate().isAfter(today)) {
                contract.setStatus("SIGNED"); // Chuyển sang trạng thái "Đã ký" (Chờ scheduler kích hoạt)
                contractRepository.save(contract);
                // chờ cron
                return;
            }

            // Trường hợp 2: Hợp đồng hiệu lực ngay (Ngày bắt đầu <= Hôm nay)
            contract.setStatus("ACTIVE");
            contractRepository.save(contract);

            // XỬ LÝ TÀI KHOẢN USER (Phân biệt Mới/Cũ)
            UserEntity user = contract.getUserID();

            boolean isNewAccount = (user.getUsername() == null || user.getUsername().isEmpty());// Nếu chưa có username -> Là nhân viên mới

            if (isNewAccount) {
                // mới
                String generatedUsername = String.format("%06d", user.getId());
                String rawPassword = UUID.randomUUID().toString().substring(0, 8);

                user.setUsername(generatedUsername);
                user.setPassword(passwordEncoder.encode(rawPassword));
                user.setHiredate(contract.getStartdate());
                user.setStatus("active");
                userRepository.save(user);

                createInitialLeaveBalances(user);
                emailService.sendAccountInfo(user.getEmail(), generatedUsername, rawPassword,user.getFullname());

            } else {
                //Cũ
                if (!"active".equalsIgnoreCase(user.getStatus())) {
                    user.setStatus("active");
                    userRepository.save(user);
                }

            }

        } catch (Exception e) {
            // Xử lý xóa file rác nếu transaction lỗi
            if (uploadedFileName != null) {
                try {
                    Path filePath = Paths.get(uploadDir).resolve(uploadedFileName);
                    Files.deleteIfExists(filePath);
                    System.out.println("Đã xóa file rác do lỗi transaction: " + uploadedFileName);
                } catch (IOException ex) {
                    System.err.println("Không thể xóa file rác: " + ex.getMessage());
                }
            }
            throw e; // Ném lỗi để Rollback DB
        }
    }

    //Lưu PDF
    private String storeFile(MultipartFile file) throws IOException {
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        // Tạo thư mục nếu chưa có
        Path uploadPath = Paths.get(uploadDir.trim());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Copy file vào thư mục
        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

//        return "/" + uploadDir + "/" + newFileName;
        return newFileName;
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
