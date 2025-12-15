package com.manaplastic.backend.controller.attendance;

import com.manaplastic.backend.DTO.attendance.AttendanceDTO;
import com.manaplastic.backend.DTO.criteria.AttendanceFilterCriteria;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.AttendanceService;
import com.manaplastic.backend.service.CheckPermissionService;
import com.manaplastic.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class AttendanceController {
    @Autowired
    private UserService userService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private CheckPermissionService checkPermissionService;

    @GetMapping("/chamCong")
    @PreAuthorize("hasAnyAuthority('HR','Manager','Employee')")
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


    @DeleteMapping("/chamCong/{attendanceId}")
    @PreAuthorize("hasAuthority('HR')")
    @RequiredPermission(PermissionConst.ATTENDANCE_UPDATE)
    public ResponseEntity<String> deleteAttendance(@PathVariable int attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.ok("Xóa thành công!");
    }
}
