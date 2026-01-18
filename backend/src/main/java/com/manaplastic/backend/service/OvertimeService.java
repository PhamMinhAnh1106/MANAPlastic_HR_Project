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

import java.time.*;
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
    private static final LocalTime NIGHT_END_TIME = LocalTime.of(6, 0);

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

        LocalDateTime endTime = actualOut;
        LocalDateTime startTime = endTime.minusMinutes(minutes);

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
        validateTimeSegments(req.getStartTime(), req.getEndTime(), detailDTOs);
        List<OvertimeRequestDetailEntity> currentDetails = req.getDetails();

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
            // Cập nhật
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
                // Them mới
                OvertimeRequestDetailEntity newDetail = new OvertimeRequestDetailEntity();
                newDetail.setRequestID(req);
                newDetail.setStartTime(dto.getStartTime());
                newDetail.setEndTime(dto.getEndTime());
                newDetail.setHours(dto.getHours());

                OvertimetypeEntity type = overtimeTypeRepo.findById(dto.getOvertimeTypeID())
                        .orElseThrow(() -> new RuntimeException("Chưa chọn loại OT cho dòng mới"));
                newDetail.setOvertimeTypeID(type);
                currentDetails.add(newDetail);
            }
        }

        double newTotalFinalHours = currentDetails.stream().mapToDouble(OvertimeRequestDetailEntity::getHours).sum();
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
            UserEntity user, java.time.LocalDate date, LocalDateTime startTime, LocalDateTime endTime,
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


//        LocalDateTime nightPivot = LocalDateTime.of(date, NIGHT_START_TIME);
        // Xác định 2 mốc xoay chiều (Pivots)
        LocalDateTime nightStartPivot = LocalDateTime.of(date, NIGHT_START_TIME); // 22:00 hôm nay
        LocalDateTime nightEndPivot = LocalDateTime.of(date.plusDays(1), NIGHT_END_TIME); // 06:00 sáng hôm sau
        double calculatedTotalHours = 0;

        // KHÚC 1: Xử lý phần CA NGÀY (Trước 22:00)
        // Nếu bắt đầu trước 22h, thì sẽ có 1 đoạn ban ngày
        if (startTime.isBefore(nightStartPivot)) {
            // Điểm kết thúc của khúc này là: Hoặc là EndTime (nếu về sớm), Hoặc là bị chặn ở 22h
            LocalDateTime segmentEnd = endTime.isBefore(nightStartPivot) ? endTime : nightStartPivot;

            double h1 = calculateMinutes(startTime, segmentEnd) / 60.0;
            if (h1 > 0) {
                // Dùng baseType (Loại ngày)
                addDetailToMaster(master, baseType, startTime, segmentEnd, h1);
                calculatedTotalHours += h1;
            }
        }

        // KHÚC 2: CA ĐÊM (22:00 -> 06:00)
        // Logic: Có bất kỳ sự giao thoa nào với khung 22h-06h không?
        // Start < 06h sáng sau VÀ End > 22h tối nay
        if (startTime.isBefore(nightEndPivot) && endTime.isAfter(nightStartPivot)) {
            LocalDateTime segmentStart = startTime.isAfter(nightStartPivot) ? startTime : nightStartPivot;
            LocalDateTime segmentEnd = endTime.isBefore(nightEndPivot) ? endTime : nightEndPivot;

            double h2 = calculateMinutes(segmentStart, segmentEnd) / 60.0;
            if (h2 > 0) {
                OvertimetypeEntity nightType = findNightShiftType(baseType);
                addDetailToMaster(master, nightType, segmentStart, segmentEnd, h2);
                calculatedTotalHours += h2;
            }
        }

        // KHÚC 3: CA NGÀY (SAU 06:00 SÁNG HÔM SAU)
        // Logic: Nếu End muộn hơn 06h sáng hôm sau
        if (endTime.isAfter(nightEndPivot)) {
            LocalDateTime segmentStart = startTime.isAfter(nightEndPivot) ? startTime : nightEndPivot;

            double h3 = calculateMinutes(segmentStart, endTime) / 60.0;
            if (h3 > 0) {
                addDetailToMaster(master, baseType, segmentStart, endTime, h3);
                calculatedTotalHours += h3;
            }
        }

        master.setTotalHours(calculatedTotalHours);
        master.setFinalPaidHours(calculatedTotalHours);

        otRepo.save(master);
    }

    // Helper function để code gọn hơn, đỡ lặp lại việc new Entity
    private void addDetailToMaster(OvertimeRequestEntity master, OvertimetypeEntity type, LocalDateTime start, LocalDateTime end, double hours) {
        OvertimeRequestDetailEntity detail = new OvertimeRequestDetailEntity();
        detail.setOvertimeTypeID(type);
        detail.setStartTime(start);
        detail.setEndTime(end);
        detail.setHours(hours);
        master.addDetail(detail);
    }

    private long calculateMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return Duration.between(start, end).toMinutes();
    }
    // Helper mapping loại đêm
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

    // Xem danh sách
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
    // THÊM: Hàm validate logic thời gian chặt chẽ
    private void validateTimeSegments(LocalDateTime masterStart, LocalDateTime masterEnd, List<OvertimeDetailUpdateDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return; // Hoặc throw exception tùy logic business

        // 1. Check Master
        if (!masterEnd.isAfter(masterStart)) {
            throw new RuntimeException("Thời gian tổng không hợp lệ (Kết thúc phải sau Bắt đầu).");
        }

        long totalMasterMinutes = calculateMinutes(masterStart, masterEnd);
        long totalDetailMinutes = 0;

        // Tạo list tạm để sort
        List<OvertimeDetailUpdateDTO> sortedList = new ArrayList<>(dtos);
        sortedList.sort(Comparator.comparing(OvertimeDetailUpdateDTO::getStartTime));

        for (int i = 0; i < sortedList.size(); i++) {
            OvertimeDetailUpdateDTO current = sortedList.get(i);

            // Check A: Thời gian con có nằm ngoài phạm vi cha không?
            if (current.getStartTime().isBefore(masterStart) || current.getEndTime().isAfter(masterEnd)) {
                throw new RuntimeException("Chi tiết số " + (i+1) + " nằm ngoài khoảng thời gian của đơn tổng.");
            }

            // Check B: Thời gian ngược
            if (!current.getEndTime().isAfter(current.getStartTime())) {
                throw new RuntimeException("Chi tiết số " + (i+1) + ": Giờ kết thúc phải sau giờ bắt đầu.");
            }

            totalDetailMinutes += calculateMinutes(current.getStartTime(), current.getEndTime());

            // Check C: Trùng lặp (Overlap) với dòng kế tiếp
            if (i < sortedList.size() - 1) {
                OvertimeDetailUpdateDTO next = sortedList.get(i + 1);
                if (current.getEndTime().isAfter(next.getStartTime())) {
                    throw new RuntimeException("Phát hiện trùng lặp thời gian giữa các dòng chi tiết.");
                }
            }
        }

        // 3. SO SÁNH TỔNG (Bắt buộc khớp 100%)
        if (totalDetailMinutes != totalMasterMinutes) {
            throw new RuntimeException("Tổng thời gian chi tiết (" + totalDetailMinutes + " phút) " +
                    "không khớp với thời gian tổng của đơn (" + totalMasterMinutes + " phút). " +
                    "Vui lòng kiểm tra lại từng phút.");
        }
    }
}