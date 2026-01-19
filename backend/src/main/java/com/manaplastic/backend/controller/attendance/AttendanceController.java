package com.manaplastic.backend.controller.attendance;

import com.manaplastic.backend.DTO.attendance.AttendanceDTO;
import com.manaplastic.backend.DTO.criteria.AttendanceFilterCriteria;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.AttendanceEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.AttendanceService;
import com.manaplastic.backend.service.CheckPermissionService;
import com.manaplastic.backend.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@RestController
@RequestMapping("/chamCong")
public class AttendanceController {
    @Autowired
    private UserService userService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private CheckPermissionService checkPermissionService;

    @Value("${app.upload.attendance}")
    private String uploadDir;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @RequiredPermission(PermissionConst.ATTENDANCE_VIEW_SELF)
    public ResponseEntity<Page<AttendanceDTO>> getMyAttendance(
            @ModelAttribute AttendanceFilterCriteria criteria,
            @AuthenticationPrincipal UserEntity currentUser,
            @PageableDefault(page = 0, size= 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {



        boolean canViewDept = checkPermissionService.checkPermission(currentUser.getId(), PermissionConst.ATTENDANCE_VIEW_DEPT);
        boolean canViewAll = checkPermissionService.checkPermission(currentUser.getId(), PermissionConst.ATTENDANCE_VIEW_ALL);

        if (canViewAll) {
            // Là HR
        }
        else if (canViewDept) { // xem phòng ban
            if (currentUser.getDepartmentID() == null) {
                throw new RuntimeException("Tài khoản Manager chưa được gán phòng ban, không thể xem dữ liệu.");
            }
            criteria.setDepartmentId(currentUser.getDepartmentID().getId());

        }
        else { //  xem của mình
            criteria.setUserId(currentUser.getId());
        }

        Page<AttendanceDTO> result = attendanceService.getFilteredAttendance(criteria, pageable);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getAttendanceImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Xác định loại ảnh (jpg, png...)
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "image/jpeg"; // Mặc định nếu không dò được
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
    @DeleteMapping("/{attendanceId}")
    @PreAuthorize("hasAuthority('HR')")
    @LogActivity(action="DELETE_ATTENDANCE",description = "Xóa dữ liệu chấm công",logType = LogType.DANGER)
    @RequiredPermission(PermissionConst.ATTENDANCE_UPDATE)
    public ResponseEntity<String> deleteAttendance(@PathVariable int attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.ok("Xóa thành công!");
    }
}
