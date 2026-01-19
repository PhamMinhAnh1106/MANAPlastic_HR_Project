package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.schedule.AddLeaverequestDTO;
import com.manaplastic.backend.DTO.schedule.LeaveBalanceDTO;
import com.manaplastic.backend.DTO.criteria.LeaveRequestFilterCriteria;
import com.manaplastic.backend.DTO.schedule.LeaverequestDTO;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.filters.LeaveRequestFilter;
import com.manaplastic.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private ActivityLogRepository activityLogRepository;
    @Autowired
    private EmployeeOfficialScheduleRepository scheduleRepository;
    @Autowired
    private EmployeeDocumentRepository documentRepo;

    private String getShiftNameFromEnum(LeaverequestEntity.LeaveType type) {
        switch (type) {
            case ANNUAL: return "AL (Anually Leave)";
            case SICK: return "SL (Sick Leave)";
            case MATERNITY: return "ML (Maternity Leave)";
            case PATERNITY: return "PL (Paternity Leave)";
            case UNPAID: return "UL (Unpaid Leave)";
            default: throw new RuntimeException("Không tìm thấy Ca làm việc tương ứng với: " + type);
        }
    }

    //Tạo đơn - đăng ký đơn
    @Transactional
    public LeaverequestDTO createLeaveRequest(AddLeaverequestDTO dto, UserEntity currentUserId) {
        //Buoccws kiểm tra số dư ngày phép
        LeaverequestEntity.LeaveType reqType;
        try {
            reqType = LeaverequestEntity.LeaveType.valueOf(dto.leavetype());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Loại phép không hợp lệ: " + dto.leavetype());
        }

        // Tìm ShiftEntity dựa trên Enum đã map
        String shiftNameInDB = getShiftNameFromEnum(reqType);
        ShiftEntity leaveTypeShift = shiftRepository.findByShiftname(shiftNameInDB)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại ca trong DB: " + shiftNameInDB));

        int shiftId = leaveTypeShift.getId();
        int year = dto.startdate().getYear();

        // Tính số ngày nhân viên yêu cầu (bao gồm cả từ ngày bắt đầu và kết thúc)
        long requestedDays = ChronoUnit.DAYS.between(dto.startdate(), dto.enddate()) + 1;
        if (requestedDays <= 0) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
        }

        if(dto.startdate().isBefore(LocalDate.now().minusDays(7))) {
            throw new IllegalArgumentException("Bạn không thể tạo đơn cho ngày trong quá khứ lố 1 tuần.");
        }

        if (leaveRequestRepository.existsOverlappingRequest(currentUserId, dto.startdate(), dto.enddate())) {
            throw new IllegalArgumentException("Bạn đã có đơn nghỉ phép trùng với khoảng thời gian này.");
        }

        if (reqType == LeaverequestEntity.LeaveType.MATERNITY) {
            boolean isEligible = documentRepo.isDocumentActive(
                    currentUserId.getId(),
                    dto.startdate(), // Check tại thời điểm bắt đầu nghỉ
                    EmployeeDocumentEntity.DocumentType.MEDICAL_PREGNANCY,
                    EmployeeDocumentEntity.DocumentStatus.APPROVED
            );

            if (!isEligible) {
                throw new IllegalArgumentException(
                        "Không thể tạo đơn Thai sản: Hồ sơ thai sản (MEDICAL_PREGNANCY) của bạn chưa được duyệt " +
                                "hoặc không có hiệu lực vào ngày " + dto.startdate()
                );
            }
        }

        // Tìm số dư phép tương ứng (VD: AL của năm 2025)
        LeavebalanceEntityId balanceId = new LeavebalanceEntityId();
        balanceId.setUserID(currentUserId.getId());
        balanceId.setLeavetypeid(shiftId);
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

        newRequest.setLeavetype(reqType);
        newRequest.setShiftID(leaveTypeShift);
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
                entity.getLeavetype().name(),
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
//    public List<LeaverequestDTO> getFilteredRequests(LeaveRequestFilterCriteria criteria) {
//
//        Specification<LeaverequestEntity> spec = LeaveRequestFilter.withCriteria(criteria);
//        List<LeaverequestEntity> requests = leaveRequestRepository.findAll(spec);
//
//        return requests.stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }
    public Page<LeaverequestDTO> getFilteredRequests(LeaveRequestFilterCriteria criteria, Pageable pageable) {
        Specification<LeaverequestEntity> spec = LeaveRequestFilter.withCriteria(criteria);
        Page<LeaverequestEntity> result = leaveRequestRepository.findAll(spec, pageable);
        return result.map(this::mapToDTO);
    }


    // Duyệt đơn
    @Transactional
    public void approveRequest(Integer leaveRequestId, UserEntity approver) {
        LeaverequestEntity request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nghỉ phép."));

        if (request.getStatus() != LeaverequestEntity.LeaverequestStatus.PENDING) {
            throw new RuntimeException("Đơn này đã được xử lý, không thể duyệt.");
        }
        UserEntity requester = request.getUserID();

        boolean isManager = approver.getRoleID() != null && "Manager".equalsIgnoreCase(approver.getRoleID().getRolename());

        // Nếu là Manager
        if (isManager) {
//            if (approver.getId().equals(requester.getId())) {
//                throw new RuntimeException("Manager không thể tự duyệt đơn nghỉ phép của chính mình.");
//            }

            if (approver.getDepartmentID() == null) {
                throw new RuntimeException("Lỗi hệ thống: Tài khoản Manager của bạn chưa được gán Phòng ban.");
            }
            if (requester.getDepartmentID() == null) {
                throw new RuntimeException("Không thể duyệt: Nhân viên tạo đơn chưa thuộc phòng ban nào.");
            }

            Integer approverDeptId = approver.getDepartmentID().getId();
            Integer requesterDeptId = requester.getDepartmentID().getId();

            if (!approverDeptId.equals(requesterDeptId)) {
                throw new RuntimeException("Truy cập bị từ chối: Bạn chỉ có quyền duyệt đơn cho nhân viên thuộc phòng ban của mình!");
            }
        }
        // Là HR
        request.setStatus(LeaverequestEntity.LeaverequestStatus.APPROVED);
        LeaverequestEntity savedRequest = leaveRequestRepository.save(request);

        updateLeaveBalanceOnApproval(savedRequest);// Trừ ngày phép
        updateScheduleForLeaveRange(savedRequest);
        logLeaveApprovalAction(approver, requester, savedRequest);

        String email = savedRequest.getUserID().getEmail();
        String fullname = savedRequest.getUserID().getFullname();

        emailService.sendApprovalEmail(email, fullname, savedRequest);
    }

    // Từ chối đơn
    @Transactional
    public void rejectRequest(Integer leaveRequestId, UserEntity rejecter) {
        LeaverequestEntity request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nghỉ phép."));

        if (request.getStatus() != LeaverequestEntity.LeaverequestStatus.PENDING) { // tránh tình trạng đã duyệt rô mà đi từ chối lại
            throw new RuntimeException("Đơn này đã được xử lý, không thể từ chối.");
        }

        UserEntity requester = request.getUserID();
        boolean isManager = rejecter.getRoleID() != null
                && "Manager".equalsIgnoreCase(rejecter.getRoleID().getRolename());

        if (isManager) {
//            if (rejecter.getId().equals(requester.getId())) {
//                throw new RuntimeException("Manager không thể tự xử lý đơn nghỉ phép của chính mình.");
//            }

            if (rejecter.getDepartmentID() == null || requester.getDepartmentID() == null) {
                throw new RuntimeException("Lỗi dữ liệu: Nhân viên hoặc Quản lý chưa được gán phòng ban.");
            }

            Integer approverDeptId = rejecter.getDepartmentID().getId();
            Integer requesterDeptId = requester.getDepartmentID().getId();

            if (!approverDeptId.equals(requesterDeptId)) {
                throw new RuntimeException("Truy cập bị từ chối: Bạn chỉ được xử lý đơn của nhân viên cùng phòng ban!");
            }
        }
            // Là HR
        request.setStatus(LeaverequestEntity.LeaverequestStatus.REJECTED);
        LeaverequestEntity savedRequest = leaveRequestRepository.save(request);

        String email = savedRequest.getUserID().getEmail();
        String fullname = savedRequest.getUserID().getFullname();

        emailService.sendRejectionEmail(email, fullname, savedRequest);
    }

    private void updateLeaveBalanceOnApproval(LeaverequestEntity approvedRequest) {
        ShiftEntity leaveTypeShift = approvedRequest.getShiftID();

        if (leaveTypeShift == null) {
            String shiftName = getShiftNameFromEnum(approvedRequest.getLeavetype());
            leaveTypeShift = shiftRepository.findByShiftname(shiftName).orElse(null);
        } // cho db cũ khi chưa có cột shiftID mới trong leaverequest

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
        id.setLeavetypeid(leaveType.getId());
        id.setYear(year);

        Optional<LeavebalanceEntity> opt = leaveBalanceRepository.findById(id);

        if (opt.isPresent()) {
            LeavebalanceEntity balance = opt.get();
            balance.setDaysUsed(balance.getDaysUsed() + (int) daysToDeduct);
            leaveBalanceRepository.save(balance);
        } else {
            // Nếu năm sau chưa có leave balance, tự động tạo
//            LeavebalanceEntity newBalance = new LeavebalanceEntity();
//            newBalance.setId(id);
//            newBalance.setUserID(user);
//            newBalance.setLeaveType(leaveType);
//
//
//            newBalance.setTotalGranted(0);
//            newBalance.setCarriedOver(0);
//            newBalance.setDaysUsed((int) daysToDeduct);
//
//            leaveBalanceRepository.save(newBalance);
            throw new RuntimeException("Chưa có dữ liệu ngày phép cho năm " + year + ". Vui lòng liên hệ HR hoặc chạy job quyết toán năm.");
        }
    }

    // Ghi log  duyệt nghỉ phép
    private void logLeaveApprovalAction(UserEntity approver, UserEntity requester, LeaverequestEntity request) {
        ActivitylogEntity log = new ActivitylogEntity();
        log.setUserID(approver); // Người duyệt (HR hoặc Manager)
        log.setActiontime(LocalDateTime.now());

        // Phân loại Action dựa trên Role người duyệt
        String approverRole = (approver.getRoleID() != null) ? approver.getRoleID().getRolename() : "Unknown";

        if ("Manager".equalsIgnoreCase(approverRole)) {
            log.setAction("MANAGER_APPROVE_LEAVE");
            log.setLogType(LogType.INFO); // Dùng Enum LogType.INFO nếu đã define
            log.setDetails("Manager " + approver.getFullname() + " duyệt đơn nghỉ phép cho nhân viên: " + requester.getFullname());
        } else {
            // Mặc định là HR hoặc Admin
            log.setAction("HR_APPROVE_LEAVE");
            log.setLogType(LogType.INFO);
            log.setDetails("HR " + approver.getFullname() + " duyệt hoàn tất đơn nghỉ phép cho: " + requester.getFullname());
        }

        // Bổ sung thông tin ngày vào details
        String dateRange = " | Từ: " + request.getStartdate() + " Đến: " + request.getEnddate();
        log.setDetails(log.getDetails() + dateRange + " (Mã đơn: " + request.getId() + ")");

        activityLogRepository.save(log);
    }

    private void updateScheduleForLeaveRange(LeaverequestEntity request) {
        UserEntity employee = request.getUserID();
        LocalDate startDate = request.getStartdate();
        LocalDate endDate = request.getEnddate();

        // Lấy ShiftID từ request. Nếu null thì tìm lại theo Enum
        ShiftEntity leaveShift = request.getShiftID();
        if (leaveShift == null) {
            String shiftName = getShiftNameFromEnum(request.getLeavetype());
            leaveShift = shiftRepository.findByShiftname(shiftName).orElse(null);
        }

        if (leaveShift == null) return; // Không tìm thấy ca làm việc, không update lịch

        final ShiftEntity finalShift = leaveShift;

        // Duyệt qua từng ngày từ start -> end
        startDate.datesUntil(endDate.plusDays(1)).forEach(date -> {
            updateOfficialSchedule(employee, date, finalShift);
        });
    }

    // Cập nhật/Tạo lịch làm việc cho 1 ngày cụ thể
    private void updateOfficialSchedule(UserEntity employee, LocalDate date, ShiftEntity newShift) {
        EmployeeofficialscheduleEntity schedule = scheduleRepository.findByEmployeeIDAndDate(employee, date)
                .orElse(new EmployeeofficialscheduleEntity());

        schedule.setEmployeeID(employee);
        schedule.setDate(date);
        schedule.setShiftID(newShift);

        // Nếu newShift null thì là ngày nghỉ, nhưng mà ở đây newShift luôn là loại ca "Leave" (AL, SL...)
        schedule.setIsDayOff(newShift == null);

        // Format tháng năm (MM-yyyy)
        schedule.setMonthYear(date.format(DateTimeFormatter.ofPattern("MM-yyyy")));
        schedule.setPublishedDate(Instant.now());

        scheduleRepository.save(schedule);
    }


//    //Tạo số dư mới cho nhân sự mới
//    @Transactional
//    public void createUser(UserEntity user) {
//        userRepository.save(user); // Lưu user trước
//
//        int currentYear = LocalDate.now().getYear();
//
//        // Lấy TẤT CẢ các loại phép có trong bảng 'shifts' (AL, SL, PL, ML...)
//        List<ShiftEntity> leaveTypes = shiftRepository.findAllLeaveTypes();
//
//        for (ShiftEntity leaveType : leaveTypes) {
//            int daysToGrant = 0;
//
//            List<LeavepolicyEntity> policies = leavePolicyRepository.findPolicyMatches(
//                    leaveType.getShiftnameAsEnum(),
//                    0,
//                    user.getJobtype()
//            );
//            Optional<LeavepolicyEntity> policyOpt = policies.stream().findFirst();
//
//            if (policyOpt.isPresent()) {
//                daysToGrant = policyOpt.get().getDays();
//            }
//
//            // Tạo bản ghi số dư
//            LeavebalanceEntityId id = new LeavebalanceEntityId();
//            id.setUserID(user.getId());
//            id.setLeavetypeid(leaveType.getId());
//            id.setYear(currentYear);
//
//            LeavebalanceEntity newBalance = new LeavebalanceEntity();
//            newBalance.setId(id);
//            newBalance.setUserID(user);
//            newBalance.setLeaveType(leaveType);
//            newBalance.setTotalGranted(daysToGrant);
//            newBalance.setCarriedOver(0);
//            newBalance.setDaysUsed(0);
//
//            leaveBalanceRepository.save(newBalance);
//        }
//    }

    private int calculateCarryOver(Integer userId, int previousYear) {
        Optional<ShiftEntity> alShiftOpt = shiftRepository.findByShiftname("AL (Anually Leave)");

        if (alShiftOpt.isEmpty()) {
            return 0;
        }

        LeavebalanceEntityId oldId = new LeavebalanceEntityId();
        oldId.setUserID(userId);
        oldId.setLeavetypeid(alShiftOpt.get().getId());
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

    @Scheduled(cron = "0 5 0 1 1 *") // 00:05 phút sáng 1/1 hàng năm
    @Transactional
    public void generateLeaveBalanceForNewYear() {
        int newYear = LocalDate.now().getYear();
        List<UserEntity> users = userRepository.findAllActiveUsers();

        for (UserEntity user : users) {

            if (user.getHiredate() == null) {
                System.err.println("Cảnh báo: User " + user.getUsername() + " (ID: " + user.getId() + ") chưa có ngày vào làm (hiredate). Bỏ qua tính toán.");
                continue;
            }

            int thamNien = newYear - user.getHiredate().getYear();
            int phepTon = calculateCarryOver(user.getId(), newYear - 1);

            List<ShiftEntity> leaveTypes = shiftRepository.findAllLeaveTypes();

            for (ShiftEntity leaveType : leaveTypes) {

                int daysToGrant = 0;
                List<LeavepolicyEntity> policies = leavePolicyRepository.findPolicyMatches(
                        leaveType.getShiftnameAsEnum(),
                        thamNien,
                        user.getJobtype(),
                        user.getGender()
                );
                Optional<LeavepolicyEntity> policyOpt = policies.stream().findFirst();

                if (policyOpt.isPresent()) {
                    daysToGrant = policyOpt.get().getDays();
                }

                LeavebalanceEntityId id = new LeavebalanceEntityId();
                id.setUserID(user.getId());
                id.setLeavetypeid(leaveType.getId());
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
                    balanceRecord.setTotalGranted(0); // lam toi tháng nào thì nhận thêm AL tháng đó
                    balanceRecord.setCarriedOver(phepTon);
                } else {
                    balanceRecord.setCarriedOver(0);
                }

                leaveBalanceRepository.save(balanceRecord);
            }
        }
    }

    // Chạy vào 00:30 ngày 1 hàng tháng (Chạy sau job năm mới một chút để tránh conflict ngày 1/1)
    @Scheduled(cron = "0 30 0 1 * *")
    @Transactional
    public void accrueMonthlyLeave() { // hàm +1 AL vào mỗi tháng
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        ShiftEntity alShift = shiftRepository.findByShiftname("AL (Anually Leave)")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phép AL"));

        List<UserEntity> users = userRepository.findAllActiveUsers();

        for (UserEntity user : users) {

            if (user.getHiredate().isAfter(today)) continue; // Kiểm tra ngày vào làm vd: ngày 15 vào thì tháng sau mới +1 AL

            // Tính số ngày được cộng trong 1 tháng
            int thamNien = currentYear - user.getHiredate().getYear();
            int daysPerYear = 12; // Mặc định

            // Tìm policy chuẩn
            Optional<LeavepolicyEntity> policyOpt = leavePolicyRepository.findPolicyMatches(
                    LeavepolicyEntity.LeaveType.ANNUAL,
                    thamNien,
                    user.getJobtype(),
                    user.getGender()
            ).stream().findFirst();

            if (policyOpt.isPresent()) {
                daysPerYear = policyOpt.get().getDays();
            }

            int monthlyAccrual = daysPerYear / 12;

            LeavebalanceEntityId id = new LeavebalanceEntityId(user.getId(), alShift.getId(), currentYear);

            // Tìm bản ghi balance (Đã được tạo ở bước 1 hoặc tạo mới nếu user mới vào)
            LeavebalanceEntity balance = leaveBalanceRepository.findById(id).orElseGet(() -> {
                LeavebalanceEntity newB = new LeavebalanceEntity();
                newB.setId(id);
                newB.setUserID(user);
                newB.setLeaveType(alShift);
                newB.setDaysUsed(0);
                newB.setCarriedOver(0);
                newB.setTotalGranted(0);
                return newB;
            });

            // CỘNG DỒN
            balance.setTotalGranted(balance.getTotalGranted() + monthlyAccrual);

            leaveBalanceRepository.save(balance);
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
