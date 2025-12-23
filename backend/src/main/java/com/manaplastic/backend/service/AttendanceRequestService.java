package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.attendance.AttendanceRequestCreateDTO;
import com.manaplastic.backend.DTO.attendance.AttendanceRequestResponseDTO;
import com.manaplastic.backend.DTO.criteria.AttendanceRequestFilterCriteria;
import com.manaplastic.backend.entity.AttendanceEntity;
import com.manaplastic.backend.entity.AttendanceRequestEntity;
import com.manaplastic.backend.entity.ShiftEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.filters.AttendanceRequestFilter;
import com.manaplastic.backend.repository.AttendanceRepository;
import com.manaplastic.backend.repository.AttendanceRequestRepository;
import com.manaplastic.backend.repository.UserRepository;
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
import java.time.Instant;
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

        entity.setRequesttype(dto.getRequestType());
        entity.setCheckintime(dto.getCheckInTime());
        entity.setCheckouttime(dto.getCheckOutTime());
//        entity.setImgproof(dto.getImgProof());
        if (file != null && !file.isEmpty()) {
            String imgPath = saveProofImage(file);
            entity.setImgproof(imgPath); // Lưu đường dẫn vào DB
        }
        entity.setReason(dto.getReason());
        entity.setStatus(PENDING);

        return requestRepo.save(entity);
    }

    //Duyệt
    @Transactional
    public void approveRequest(int requestId, int approverId) {
        AttendanceRequestEntity request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

        if (request.getStatus() != PENDING) {
            throw new RuntimeException("Yêu cầu này đã được xử lý trước đó.");
        }

        UserEntity approver = new UserEntity();
        approver.setId(approverId);
//        request.setStatus(AttendanceRequestEntity.RequestStatus.APPROVED);
        request.setStatus(APPROVED);
        request.setApproverid(approver);
        requestRepo.save(request);


        updateAttendanceData(request);
    }

    // Từ chối
    public void rejectRequest(int requestId, int approverId, String comment) {
        AttendanceRequestEntity request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

        if (comment == null || comment.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập lý do từ chối.");
        }

        if (request.getStatus() != PENDING) {
            throw new RuntimeException("Yêu cầu đã được xử lý.");
        }

        UserEntity approver = new UserEntity();
        approver.setId(approverId);
        request.setStatus(REJECTED);
        request.setApproverid(approver);
        request.setComment(comment);

        requestRepo.save(request);
    }

    // Hàm hỗ trợ lưu file
    private String saveProofImage(MultipartFile file) {
        try {
//            String uploadDir = "uploads/proofs/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/" + uploadDir.replace("\\", "/") + "/" + fileName;

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