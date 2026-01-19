package com.manaplastic.backend.controller.schedule;

import com.manaplastic.backend.DTO.schedule.AddLeaverequestDTO;
import com.manaplastic.backend.DTO.schedule.LeaveBalanceDTO;
import com.manaplastic.backend.DTO.criteria.LeaveRequestFilterCriteria;
import com.manaplastic.backend.DTO.schedule.LeaverequestDTO;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.LeaverequestService;
import com.manaplastic.backend.service.CheckPermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LeaverequestController {
    @Autowired
    private LeaverequestService leaverequestService;
    @Autowired
    private CheckPermissionService checkPermissionService;


    //nhân viên và quản lý, HR dùng chung đăng ký, xem, xóa đơn nghỉ phép
    @PostMapping("/user/leaverequest/addRequest")
    @RequiredPermission(PermissionConst.LEAVE_CREATE)
//    @PreAuthorize("hasAnyAuthority('Employee','Manager','HR')")
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
    @RequiredPermission(PermissionConst.LEAVE_VIEW_SELF)
    public ResponseEntity<List<LeaverequestDTO>> getMyLeaveRequests(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<LeaverequestDTO> myRequests = leaverequestService.getMyLeaveRequests(currentUser);
        return ResponseEntity.ok(myRequests);
    }

    //chỉ được xóa đơn khi đơn đang là PENDING
    @DeleteMapping("/user/leaverequest/myRequest/{id}")
    @PreAuthorize("isAuthenticated()")
    @RequiredPermission(PermissionConst.LEAVE_CANCEL)
    public ResponseEntity<String> deleteMyLeaveRequest(@PathVariable("id") Integer leaveRequestId,
                                                       @AuthenticationPrincipal UserEntity currentUser) {
            leaverequestService.deleteMyLeaveRequest(leaveRequestId, currentUser);
            String responseMessage = "Đã xóa đơn nghỉ phép thành công.";
            return ResponseEntity.ok(responseMessage);
    }
    //Lọc đơn
//    @GetMapping("/user/leaverequest/filter")
//    @PreAuthorize("hasAnyAuthority('Manager','HR')")
//    public ResponseEntity<List<LeaverequestDTO>> getFilteredRequests(
//            @ModelAttribute LeaveRequestFilterCriteria criteria,
//            @AuthenticationPrincipal UserEntity currentUser) {
//
//        boolean isManager = currentUser.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("Manager"));
//
//        if (isManager) {
//            if (currentUser.getDepartmentID() != null) {
//                criteria.setDepartmentId(currentUser.getDepartmentID().getId());
//            }
//        }
//
//        List<LeaverequestDTO> requests = leaverequestService.getFilteredRequests(criteria);
//        return ResponseEntity.ok(requests);
//    }

    @GetMapping("/user/leaverequest/filter")
    @PreAuthorize("hasAnyAuthority('Manager','HR')")
    public ResponseEntity<Page<LeaverequestDTO>> getFilteredRequests(@ModelAttribute LeaveRequestFilterCriteria criteria,
                                                                     @AuthenticationPrincipal UserEntity currentUser,
                                                                     @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
//        boolean isManager = currentUser.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("Manager"));
        boolean canViewAll = checkPermissionService.checkPermission(currentUser.getId(), PermissionConst.LEAVE_VIEW_ALL);
        boolean canViewDept = checkPermissionService.checkPermission(currentUser.getId(), PermissionConst.LEAVE_VIEW_DEPT);

//        if (!isManager) {
//           //Là HR
//        }
//        else{
//            if (currentUser.getDepartmentID() != null) {
//                criteria.setDepartmentId(currentUser.getDepartmentID().getId());
//            }
//        }
        if (canViewAll) {
            // Là HR
        }
        else if (canViewDept) {
            if (currentUser.getDepartmentID() == null) {
                throw new RuntimeException("Tài khoản Manager chưa được gán phòng ban, không thể xem dữ liệu.");
            }
            criteria.setDepartmentId(currentUser.getDepartmentID().getId());
        }

        Page<LeaverequestDTO> result = leaverequestService.getFilteredRequests(criteria, pageable);
        return ResponseEntity.ok(result);
    }


    @PatchMapping("/user/leaverequest/approve/{id}") // Dùng Patch vì chỉ thay đổi 1 phần (Status)
    @PreAuthorize("hasAnyAuthority('Manager','HR')")
    @RequiredPermission(PermissionConst.LEAVE_APPROVE)
    public ResponseEntity<String> approveRequest(@PathVariable("id") Integer leaveRequestId,
                                                 @AuthenticationPrincipal UserEntity currentUser) {
            leaverequestService.approveRequest(leaveRequestId,currentUser);
            return ResponseEntity.ok("Đã duyệt (APPROVED) đơn nghỉ phép thành công. Email thông báo đang được gửi.");
    }

    @PatchMapping("/user/leaverequest/reject/{id}")
    @PreAuthorize("hasAnyAuthority('Manager','HR')")
    @RequiredPermission(PermissionConst.LEAVE_APPROVE)
    public ResponseEntity<String> rejectRequest(@PathVariable("id") Integer leaveRequestId,
                                                @AuthenticationPrincipal UserEntity currentUser) {
            leaverequestService.rejectRequest(leaveRequestId,currentUser);
            return ResponseEntity.ok("Đã từ chối (REJECTED) đơn nghỉ phép. Email thông báo đang được gửi.");

    }

    @GetMapping("/user/leaverequest/myBalances")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LeaveBalanceDTO>> getMyLeaveBalances(
            @AuthenticationPrincipal UserEntity currentUser
    ) {

        if (currentUser == null) {
            return ResponseEntity.status(401).build(); // Chưa đăng nhập
        }

        List<LeaveBalanceDTO> balances = leaverequestService.getMyLeaveBalances(currentUser);
        return ResponseEntity.ok(balances);
    }

    @PostMapping("/user/leaverequest/generate-new-year")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    public ResponseEntity<String> triggerGenerateNewYear() {
        try {
            // Gọi hàm logic trong Service
            leaverequestService.generateLeaveBalanceForNewYear();
            return ResponseEntity.ok("Đã chạy trigger tạo phép năm mới thành công (cho năm hiện tại).");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi khi chạy trigger: " + e.getMessage());
        }
    }
}
