package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.attendance.AttendanceRequestCreateDTO;
import com.manaplastic.backend.DTO.attendance.AttendanceRequestResponseDTO;
import com.manaplastic.backend.DTO.criteria.AttendanceRequestFilterCriteria;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.filters.AttendanceRequestFilter;
import com.manaplastic.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.manaplastic.backend.entity.AttendanceRequestEntity.RequestType.*;
import static com.manaplastic.backend.entity.AttendanceRequestEntity.RequestStatus.*;
import static com.manaplastic.backend.entity.AttendanceEntity.AttendanceStatus.*;


@Service
public class AttendanceRequestService {

    @Autowired
    private AttendanceRepository attendanceRepo;
    @Autowired
    private AttendanceRequestRepository requestRepo;
    @Autowired
    private UserRepository userRepo;
    @Value("${app.upload.proofs}")
    private String uploadDir;
    @Autowired
    private ActivityLogRepository activityLogRepository;
    @Autowired
    private DepartmentRepository departmentRepo;

    //Tạo
    public AttendanceRequestEntity createRequest(AttendanceRequestCreateDTO dto, MultipartFile file, Integer userId) {
        AttendanceRequestEntity entity = new AttendanceRequestEntity();
        UserEntity user = new UserEntity();
        user.setId(userId);

        entity.setUserid(user);
        entity.setDate(dto.getDate());

        if (dto.getShiftId() != null) {
            ShiftEntity shift = new ShiftEntity();
            shift.setId(dto.getShiftId());
            entity.setShiftid(shift);
        }

        if (dto.getCheckInTime() != null && dto.getCheckOutTime() != null) {
            if (dto.getCheckInTime().isAfter(dto.getCheckOutTime())) {
                throw new IllegalArgumentException("Thời gian Check-in phải trước Check-out.");
            }
        }

        if (dto.getRequestType() == FULL_SHIFT) {
            if (dto.getCheckInTime() == null || dto.getCheckOutTime() == null) {
                throw new IllegalArgumentException("Yêu cầu cả ca phải nhập đủ giờ vào và ra.");
            }
        } else if (dto.getRequestType() == CHECK_IN && dto.getCheckInTime() == null) {
            throw new IllegalArgumentException("Yêu cầu Check-in thiếu giờ vào.");
        } else if (dto.getRequestType() == CHECK_OUT && dto.getCheckOutTime() == null) {
            throw new IllegalArgumentException("Yêu cầu Check-out thiếu giờ ra.");
        }

        // check trùng đơn cho cả 2 cấp
        boolean exists = requestRepo.existsByUseridAndDateAndStatus(user, dto.getDate(), PENDING_MANAGER)
                || requestRepo.existsByUseridAndDateAndStatus(user, dto.getDate(), PENDING_HR);

        if (exists) {
            throw new IllegalArgumentException("Bạn đang có một yêu cầu chờ duyệt cho ngày này rồi.");
        }

        entity.setRequesttype(dto.getRequestType());
        entity.setCheckintime(dto.getCheckInTime());
        entity.setCheckouttime(dto.getCheckOutTime());
//        entity.setImgproof(dto.getImgProof());

        if (file != null && !file.isEmpty()) {
            String imgPath = saveProofImage(file);
            entity.setImgproof(imgPath); // Lưu đường dẫn vào DB
        }
        entity.setReason(dto.getReason());
        entity.setStatus(PENDING_MANAGER);

        return requestRepo.save(entity);
    }

//    //Duyệt
//    @Transactional
//    public void approveRequest(int requestId, int approverId) {
//        AttendanceRequestEntity request = requestRepo.findById(requestId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
//
//        UserEntity approver = userRepo.findById(approverId)
//                .orElseThrow(() -> new RuntimeException("Người duyệt không tồn tại."));
//
//        if (request.getStatus() != PENDING) {
//            throw new RuntimeException("Yêu cầu này đã được xử lý trước đó.");
//        }
//
    /// /        UserEntity approver = new UserEntity();
    /// /        approver.setId(approverId);
    /// /        request.setStatus(AttendanceRequestEntity.RequestStatus.APPROVED);
//        request.setStatus(APPROVED);
//        request.setApproverid(approver);
//        requestRepo.save(request);
//
//
//        updateAttendanceData(request);
//        logAttendanceApprovalAction(approver,request);
//    }

//      Duyệt - Manager
    @Transactional
    public void approveByManager(int requestId, UserEntity managerId) {
        AttendanceRequestEntity request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

        if (request.getStatus() != PENDING_MANAGER) {
            throw new RuntimeException("Yêu cầu này không ở trạng thái chờ quản lý duyệt.");
        }

        UserEntity requester = request.getUserid();
        DepartmentEntity department = requester.getDepartmentID();

        if (department == null) {
            throw new RuntimeException("Nhân viên này chưa thuộc phòng ban nào.");
        }


        UserEntity deptManagerId = department.getManagerID();

        if (deptManagerId == null || !deptManagerId.getId().equals(managerId.getId())) {
            throw new RuntimeException("Bạn không phải quản lý trực tiếp của phòng ban này.");
        }

        request.setStatus(PENDING_HR);

        // Lưu thông tin Manager duyệt
        request.setManagerApproverID(managerId);
        request.setManagerApprovedAt(LocalDateTime.now());

        requestRepo.save(request);
        logAttendanceAction(managerId, request, "MANAGER_APPROVE_ATTENDANCE", "đã duyệt sơ bộ (Manager Check)");
    }

    // HR Duyệt (Chuyển sang APPROVED và Update công)
    @Transactional
    public void approveByHR(int requestId, UserEntity hrId) {
        AttendanceRequestEntity request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

        if (request.getStatus() != PENDING_HR) {
            throw new RuntimeException("Yêu cầu này chưa được quản lý duyệt hoặc đã xử lý xong.");
        }

        request.setStatus(APPROVED);
        request.setApproverid(hrId);
        request.setHrApprovedAt(LocalDateTime.now());

        requestRepo.save(request);

        updateAttendanceData(request);
        logAttendanceAction(hrId, request, "HR_APPROVE_ATTENDANCE", "đã duyệt và cập nhật công (HR Final)");
    }

    // Từ chối
    @Transactional
    public void rejectRequest(int requestId, UserEntity rejecterId, String comment) {
        AttendanceRequestEntity request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

        if (comment == null || comment.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập lý do từ chối.");
        }

        // Chỉ được từ chối khi đơn đang chờ
        if (request.getStatus() == APPROVED || request.getStatus() == REJECTED) {
            throw new RuntimeException("Yêu cầu đã được xử lý xong, không thể từ chối lại.");
        }

        UserEntity rejecter = userRepo.findById(rejecterId).orElse(new UserEntity());
        rejecter.setId(rejecterId.getId());

        request.setStatus(REJECTED);

        request.setApproverid(rejecter);
        request.setComment(comment);

        requestRepo.save(request);
        logAttendanceAction(rejecter, request, "REJECT_ATTENDANCE_REQ", "đã từ chối yêu cầu. Lý do: " + comment);
    }

    // Hàm hỗ trợ lưu file
    private String saveProofImage(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir.trim());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new IllegalArgumentException("Chỉ chấp nhận file ảnh định dạng JPG, JPEG hoặc PNG.");
            }

            if (file.isEmpty()) {
                throw new RuntimeException("File không được để trống.");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) originalFilename = "unknown.jpg";

            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex);
            }

            String baseName = (dotIndex >= 0) ? originalFilename.substring(0, dotIndex) : originalFilename;

            // Regex: [^a-zA-Z0-9] nghĩa là thay thế tất cả ký tự KHÔNG PHẢI chữ và số thành dấu gạch dưới
            String cleanBaseName = baseName.replaceAll("[^a-zA-Z0-9]", "_");

            String finalFileName = UUID.randomUUID().toString() + "_" + cleanBaseName + extension;

            Path filePath = uploadPath.resolve(finalFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return finalFileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu ảnh minh chứng: " + e.getMessage());
        }
    }

    // Lọc
    public Page<AttendanceRequestResponseDTO> getFilteredRequests(AttendanceRequestFilterCriteria filter, Pageable pageable) {

        Specification<AttendanceRequestEntity> spec = AttendanceRequestFilter.filterRequests(filter);
        Page<AttendanceRequestEntity> pageResult = requestRepo.findAll(spec, pageable);
        return pageResult.map(this::convertToDTO);
    }

    private AttendanceRequestResponseDTO convertToDTO(AttendanceRequestEntity entity) {
        AttendanceRequestResponseDTO dto = new AttendanceRequestResponseDTO();
        dto.setRequestId(entity.getId());
//        dto.setUserId(entity.getUserid().getId());
//        dto.setEmployeeName(entity.getUserid().getFullname());

        if (entity.getUserid() != null) {
            dto.setUserId(entity.getUserid().getId());
            dto.setEmployeeName(entity.getUserid().getFullname()); // Hàm lấy tên user

            // Kiểm tra phòng ban có null không để tránh lỗi tương tự
            if (entity.getUserid().getDepartmentID() != null) {
                dto.setDepartmentName(entity.getUserid().getDepartmentID().getDepartmentname());
            }
        }
        dto.setDate(entity.getDate());
        dto.setShiftName(entity.getShiftid() != null ? entity.getShiftid().getShiftname() : "N/A");
        dto.setRequestType(entity.getRequesttype());
        dto.setCheckInTime(entity.getCheckintime());
        dto.setCheckOutTime(entity.getCheckouttime());
        dto.setImgProof(entity.getImgproof());
        dto.setStatus(entity.getStatus());
        dto.setReason(entity.getReason());
        if (entity.getApproverid() != null) {
            dto.setApproverName(entity.getApproverid().getFullname());
        } else {
            dto.setApproverName(null);
        }
        dto.setComment(entity.getComment());
        dto.setCreatedAt(entity.getCreatedat());
        return dto;
    }

    // Ghi log duyệt yêu cầu chấm công
    private void logAttendanceAction(UserEntity actor, AttendanceRequestEntity request, String actionCode, String actionDescription) {
        UserEntity requester = request.getUserid();

        ActivitylogEntity log = new ActivitylogEntity();
        log.setUserID(actor);
        log.setActiontime(LocalDateTime.now());

        // Logic kiểm tra tự duyệt/tự xử lý
        boolean isSelfAction = actor.getId().equals(requester.getId());

        boolean isRejection = actionCode.contains("REJECT");

        if (isSelfAction && !isRejection) {
            // Tự duyệt
            log.setAction("SELF_" + actionCode);
            log.setLogType(LogType.WARNING);
            log.setDetails("CẢNH BÁO: " + actor.getFullname() + " đã TỰ DUYỆT yêu cầu chấm công cho chính mình.");
        } else {
            // Bình thường
            log.setAction(actionCode);
            log.setLogType(LogType.INFO);

            String actorRole = (actor.getRoleID() != null) ? actor.getRoleID().getRolename() : "User";

            log.setDetails(actorRole + " " + actor.getFullname() + " " + actionDescription + " cho: " + requester.getFullname());
        }

        // Bổ sung thông tin chi tiết của đơn
        StringBuilder extraInfo = new StringBuilder();
        extraInfo.append(" | Ngày: ").append(request.getDate());
        extraInfo.append(" | Loại: ").append(request.getRequesttype());

        if (request.getCheckintime() != null) {
            extraInfo.append(" | Vào: ").append(request.getCheckintime());
        }

        if (request.getCheckouttime() != null) {
            extraInfo.append(" | Ra: ").append(request.getCheckouttime());
        }

        extraInfo.append(" (Mã đơn: ").append(request.getId()).append(")");
        log.setDetails(log.getDetails() + extraInfo.toString());

        activityLogRepository.save(log);
    }

    private void updateAttendanceData(AttendanceRequestEntity req) {
        // Tìm xem ngày đó nhân viên đã có record chấm công chưa
        AttendanceEntity attendance = attendanceRepo.findByUserIDAndDate(req.getUserid(), req.getDate())
                .orElse(new AttendanceEntity()); // Nếu chưa có thì tạo mới

        // Nếu là tạo mới, cần set các thông tin cơ bản
        if (attendance.getId() == null) {
            attendance.setUserID(req.getUserid());
            attendance.setDate(req.getDate());
            attendance.setShiftID(req.getShiftid());
        }

        // Cập nhật giờ dựa trên loại yêu cầu
        if (req.getRequesttype() == CHECK_IN || req.getRequesttype() == FULL_SHIFT) {
            if (req.getCheckintime() != null) {
                attendance.setCheckin(req.getCheckintime());
            }
        }

        if (req.getRequesttype() == CHECK_OUT || req.getRequesttype() == FULL_SHIFT) {
            if (req.getCheckouttime() != null) {
                attendance.setCheckout(req.getCheckouttime());
            }
        }

        // Cập nhật trạng thái tổng quát của ngày công (PRESENT) - bảng Attedance
        if (attendance.getCheckin() != null && attendance.getCheckout() != null) {
            attendance.setStatus(PRESENT);
        } else if (attendance.getCheckin() != null) {
            attendance.setStatus(MISSING_OUTPUT_DATA);
        } else if (attendance.getCheckout() != null) {
            attendance.setStatus(MISSING_INPUT_DATA);
        }

        attendanceRepo.save(attendance);
    }
}