package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.AddLeaverequestDTO;
import com.manaplastic.backend.DTO.LeaveBalanceDTO;
import com.manaplastic.backend.DTO.LeaveRequestFilterCriteria;
import com.manaplastic.backend.DTO.LeaverequestDTO;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.LeaverequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LeaverequestController {
    @Autowired
    private LeaverequestService leaverequestService;

    //nhân viên và quản lý dùng chung đăng ký, xem, xóa đơn nghỉ phép ( HR thì chưa )
    @PostMapping("/user/leaverequest/addRequest")
    @PreAuthorize("hasAnyAuthority('Employee','Manager')")
    public ResponseEntity<String> createLeaveRequest(
            @Valid @RequestBody AddLeaverequestDTO requestDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

//        LeaverequestDTO createdRequest = leaverequestService.createLeaveRequest(requestDTO, currentUser);
//        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        try {
            leaverequestService.createLeaveRequest(requestDTO, currentUser);

            String responseMessage = "Đã tạo đơn nghỉ phép thành công.";
            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/user/leaverequest/myRequest")
    @PreAuthorize("hasAnyAuthority('Employee','Manager')")
    public ResponseEntity<List<LeaverequestDTO>> getMyLeaveRequests(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<LeaverequestDTO> myRequests = leaverequestService.getMyLeaveRequests(currentUser);
        return ResponseEntity.ok(myRequests);
    }

    //chỉ được xóa đơn khi đơn đang là PENDING
    @DeleteMapping("/user/leaverequest/myRequest/{id}")
    @PreAuthorize("hasAnyAuthority('Employee','Manager')")
    public ResponseEntity<String> deleteMyLeaveRequest(@PathVariable("id") Integer leaveRequestId,
                                                       @AuthenticationPrincipal UserEntity currentUser) {
        try {
            leaverequestService.deleteMyLeaveRequest(leaveRequestId, currentUser);
            String responseMessage = "Đã xóa đơn nghỉ phép thành công.";
            return ResponseEntity.ok(responseMessage);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    //Lọc đơn
    @GetMapping("/user/leaverequest/filter")
    @PreAuthorize("hasAnyAuthority('Manager','HR')")
    public ResponseEntity<List<LeaverequestDTO>> getFilteredRequests(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @AuthenticationPrincipal UserEntity currentUser) {

        // Nếu là Manager, tự động lọc theo phòng ban của họ
        Integer effectiveDeptId = departmentId;
        if (currentUser.getAuthorities().contains("Manager")) {
            effectiveDeptId = currentUser.getDepartmentID().getId();
        }


        LeaveRequestFilterCriteria criteria = new LeaveRequestFilterCriteria(
                effectiveDeptId,
                username,
                status,
                fromDate,
                toDate
        );

        List<LeaverequestDTO> requests = leaverequestService.getFilteredRequests(criteria);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/user/leaverequest/approve/{id}") // Dùng Patch vì chỉ thay đổi 1 phần (Status)
    @PreAuthorize("hasAnyAuthority('Manager','HR')")
    public ResponseEntity<String> approveRequest(@PathVariable("id") Integer leaveRequestId) {
        try {
            leaverequestService.approveRequest(leaveRequestId);
            return ResponseEntity.ok("Đã duyệt (APPROVED) đơn nghỉ phép thành công. Email thông báo đang được gửi.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/user/leaverequest/reject/{id}")
    @PreAuthorize("hasAnyAuthority('Manager','HR')")
    public ResponseEntity<String> rejectRequest(@PathVariable("id") Integer leaveRequestId) {
        try {
            leaverequestService.rejectRequest(leaveRequestId);
            return ResponseEntity.ok("Đã từ chối (REJECTED) đơn nghỉ phép. Email thông báo đang được gửi.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/user/leaverequest/myBalances")
    public ResponseEntity<List<LeaveBalanceDTO>> getMyLeaveBalances(
            @AuthenticationPrincipal UserEntity currentUser
    ) {

        if (currentUser == null) {
            return ResponseEntity.status(401).build(); // Chưa đăng nhập
        }

        List<LeaveBalanceDTO> balances = leaverequestService.getMyLeaveBalances(currentUser);
        return ResponseEntity.ok(balances);
    }
}
