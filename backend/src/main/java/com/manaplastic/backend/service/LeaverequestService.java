package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.AddLeaverequestDTO;
import com.manaplastic.backend.DTO.LeaveBalanceDTO;
import com.manaplastic.backend.DTO.LeaveRequestFilterCriteria;
import com.manaplastic.backend.DTO.LeaverequestDTO;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.filters.LeaveRequestFilter;
import com.manaplastic.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaverequestService {
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeavePolicyRepository leavePolicyRepository;


    //Tạo đơn - đăng ký đơn
    @Transactional
    public LeaverequestDTO createLeaveRequest(AddLeaverequestDTO dto, UserEntity currentUserId) {
        //Buoccws kiểm tra số dư ngày phép
        ShiftEntity leaveTypeShift = shiftRepository.findByShiftname(dto.leavetype())
                .orElseThrow(() -> new RuntimeException("Loại phép " + dto.leavetype() + " không tồn tại."));

        int shiftId = leaveTypeShift.getId();
        int year = dto.startdate().getYear();

        // Tính số ngày nhân viên yêu cầu (bao gồm cả từ ngày bắt đầu và kết thúc)
        long requestedDays = ChronoUnit.DAYS.between(dto.startdate(), dto.enddate()) + 1;
        if (requestedDays <= 0) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
        }

        // Tìm số dư phép tương ứng (VD: AL của năm 2025)
        LeavebalanceEntityId balanceId = new LeavebalanceEntityId();
        balanceId.setUserID(currentUserId.getId());
        balanceId.setLeaveTypeId(shiftId);
        balanceId.setYear(year);

        Optional<LeavebalanceEntity> balanceOpt = leaveBalanceRepository.findById(balanceId);

        if (balanceOpt.isPresent()) {
            LeavebalanceEntity balance = balanceOpt.get();
            int totalAvailable = balance.getTotalGranted() + balance.getCarriedOver();
            int remaining = totalAvailable - balance.getDaysUsed();

            if (requestedDays > remaining) {
                throw new IllegalArgumentException(
                        String.format("Ngày phép không hợp lệ. Bạn yêu cầu %d ngày, nhưng chỉ còn lại %d ngày (%s - %d).",
                                requestedDays, remaining, dto.leavetype(), year)
                );
            }
        }

        //Sau khi đã kiểm tra các số dư ngày phép, nếu trường hopj nhân viên nghỉ không lương (UL - Unpaid Leave) thì vẫn tạo được voiws leaveType là UL
        LeaverequestEntity newRequest = new LeaverequestEntity();

        newRequest.setLeavetype(dto.leavetype());
        newRequest.setStartdate(dto.startdate());
        newRequest.setEnddate(dto.enddate());
        newRequest.setReason(dto.reason());


        newRequest.setUserID(currentUserId);
        newRequest.setRequestdate(LocalDate.now());
        newRequest.setStatus(LeaverequestEntity.LeaverequestStatus.PENDING);

        LeaverequestEntity savedRequest = leaveRequestRepository.save(newRequest);

        return mapToDTO(savedRequest);
    }
    private LeaverequestDTO mapToDTO(LeaverequestEntity entity) {
        return new LeaverequestDTO(
                entity.getId(),
                entity.getUserID().getUsername(),
                entity.getUserID().getFullname(),
                entity.getLeavetype(),
                entity.getStartdate(),
                entity.getEnddate(),
                entity.getReason(),
                entity.getStatus().name(),
                entity.getRequestdate()
        );
    }

    // Lấy danh sách đơn - xem đơn cảu tôi
    public List<LeaverequestDTO> getMyLeaveRequests(UserEntity currentUserId) {
        List<LeaverequestEntity> requests = leaveRequestRepository.findByUserIDOrderByRequestdateDesc(currentUserId);

        return requests.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // xóa đơn (PENDING)
    @Transactional
    public void deleteMyLeaveRequest(Integer leaveRequestId, UserEntity currentUserId) {
        int rowsAffected = leaveRequestRepository.deleteByIdAndUserIDAndStatus(
                leaveRequestId,
                currentUserId,
                LeaverequestEntity.LeaverequestStatus.PENDING
        );

        if (rowsAffected == 0) {
            throw new RuntimeException("Không thể xóa đơn. Đơn không tồn tại, không thuộc về bạn, hoặc đã được xử lý.");
        }
    }

    // Danh sách đã lọc
    public List<LeaverequestDTO> getFilteredRequests(LeaveRequestFilterCriteria criteria) {

        Specification<LeaverequestEntity> spec = LeaveRequestFilter.withCriteria(criteria);
        List<LeaverequestEntity> requests = leaveRequestRepository.findAll(spec);

        return requests.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Duyệt đơn
    @Transactional
    public void approveRequest(Integer leaveRequestId) {
        LeaverequestEntity request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nghỉ phép."));

        if (request.getStatus() != LeaverequestEntity.LeaverequestStatus.PENDING) {
            throw new RuntimeException("Đơn này đã được xử lý, không thể duyệt.");
        }

        request.setStatus(LeaverequestEntity.LeaverequestStatus.APPROVED);
        LeaverequestEntity savedRequest = leaveRequestRepository.save(request);

        updateLeaveBalanceOnApproval(savedRequest);// Trừ ngày phép

        String email = savedRequest.getUserID().getEmail();
        String fullname = savedRequest.getUserID().getFullname();

        emailService.sendApprovalEmail(email, fullname, savedRequest);
    }

    private void updateLeaveBalanceOnApproval(LeaverequestEntity approvedRequest) {
        ShiftEntity leaveTypeShift = shiftRepository.findByShiftname(approvedRequest.getLeavetype())
                .orElse(null);

        if (leaveTypeShift == null) return;

        LocalDate start = approvedRequest.getStartdate();
        LocalDate end = approvedRequest.getEnddate();
        int startYear = start.getYear();
        int endYear = end.getYear();

        UserEntity user = approvedRequest.getUserID();

        if (startYear == endYear) {
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            deductLeaveDays(user, leaveTypeShift, startYear, days);
            return;
        }

        // Năm đầu
        LocalDate endOfStartYear = LocalDate.of(startYear, 12, 31);
        long daysInStartYear = ChronoUnit.DAYS.between(start, endOfStartYear) + 1;

        // Năm sau
        LocalDate startOfEndYear = LocalDate.of(endYear, 1, 1);
        long daysInEndYear = ChronoUnit.DAYS.between(startOfEndYear, end) + 1;


        deductLeaveDays(user, leaveTypeShift, startYear, daysInStartYear);
        deductLeaveDays(user, leaveTypeShift, endYear, daysInEndYear);
    }

    private void deductLeaveDays(UserEntity user, ShiftEntity leaveType, int year, long daysToDeduct) {

        LeavebalanceEntityId id = new LeavebalanceEntityId();
        id.setUserID(user.getId());
        id.setLeaveTypeId(leaveType.getId());
        id.setYear(year);

        Optional<LeavebalanceEntity> opt = leaveBalanceRepository.findById(id);

        if (opt.isPresent()) {
            LeavebalanceEntity balance = opt.get();
            balance.setDaysUsed(balance.getDaysUsed() + (int) daysToDeduct);
            leaveBalanceRepository.save(balance);
        } else {
            // Nếu năm sau chưa có leave balance, tự động tạo
            LeavebalanceEntity newBalance = new LeavebalanceEntity();
            newBalance.setId(id);
            newBalance.setUserID(user);
            newBalance.setLeaveType(leaveType);


            newBalance.setTotalGranted(0);
            newBalance.setCarriedOver(0);
            newBalance.setDaysUsed((int) daysToDeduct);

            leaveBalanceRepository.save(newBalance);
        }
    }


    // Từ chối đơn
    @Transactional
    public void rejectRequest(Integer leaveRequestId) {
        LeaverequestEntity request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nghỉ phép."));

        if (request.getStatus() != LeaverequestEntity.LeaverequestStatus.PENDING) { // tránh tình trạng đã duyệt rô mà đi từ chối lại
            throw new RuntimeException("Đơn này đã được xử lý, không thể từ chối.");
        }

        request.setStatus(LeaverequestEntity.LeaverequestStatus.REJECTED);
        LeaverequestEntity savedRequest = leaveRequestRepository.save(request);

        String email = savedRequest.getUserID().getEmail();
        String fullname = savedRequest.getUserID().getFullname();

        emailService.sendRejectionEmail(email, fullname, savedRequest);
    }

    //Tạo số dư mới cho nhân sự mới
    @Transactional
    public void createUser(UserEntity user) {
        userRepository.save(user); // Lưu user trước

        int currentYear = LocalDate.now().getYear();

        // Lấy TẤT CẢ các loại phép có trong bảng 'shifts' (AL, SL, PL, ML...)
        List<ShiftEntity> leaveTypes = shiftRepository.findAllLeaveTypes();

        for (ShiftEntity leaveType : leaveTypes) {
            int daysToGrant = 0;

            List<LeavepolicyEntity> policies = leavePolicyRepository.findPolicyMatches(
                    leaveType.getShiftnameAsEnum(),
                    0,
                    user.getJobType()
            );
            Optional<LeavepolicyEntity> policyOpt = policies.stream().findFirst();

            if (policyOpt.isPresent()) {
                daysToGrant = policyOpt.get().getDays();
            }

            // Tạo bản ghi số dư
            LeavebalanceEntityId id = new LeavebalanceEntityId();
            id.setUserID(user.getId());
            id.setLeaveTypeId(leaveType.getId());
            id.setYear(currentYear);

            LeavebalanceEntity newBalance = new LeavebalanceEntity();
            newBalance.setId(id);
            newBalance.setUserID(user);
            newBalance.setLeaveType(leaveType);
            newBalance.setTotalGranted(daysToGrant);
            newBalance.setCarriedOver(0);
            newBalance.setDaysUsed(0);

            leaveBalanceRepository.save(newBalance);
        }
    }

    private int calculateCarryOver(Integer userId, int previousYear) {
        Optional<ShiftEntity> alShiftOpt = shiftRepository.findByShiftname("AL (Anually Leave)");

        if (alShiftOpt.isEmpty()) {
            return 0;
        }

        LeavebalanceEntityId oldId = new LeavebalanceEntityId();
        oldId.setUserID(userId);
        oldId.setLeaveTypeId(alShiftOpt.get().getId());
        oldId.setYear(previousYear);

        Optional<LeavebalanceEntity> oldBalanceOpt = leaveBalanceRepository.findById(oldId);

        if (oldBalanceOpt.isPresent()) {
            LeavebalanceEntity oldBalance = oldBalanceOpt.get();
            int remaining = (oldBalance.getTotalGranted() + oldBalance.getCarriedOver())
                    - oldBalance.getDaysUsed();

            int MAX_CARRY_OVER_DAYS = 3;

            return Math.max(0, Math.min(remaining, MAX_CARRY_OVER_DAYS));
        }

        return 0;
    }

    @Scheduled(cron = "0 5 0 1 1 *") // 5 phút sáng 1/1
    public void generateLeaveBalanceForNewYear() {
        int newYear = LocalDate.now().getYear();
        List<UserEntity> users = userRepository.findAllActiveUsers();

        for (UserEntity user : users) {
            int thamNien = newYear - user.getHiredate().getYear();
            int phepTon = calculateCarryOver(user.getId(), newYear - 1);

            List<ShiftEntity> leaveTypes = shiftRepository.findAllLeaveTypes();

            for (ShiftEntity leaveType : leaveTypes) {

                int daysToGrant = 0;
                List<LeavepolicyEntity> policies = leavePolicyRepository.findPolicyMatches(
                        leaveType.getShiftnameAsEnum(),
                        thamNien,
                        user.getJobType()
                );
                Optional<LeavepolicyEntity> policyOpt = policies.stream().findFirst();

                if (policyOpt.isPresent()) {
                    daysToGrant = policyOpt.get().getDays();
                }

                LeavebalanceEntityId id = new LeavebalanceEntityId();
                id.setUserID(user.getId());
                id.setLeaveTypeId(leaveType.getId());
                id.setYear(newYear);

                // Tìm xem bản ghi năm mới đã tồn tại chưa
                // (Ví dụ: do đơn vắt năm tạo ra)
                Optional<LeavebalanceEntity> optBalance = leaveBalanceRepository.findById(id);

                LeavebalanceEntity balanceRecord;
                if (optBalance.isPresent()) {
                    // ĐÃ TỒN TẠI (do đơn vắt năm): Chỉ cần cập nhật
                    balanceRecord = optBalance.get();
                } else {
                    // CHƯA TỒN TẠI: Thì tạo mới
                    balanceRecord = new LeavebalanceEntity();
                    balanceRecord.setId(id);
                    balanceRecord.setUserID(user);
                    balanceRecord.setLeaveType(leaveType);
                    balanceRecord.setDaysUsed(0);
                }

                //Các số dư phép cho năm mới chỉ được giữ lại là phép năm - AL
                balanceRecord.setTotalGranted(daysToGrant);
                if (leaveType.getShiftname().startsWith("AL")) {
                    balanceRecord.setCarriedOver(phepTon);
                } else {
                    balanceRecord.setCarriedOver(0);
                }

                leaveBalanceRepository.save(balanceRecord);
            }
        }
    }

    //Laays số dư cuar tôi
    public List<LeaveBalanceDTO> getMyLeaveBalances(UserEntity currentUser) {
        List<LeavebalanceEntity> balances = leaveBalanceRepository.findByUserID(currentUser);

        return balances.stream()
                .map(this::mapBalanceToDTO)
                .collect(Collectors.toList());
    }

    private LeaveBalanceDTO mapBalanceToDTO(LeavebalanceEntity entity) {
        int totalAvailable = entity.getTotalGranted() + entity.getCarriedOver();
        int remaining = totalAvailable - entity.getDaysUsed();

        return new LeaveBalanceDTO(
                entity.getLeaveType().getShiftname(),
                entity.getLeaveType().getId(),
                entity.getId().getYear(),
                entity.getTotalGranted(),
                entity.getCarriedOver(),
                entity.getDaysUsed(),
                remaining // Trả về số phép CÒN LẠI
        );
    }
}
