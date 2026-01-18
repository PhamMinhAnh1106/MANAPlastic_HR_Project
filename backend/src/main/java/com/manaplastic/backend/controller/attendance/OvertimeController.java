package com.manaplastic.backend.controller.attendance;

import com.manaplastic.backend.DTO.attendance.OvertimeApproveDTO;
import com.manaplastic.backend.DTO.attendance.OvertimeCreateDTO;
import com.manaplastic.backend.DTO.attendance.OvertimeRejectDTO;
import com.manaplastic.backend.DTO.attendance.OvertimeResponseDTO;
import com.manaplastic.backend.DTO.criteria.OvertimeFilterCriteria;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.LogicalOperator;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.AttendanceRepository;
import com.manaplastic.backend.repository.UserRepository;
import com.manaplastic.backend.service.AttendanceScannerService;
import com.manaplastic.backend.service.OvertimeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/user/overTimeRequest")
@RequiredArgsConstructor
public class OvertimeController {

    @Autowired
    private final OvertimeService otService;
    @Autowired
    private final UserRepository userRepo;
    @Autowired
    private final AttendanceScannerService scannerService;

    // Tạo
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @RequiredPermission(PermissionConst.OVERTIME_CREATE)
    @LogActivity(action = "OT_CREATE", description = "Tạo đơn OT")
    public ResponseEntity<?> createOvertimeRequest(
            @RequestBody OvertimeCreateDTO dto,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        otService.createManualRequest(dto, currentUser);
        return ResponseEntity.ok("Tạo yêu cầu tăng ca thành công!");
    }

    // Xem
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @RequiredPermission(PermissionConst.OVERTIME_VIEW)
    public ResponseEntity<Page<OvertimeResponseDTO>> getOvertimeRequests(
            @AuthenticationPrincipal UserEntity currentUser,
            @ModelAttribute OvertimeFilterCriteria criteria,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OvertimeResponseDTO> result =
                otService.getFilteredRequests(criteria, currentUser, pageable);

        return ResponseEntity.ok(result);
    }

    // QL duyệt
    @PutMapping("/manager/approve/{id}")
    @PreAuthorize("hasAnyAuthority('Manager','HR')")
    @RequiredPermission(
            value = {PermissionConst.OVERTIME_APPROVE_MANAGER, PermissionConst.OVERTIME_APPROVE_HR},
            logic = LogicalOperator.OR)
    @LogActivity(action = "OT_APPROVE_MANAGER", description = "Quản lý duyệt đơn OT")
    public ResponseEntity<?> managerApprove(
            @PathVariable Integer id,
            @RequestBody OvertimeApproveDTO dto,
            @AuthenticationPrincipal UserEntity manager
    ) {
        otService.approveByManager(id, manager, dto);
        return ResponseEntity.ok("Đã duyệt đơn tăng ca (Cấp Quản lý).");
    }

    // HR duyệt
    @PutMapping("/hr/approve/{id}")
    @PreAuthorize("hasAuthority('HR')")
    @RequiredPermission(PermissionConst.OVERTIME_APPROVE_HR)
    @LogActivity(action = "OT_APPROVE_HR", description = "HR duyệt đơn OT")
    public ResponseEntity<?> hrApprove(
            @PathVariable Integer id,
            @RequestBody OvertimeApproveDTO dto,
            @AuthenticationPrincipal UserEntity hr
    ) {
        otService.approveByHR(id, hr, dto);
        return ResponseEntity.ok("Đã phê duyệt hoàn tất. Giờ làm đã được chốt.");
    }

    // Từ chối (Dùng chung cho Manager và HR)
    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAnyAuthority('Manager', 'HR')")
    @RequiredPermission(PermissionConst.OVERTIME_REJECT)
    @LogActivity(action = "OT_REJECT", description = "Từ chối đơn OT")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Integer id,
            @RequestBody OvertimeRejectDTO dto,
            @AuthenticationPrincipal UserEntity user
    ) {
        otService.rejectRequest(id, user, dto.getReason());
        return ResponseEntity.ok("Đã từ chối đơn tăng ca.");
    }

    // Trigger thủ công, bấm để quét
    @PostMapping("/scanDailyOt")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<?> manualScanOT(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        scannerService.scanAndGenerateOvertime(date);
        return ResponseEntity.ok("Đã chạy quét OT tự động cho ngày " + date);
    }
}