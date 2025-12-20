package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.schedule.ShiftChangeDTO;
import com.manaplastic.backend.DTO.schedule.ShiftChangeFilterCriteria;
import com.manaplastic.backend.DTO.schedule.ShiftChangeListDTO;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.RequestStatus;
import com.manaplastic.backend.entity.*; // Import hết các entity
import com.manaplastic.backend.filters.ShiftChangeFilter;
import com.manaplastic.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftChangeService {
    @Autowired
    private final ShiftChangeRequestRepository requestRepo;
    @Autowired
    private final EmployeeOfficialScheduleRepository scheduleRepo;
    @Autowired
    private final ActivityLogRepository logRepo;
    @Autowired
    private final UserRepository userRepo;

    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    @Autowired
    private final ShiftRepository shiftRepository;
    @Autowired
    private final EmployeeOfficialScheduleRepository employeeOfficialScheduleRepository;

    // Hàm Duyệt yêu cầu
    @Transactional
    public void approveRequest(Integer requestId, Integer currentManagerId) {
        ShiftChangeRequestEntity request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));

        UserEntity manager = getUserOrThrow(currentManagerId);

        UserEntity employee = request.getEmployeeID();

        if (manager.getDepartmentID() == null || employee.getDepartmentID() == null ||
                !manager.getDepartmentID().getId().equals(employee.getDepartmentID().getId())) {
            throw new SecurityException("Không được duyệt đơn khác phòng ban");
        }
        boolean isSelfApproval = manager.getId().equals(employee.getId());

        request.setStatus(RequestStatus.APPROVED);
        request.setApproverID(manager);
        requestRepo.save(request);

        updateOfficialSchedule(employee, request.getDate(), request.getNewShiftID());
        logApprovalAction(manager, employee, request);
    }

    //Yêu cầu đổi ca
    public void createRequest(ShiftChangeDTO dto, Integer currentUserId) {
        UserEntity employee = getUserOrThrow(currentUserId);

        if (dto.getTargetDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Không thể xin đổi ca cho ngày trong quá khứ.");
        }

        if (dto.getTargetDate().isEqual(LocalDate.now())) {
            throw new IllegalArgumentException("Yêu cầu đổi ca phải được gửi trước ít nhất 1 ngày.");
        }

        boolean hasPendingRequest = requestRepo.existsByEmployeeID_IdAndDateAndStatus(
                currentUserId,
                dto.getTargetDate(),
                RequestStatus.PENDING
        );

        if (hasPendingRequest) {
            throw new IllegalArgumentException("Bạn đang có yêu cầu chờ duyệt cho ngày này rồi. Vui lòng không gửi lại.");
        }

        EmployeeofficialscheduleEntity currentSchedule = employeeOfficialScheduleRepository
                .findByEmployeeIDAndDate(employee, dto.getTargetDate())
                .orElse(null);

        Integer currentShiftId = (currentSchedule != null && currentSchedule.getShiftID() != null)
                ? currentSchedule.getShiftID().getId()
                : null;

        if (dto.getNewShiftId() != null && dto.getNewShiftId().equals(currentShiftId)) {
            throw new IllegalArgumentException("Ca mới trùng với ca hiện tại, không cần đổi.");
        }

        ShiftChangeRequestEntity request = new ShiftChangeRequestEntity();
        request.setEmployeeID(employee);
        request.setDate(dto.getTargetDate());
        request.setReason(dto.getReason());
        request.setStatus(RequestStatus.PENDING); // Mặc định là chờ duyệt
        request.setCreatedAt(Instant.now());


        //Ca trước khi đổi
        if (currentSchedule != null) {
            request.setCurrentShiftID(currentSchedule.getShiftID().getId());
        } else {
            request.setCurrentShiftID(null);
        }

        //Ca mới
        if (dto.getNewShiftId() != null) {
            ShiftEntity newShift = shiftRepository.findById(dto.getNewShiftId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc với ID: " + dto.getNewShiftId()));
            request.setNewShiftID(newShift);
        } else {
            request.setNewShiftID(null);
        }

        requestRepo.save(request);
    }

    // Từ chối
    @Transactional
    public void rejectRequest(Integer requestId, Integer managerId) {
        ShiftChangeRequestEntity request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));

        UserEntity manager = getUserOrThrow(managerId);

        if (!manager.getDepartmentID().getId().equals(request.getEmployeeID().getDepartmentID().getId())) {
            throw new SecurityException("Không được từ chối đơn khác phòng ban");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setApproverID(manager);
        requestRepo.save(request);

    }


    private UserEntity getUserOrThrow(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
    private void logApprovalAction(UserEntity manager, UserEntity employee, ShiftChangeRequestEntity request) {
        ActivitylogEntity log = new ActivitylogEntity();
        log.setUserID(manager); // Người thực hiện là Manager
        log.setActiontime(LocalDateTime.now());

        // Logic nghiệp vụ xác định mức độ quan trọng của Log
        if (manager.getId().equals(employee.getId())) {
            log.setAction("SELF_APPROVAL_SCHEDULE");
            log.setLogType(LogType.valueOf("WARNING"));
            log.setDetails("CẢNH BÁO: Tự duyệt yêu cầu đổi ca. Lý do: " + request.getReason());
        } else {
            log.setAction("APPROVE_SHIFT_CHANGE");
            log.setLogType(LogType.valueOf("INFO"));
            log.setDetails("Duyệt đổi ca cho nhân viên: " + employee.getFullname()
                    + " (Mã đơn: " + request.getId() + ")");
        }

        logRepo.save(log);
    }
    private void updateOfficialSchedule(UserEntity employee, LocalDate date, ShiftEntity newShift) {
        // Tìm lịch ngày hôm đó, nếu chưa có thì tạo mới
        EmployeeofficialscheduleEntity schedule = scheduleRepo.findByEmployeeIDAndDate(employee, date)
                .orElse(new EmployeeofficialscheduleEntity());

        schedule.setEmployeeID(employee);
        schedule.setDate(date);
        schedule.setShiftID(newShift); // Nếu newShift là null thì set null
        schedule.setIsDayOff(newShift == null); // Nếu không có ca thì là ngày nghỉ

        // Tự động set tháng năm
        schedule.setMonthYear(date.format(MONTH_YEAR_FORMATTER));
        schedule.setPublishedDate(Instant.now());

        scheduleRepo.save(schedule);
    }

    // Lấy ds YC đổi ca
    public List<ShiftChangeListDTO> getRequests(ShiftChangeFilterCriteria criteria, Integer currentUserId) {
        UserEntity currentUser = getUserOrThrow(currentUserId);

        boolean isManager = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Manager"));

        if (isManager) {
            if (currentUser.getDepartmentID() != null) {
                criteria.setDepartmentId(currentUser.getDepartmentID().getId());
            }
        } else { // là nhân viên
            criteria.setEmployeeId(currentUser.getId());
            criteria.setDepartmentId(null);
        }

        Specification<ShiftChangeRequestEntity> spec = ShiftChangeFilter.withCriteria(criteria);

        List<ShiftChangeRequestEntity> requests = requestRepo.findAll(spec);

        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private ShiftChangeListDTO convertToDTO(ShiftChangeRequestEntity entity) {
        return ShiftChangeListDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeID().getId())
                .employeeName(entity.getEmployeeID().getFullname())
                .departmentName(entity.getEmployeeID().getDepartmentID() != null
                        ? entity.getEmployeeID().getDepartmentID().getDepartmentname() : "N/A")
                .targetDate(entity.getDate())
                .currentShiftName(entity.getCurrentShiftID() != null
                        ? getShiftNameById(entity.getCurrentShiftID()) : "Ngày nghỉ")
                .newShiftName(entity.getNewShiftID() != null
                        ? entity.getNewShiftID().getShiftname() : "Xin nghỉ (Off)")
                .reason(entity.getReason())
                .status(entity.getStatus())
                .approverName(entity.getApproverID() != null ? entity.getApproverID().getFullname() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private String getShiftNameById(Integer shiftId) {
        return shiftRepository.findById(shiftId)
                .map(ShiftEntity::getShiftname)
                .orElse("Unknown Shift");
    }
}