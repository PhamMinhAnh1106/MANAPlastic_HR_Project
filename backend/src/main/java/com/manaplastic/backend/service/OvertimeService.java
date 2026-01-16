package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.attendance.*;
import com.manaplastic.backend.DTO.criteria.OvertimeFilterCriteria;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.filters.OvertimeRequestFilter;
import com.manaplastic.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.manaplastic.backend.entity.OvertimeRequestEntity.RequestStatus.*;

@Service
public class OvertimeService {

    @Autowired private OvertimeRequestRepository otRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private DepartmentRepository deptRepo;
    @Autowired private OvertimeTypeRepository overtimeTypeRepo;
    @Autowired private EmployeeOfficialScheduleRepository scheduleRepo;

    // Mốc giờ bắt đầu tính Ca Đêm (22:00)
    private static final LocalTime NIGHT_START_TIME = LocalTime.of(22, 0);

    // Tạo tay
    @Transactional
    public void createManualRequest(OvertimeCreateDTO dto, UserEntity creator) {
        UserEntity targetUser = creator;
        boolean isCreatedByManager = false;

        // Check quyền Manager/Admin tạo hộ
        String creatorRole = creator.getRoleID() != null ? creator.getRoleID().getRolename() : "";
        boolean isManagerOrAdmin = "Manager".equalsIgnoreCase(creatorRole) || "Admin".equalsIgnoreCase(creatorRole);

        if (dto.getTargetUserId() != null && isManagerOrAdmin) {
            targetUser = userRepo.findById(dto.getTargetUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên mục tiêu."));
            if (!"Admin".equalsIgnoreCase(creatorRole)) {
                if (creator.getDepartmentID() == null || targetUser.getDepartmentID() == null ||
                        !creator.getDepartmentID().getId().equals(targetUser.getDepartmentID().getId())) {
                    throw new RuntimeException("Bạn chỉ được tạo đơn hộ nhân viên trong cùng phòng ban.");
                }
            }
            isCreatedByManager = true;
        }

        if (dto.getStartTime().equals(dto.getEndTime())) {
            throw new RuntimeException("Thời gian OT không hợp lệ (0 phút).");
        }

        OvertimetypeEntity originalType = overtimeTypeRepo.findById(dto.getOvertimetypeid())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại OT"));

        processAndSaveRequest(
                targetUser, dto.getDate(), dto.getStartTime(), dto.getEndTime(),
                dto.getReason(), originalType,
                isCreatedByManager ? PENDING_HR : PENDING_MANAGER,
                false, null,
                isCreatedByManager ? creator : null
        );
    }

    // Hệ thống tạo
    @Transactional
    public void autoGenerateSystemRequest(UserEntity user, java.time.LocalDate date, LocalDateTime actualOut, Double detectedHours) {
        if (otRepo.existsByUseridAndDate(user, date)) return;

        long minutes = (long) (detectedHours * 60);
        LocalTime endTime = actualOut.toLocalTime();
        LocalTime startTime = endTime.minusMinutes(minutes);

        final int defaultTypeId = (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) ? 2 : 1;
        OvertimetypeEntity defaultType = overtimeTypeRepo.findById(defaultTypeId)
                .orElseThrow(() -> new RuntimeException("Cấu hình lỗi: Không tìm thấy OvertimeType ID " + defaultTypeId));

        processAndSaveRequest(
                user, date, startTime, endTime,
                "Hệ thống phát hiện chênh lệch giờ về (OT phát sinh)",
                defaultType,
                PENDING_CONFIRMATION,
                true, actualOut, null
        );
    }

    //QL duyệt lần 1
    @Transactional
    public void approveByManager(Integer requestId, UserEntity manager, OvertimeApproveDTO dto) {
        OvertimeRequestEntity req = otRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn OT"));

        if (req.getStatus() != PENDING_MANAGER && req.getStatus() != PENDING_CONFIRMATION) {
            throw new RuntimeException("Trạng thái đơn không hợp lệ để Manager duyệt.");
        }

        updateOvertimeDetails(req, dto.getDetails());

        req.setStatus(PENDING_HR);
        req.setManagerApproverID(manager);
        req.setManagerApprovedAt(LocalDateTime.now());

        if (dto.getNote() != null) req.setRejectReason(dto.getNote());

        otRepo.save(req);
    }

   // HR duyệt lần 2
   @Transactional
   public void approveByHR(Integer requestId, UserEntity hr, OvertimeApproveDTO dto) {
       OvertimeRequestEntity req = otRepo.findById(requestId)
               .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn OT"));

       if (req.getStatus() != PENDING_HR) {
           throw new RuntimeException("Đơn chưa được Manager duyệt.");
       }


       updateOvertimeDetails(req, dto.getDetails());


       if (req.getFinalPaidHours() == null) {
           req.setFinalPaidHours(req.getTotalHours());
       }


       req.setStatus(APPROVED);
       req.setHrApproverID(hr);
       req.setHrApprovedAt(LocalDateTime.now());


       otRepo.save(req);
   }

    private void updateOvertimeDetails(OvertimeRequestEntity req, List<OvertimeDetailUpdateDTO> detailDTOs) {
        if (detailDTOs == null) return;

        List<OvertimeRequestDetailEntity> currentDetails = req.getDetails();

        // 1. XỬ LÝ XÓA (DELETE)
        // Tìm những ID có trong DB nhưng KHÔNG có trong danh sách DTO gửi lên
        // (Ví dụ: HR xóa bớt 1 dòng sai)
        Set<Integer> dtoIds = detailDTOs.stream()
                .map(OvertimeDetailUpdateDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // RemoveIf sẽ tự động kích hoạt lệnh DELETE trong Database nếu orphanRemoval=true hoặc khi save req
        currentDetails.removeIf(detail -> !dtoIds.contains(detail.getId()));

        // Map để truy xuất nhanh khi update
        Map<Integer, OvertimeRequestDetailEntity> currentMap = currentDetails.stream()
                .collect(Collectors.toMap(OvertimeRequestDetailEntity::getId, Function.identity()));

        for (OvertimeDetailUpdateDTO dto : detailDTOs) {
            //  cap nhat
            if (dto.getId() != null && currentMap.containsKey(dto.getId())) {

                OvertimeRequestDetailEntity existing = currentMap.get(dto.getId());
                existing.setStartTime(dto.getStartTime());
                existing.setEndTime(dto.getEndTime());
                existing.setHours(dto.getHours());

                if (dto.getOvertimeTypeID() != null) {
                    OvertimetypeEntity type = overtimeTypeRepo.findById(dto.getOvertimeTypeID())
                            .orElseThrow(() -> new RuntimeException("Loại OT không tồn tại"));
                    existing.setOvertimeTypeID(type);
                }
            } else {
                // them moi
                OvertimeRequestDetailEntity newDetail = new OvertimeRequestDetailEntity();
                newDetail.setRequestID(req); // Link ngược lại cha
                newDetail.setStartTime(dto.getStartTime());
                newDetail.setEndTime(dto.getEndTime());
                newDetail.setHours(dto.getHours());


                OvertimetypeEntity type = overtimeTypeRepo.findById(dto.getOvertimeTypeID())
                        .orElseThrow(() -> new RuntimeException("Chưa chọn loại OT cho dòng mới"));
                newDetail.setOvertimeTypeID(type);


                currentDetails.add(newDetail);
            }
        }

        double newTotalFinalHours = 0.0;
        for (OvertimeRequestDetailEntity detail : currentDetails) {
            newTotalFinalHours += detail.getHours();
        }

        req.setFinalPaidHours(newTotalFinalHours);

    }

  // từ chối - dùng chung
    @Transactional
    public void rejectRequest(Integer requestId, UserEntity user, String reason) {
        OvertimeRequestEntity req = otRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn OT"));

        if (req.getStatus() == APPROVED || req.getStatus() == REJECTED) {
            throw new RuntimeException("Đơn đã đóng, không thể từ chối.");
        }

        req.setStatus(REJECTED);
        req.setRejectReason(reason);
        req.setUpdatedAt(LocalDateTime.now());
        otRepo.save(req);
    }

  // Hàm tính toán số giờ OT ( OT 3 tiếng nhưng có thể có 2-3 loại OT )
    private void processAndSaveRequest(
            UserEntity user, java.time.LocalDate date, LocalTime startTime, LocalTime endTime,
            String reason, OvertimetypeEntity baseType,
            OvertimeRequestEntity.RequestStatus status, boolean isSystemGenerated,
            LocalDateTime actualCheckOut, UserEntity managerApprover
    ) {
        OvertimeRequestEntity master = new OvertimeRequestEntity();
        master.setUserid(user);
        master.setDepartmentid(user.getDepartmentID());
        master.setDate(date);
        master.setStartTime(startTime);
        master.setEndTime(endTime);
        master.setReason(reason);
        master.setStatus(status);
        master.setIsSystemGenerated(isSystemGenerated);
        master.setActualCheckOut(actualCheckOut);
        master.setCreatedAt(LocalDateTime.now());
        master.setUpdatedAt(LocalDateTime.now());


        Optional<EmployeeofficialscheduleEntity> scheduleOpt = scheduleRepo.findByEmployeeIDAndDate(user, date);

        if (scheduleOpt.isPresent()) {
            // Tìm thấy lịch -> Lấy Shift từ lịch
            master.setShiftID(scheduleOpt.get().getShiftID());
        } else {
            // Không tìm thấy lịch -> Có thể user không có lịch hôm đó (Ngày nghỉ?)
            master.setShiftID(null);
        }
        if (managerApprover != null) {
            master.setManagerApproverID(managerApprover);
            master.setManagerApprovedAt(LocalDateTime.now());
        }


        boolean isCrossingNight = false;
        if (endTime.isBefore(startTime)) isCrossingNight = true;
        else if (startTime.isBefore(NIGHT_START_TIME) && endTime.isAfter(NIGHT_START_TIME)) isCrossingNight = true;

        double calculatedTotalHours = 0;

        if (isCrossingNight) {
            // A. Giai đoạn 1: Ca Ngày
            if (startTime.isBefore(NIGHT_START_TIME)) {
                double h1 = Duration.between(startTime, NIGHT_START_TIME).toMinutes() / 60.0;
                OvertimeRequestDetailEntity detail1 = new OvertimeRequestDetailEntity();
                detail1.setOvertimeTypeID(baseType);
                detail1.setStartTime(startTime);
                detail1.setEndTime(NIGHT_START_TIME);
                detail1.setHours(h1);
                master.addDetail(detail1);
                calculatedTotalHours += h1;
            }
            // B. Giai đoạn 2: Ca Đêm
            OvertimetypeEntity nightType = findNightShiftType(baseType);
            long min2 = 0;
            if (endTime.isBefore(startTime)) {
                min2 = Duration.between(NIGHT_START_TIME, LocalTime.MAX).toMinutes() + Duration.between(LocalTime.MIN, endTime).toMinutes() + 1;
            } else {
                min2 = Duration.between(NIGHT_START_TIME, endTime).toMinutes();
            }
            double h2 = min2 / 60.0;
            OvertimeRequestDetailEntity detail2 = new OvertimeRequestDetailEntity();
            detail2.setOvertimeTypeID(nightType);
            detail2.setStartTime(NIGHT_START_TIME);
            detail2.setEndTime(endTime);
            detail2.setHours(h2);
            master.addDetail(detail2);
            calculatedTotalHours += h2;
        } else {
            // Không giao thoa
            double h = 0;
            OvertimetypeEntity finalType = baseType;
            if (endTime.isBefore(startTime)) {
                long mins = Duration.between(startTime, LocalTime.MAX).toMinutes() + Duration.between(LocalTime.MIN, endTime).toMinutes() + 1;
                h = mins / 60.0;
            } else {
                h = Duration.between(startTime, endTime).toMinutes() / 60.0;
            }
            if (!startTime.isBefore(NIGHT_START_TIME) || startTime.isBefore(LocalTime.of(6,0))) {
                finalType = findNightShiftType(baseType);
            }
            OvertimeRequestDetailEntity detail = new OvertimeRequestDetailEntity();
            detail.setOvertimeTypeID(finalType);
            detail.setStartTime(startTime);
            detail.setEndTime(endTime);
            detail.setHours(h);
            master.addDetail(detail);
            calculatedTotalHours += h;
        }

        // Set Total và Final mặc định ban đầu
        master.setTotalHours(calculatedTotalHours);
        master.setFinalPaidHours(calculatedTotalHours); // Mặc định bằng Total, duyệt thì sửa sau

        otRepo.save(master);
    }

    // Helper mapping loại đêm (Giữ nguyên)
    private OvertimetypeEntity findNightShiftType(OvertimetypeEntity originalType) {
        if (originalType == null) return null;
        Integer currentId = originalType.getId();
        Integer targetNightId = currentId;
        switch (currentId) {
            case 1: targetNightId = 5; break;
            case 2: targetNightId = 6; break;
            case 3: targetNightId = 7; break;
            default: return originalType;
        }
        if (!targetNightId.equals(currentId)) {
            return overtimeTypeRepo.findById(targetNightId).orElse(originalType);
        }
        return originalType;
    }

    // Xem danh sách (Giữ nguyên)
    public Page<OvertimeResponseDTO> getFilteredRequests(OvertimeFilterCriteria criteria, UserEntity currentUser, Pageable pageable) {
        Specification<OvertimeRequestEntity> spec = OvertimeRequestFilter.filterRequests(criteria, currentUser);
        Page<OvertimeRequestEntity> pageResult = otRepo.findAll(spec, pageable);
        return pageResult.map(this::convertToDTO);
    }

    // Convert DTO (Giữ nguyên)
    private OvertimeResponseDTO convertToDTO(OvertimeRequestEntity entity) {
        OvertimeResponseDTO dto = new OvertimeResponseDTO();
        dto.setRequestId(entity.getId());
        if (entity.getUserid() != null) {
            dto.setEmployeeName(entity.getUserid().getFullname());
            dto.setEmployeeId(entity.getUserid().getUsername());
        }
        if (entity.getDepartmentid() != null) {
            dto.setDepartmentName(entity.getDepartmentid().getDepartmentname());
        }
        dto.setDate(entity.getDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setTotalHours(entity.getTotalHours());
        dto.setFinalPaidHours(entity.getFinalPaidHours());
        dto.setStatus(entity.getStatus());
        dto.setReason(entity.getReason());
        dto.setIsSystemGenerated(entity.getIsSystemGenerated());
        if (entity.getManagerApproverID() != null) dto.setManagerName(entity.getManagerApproverID().getFullname());
        if (entity.getHrApproverID() != null) dto.setHrName(entity.getHrApproverID().getFullname());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getDetails() != null && !entity.getDetails().isEmpty()) {
            List<OvertimeDetailResponseDTO> detailDTOs = entity.getDetails().stream()
                    .map(detail -> new OvertimeDetailResponseDTO(
                            detail.getId(),
                            detail.getOvertimeTypeID() != null ? detail.getOvertimeTypeID().getOtName() : "Unknown Type",
                            detail.getHours(),
                            detail.getStartTime(),
                            detail.getEndTime()
                    ))
                    .collect(Collectors.toList());
            dto.setDetails(detailDTOs);
        } else {
            dto.setDetails(new ArrayList<>());
        }
        return dto;
    }
}