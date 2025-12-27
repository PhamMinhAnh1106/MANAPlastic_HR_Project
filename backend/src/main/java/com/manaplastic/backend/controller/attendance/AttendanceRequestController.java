package com.manaplastic.backend.controller.attendance;

import com.manaplastic.backend.DTO.attendance.AttendanceRequestCreateDTO;
import com.manaplastic.backend.DTO.attendance.AttendanceRequestResponseDTO;
import com.manaplastic.backend.DTO.criteria.AttendanceRequestFilterCriteria;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import com.manaplastic.backend.service.AttendanceRequestService;
import com.manaplastic.backend.service.CheckPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/user/attendanceRequests")
public class AttendanceRequestController {
    @Autowired
    private AttendanceRequestService requestService;
    @Autowired
    private CheckPermissionService checkPermissionService;
    @Value("${app.upload.proofs}")
    private String uploadDir;

  //Tạo đơn
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @RequiredPermission(PermissionConst.ATTENDANCE_REQ_CREATE)
    @LogActivity(action = "CREATE_ATTENDANCE_REQ", description = "Gửi yêu cầu bổ sung công")
    public ResponseEntity<String> createRequest(
            @ModelAttribute AttendanceRequestCreateDTO dto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserEntity currentUser) {

        // Gọi service kèm theo file
        requestService.createRequest(dto, file, currentUser.getId());

        return ResponseEntity.ok("Gửi yêu cầu thành công! Vui lòng chờ quản lý duyệt.");
    }

    // Xem & Lọc
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AttendanceRequestResponseDTO>> getRequests(
            @ModelAttribute AttendanceRequestFilterCriteria filter,
            @AuthenticationPrincipal UserEntity currentUser,
            @PageableDefault(page = 0, size = 10, sort = "createdat", direction = Sort.Direction.DESC) Pageable pageable) {

        boolean canViewAll = checkPermissionService.checkPermission(currentUser.getId(), PermissionConst.ATTENDANCE_VIEW_ALL);
        boolean canViewDept = checkPermissionService.checkPermission(currentUser.getId(), PermissionConst.ATTENDANCE_VIEW_DEPT);

        if (canViewAll) {//HR
        }
        else if (canViewDept) {
            // Manager
            if (currentUser.getDepartmentID() == null) {
                throw new RuntimeException("Tài khoản Manager chưa được gán phòng ban, không thể xem dữ liệu.");
            }
            filter.setDepartmentId(currentUser.getDepartmentID().getId());
        }
        else {
            // Employee
            filter.setUserId(currentUser.getId());
        }

        Page<AttendanceRequestResponseDTO> result = requestService.getFilteredRequests(filter, pageable);
        return ResponseEntity.ok(result);
    }

    // xem file ảnh
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getProofImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {

                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "image/jpeg";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

   // Duyệt đơn
    @PutMapping("/{requestId}/approve")
    @PreAuthorize("hasAnyAuthority('HR')")
    @RequiredPermission(PermissionConst.ATTENDANCE_APPROVE)
//    @LogActivity(action = "APPROVE_ATTENDANCE_REQ", description = "Duyệt yêu cầu công", logType = LogType.WARNING)
    public ResponseEntity<String> approveRequest(
            @PathVariable int requestId,
            @AuthenticationPrincipal UserEntity currentUser) {

        requestService.approveRequest(requestId, currentUser.getId());
        return ResponseEntity.ok("Đã duyệt yêu cầu và cập nhật dữ liệu chấm công thành công.");
    }

  //Từ chối đơn
    @PutMapping("/{requestId}/reject")
    @PreAuthorize("hasAnyAuthority('HR')")
    @RequiredPermission(PermissionConst.ATTENDANCE_APPROVE)
    @LogActivity(action = "REJECT_ATTENDANCE_REQ", description = "Từ chối yêu cầu công", logType = LogType.WARNING)
    public ResponseEntity<String> rejectRequest(
            @PathVariable int requestId,
            @RequestParam(required = true) String comment, // Bắt buộc phải có lý do từ chối
            @AuthenticationPrincipal UserEntity currentUser) {

        requestService.rejectRequest(requestId, currentUser.getId(), comment);
        return ResponseEntity.ok("Đã từ chối yêu cầu.");
    }
}
