package com.manaplastic.backend.controller.schedule;

import com.manaplastic.backend.DTO.schedule.ShiftChangeDTO;

import com.manaplastic.backend.DTO.schedule.ShiftChangeFilterCriteria;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.ShiftChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/shiftChanges")
@RequiredArgsConstructor
public class ShiftChangeController {

    private final ShiftChangeService service;

    // Lấy ds đơn xin đổi ca
    @GetMapping("/filter")
    public ResponseEntity<?> getShiftChangeRequests(
            @ModelAttribute ShiftChangeFilterCriteria criteria,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        var result = service.getRequests(criteria, currentUser.getId());
        return ResponseEntity.ok(result);
    }

    //Yêu cầu đổi
    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody ShiftChangeDTO dto,
                                           @AuthenticationPrincipal UserEntity currentUser) {
        service.createRequest(dto,currentUser.getId());
        return ResponseEntity.ok("Đã gửi yêu cầu đổi ca thành công.");
    }

  //Duyệt
    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('Manager')")
    @RequiredPermission(PermissionConst.LEAVE_APPROVE)
    public ResponseEntity<?> approveRequest(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserEntity manager
    ) {
        service.approveRequest(id, manager.getId());
        return ResponseEntity.ok("Đã duyệt yêu cầu. Lịch làm việc đã được cập nhật.");
    }

    // Từ chối
    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAuthority('Manager')")
    @RequiredPermission(PermissionConst.LEAVE_APPROVE)
    public ResponseEntity<?> rejectRequest(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserEntity manager
    ) {
        service.rejectRequest(id, manager.getId());
        return ResponseEntity.ok("Đã từ chối yêu cầu.");
    }
}