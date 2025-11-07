package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.*;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.AttendanceService;
import com.manaplastic.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@PreAuthorize("hasAuthority('Employee')")
public class EmployeeController {

    @Autowired
    private UserService userService;

    @Autowired
    private AttendanceService attendanceService;
    //Xem thông tin
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getMyInfo(@AuthenticationPrincipal UserEntity currentUser) {

        UserProfileDTO userProfile = UserProfileDTO.builder()
                .userID(currentUser.getId())
                .username(currentUser.getUsername())
                .fullname(currentUser.getFullname())
                .cccd(currentUser.getCccd())
                .email(currentUser.getEmail())
                .phonenumber(currentUser.getPhonenumber())
                .gender(currentUser.getGender())
                .birth(currentUser.getBirth())
                .address(currentUser.getAddress())
                .bankAccount(currentUser.getBankaccount())
                .bankName(currentUser.getBankname())
                .hireDate(currentUser.getHiredate())
                .roleName(currentUser.getRoleID().getRolename())
                .departmentID(currentUser.getDepartmentID().getId())
                .build();
        return ResponseEntity.ok(userProfile);
    }

    //sửa thông tin cá nhân
    @PutMapping("/updateProfile")
    public ResponseEntity<String> updateMyProfile(@AuthenticationPrincipal UserEntity currentUser, @RequestBody UpdateSelfIn4DTO updateRequest) {
        try {
            userService.updateUserProfile(currentUser.getId(), updateRequest);
            String responseMessage = "Tài khoản đã được cập nhật thành công.";
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // sửa pass
    @PutMapping("/changePass")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserEntity currentUser, @RequestBody ChangePasswordDTO request) {
        try {
            userService.changeUserPassword(currentUser, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Đổi mật khẩu thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //Xem và lọc dữ liueeuj chấm công theo tháng năm
    @GetMapping("/chamCong")
    public ResponseEntity<List<AttendanceDTO>> getMyAttendance(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserEntity currentUser) {

        AttendanceFilterCriteria criteria = new AttendanceFilterCriteria(
                month, year, null, currentUser.getId(), status // userId là bắt buộc
        );

        List<AttendanceDTO> list = attendanceService.getFilteredAttendance(criteria);

        return ResponseEntity.ok(list);
    }


}
