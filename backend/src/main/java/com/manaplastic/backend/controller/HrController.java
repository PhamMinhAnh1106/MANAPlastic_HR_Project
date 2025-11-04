package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.*;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.AttendanceService;
import com.manaplastic.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hr")
@PreAuthorize("hasAuthority('HR')")
public class HrController {
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

    // Bộ lọc user theo các tiêu chi
    @GetMapping("/userFilter")
    public ResponseEntity<List<UserProfileDTO>> filterUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Integer roleId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDateEnd,
            Pageable pageable) {

        // Gói các tham số gọi 1 lần
        UserFilterCriteria criteria = new UserFilterCriteria(
                keyword, departmentId, roleId, status, gender, hireDateStart, hireDateEnd
        );

        List<UserProfileDTO> userList = userService.filterUsersList(criteria, pageable);

        return ResponseEntity.ok(userList);
    }

    //lấy thông tin tài khoản nhân sự muốn xem
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfileDTO> getUserDetails(@PathVariable int userId) {
        UserProfileDTO userDetails = userService.getUserDetailsById(userId);
        return ResponseEntity.ok(userDetails);
    }

    //sửa thông tin tài khoản cho nhân sự
    @PutMapping("/user/{userId}")
    public ResponseEntity<String> hrUpdateUser(
            @PathVariable int userId,
            @RequestBody UpdateAccountDTO request,
            @AuthenticationPrincipal UserEntity currentUser) {
        userService.updateAccount(userId, request, currentUser);
        return ResponseEntity.ok("Cập nhật tài khoản thành công!");

        //return ResponseEntity.ok(updatedUser);
    }

    //Xem và lọc dữ liueeuj chấm công theo tháng năm
    @GetMapping("/chamCong")
    public ResponseEntity<List<AttendanceDTO>> getMyAttendance(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer departmentId) {

        AttendanceFilterCriteria criteria = new AttendanceFilterCriteria(
                month, year, departmentId, null, status
        );

        List<AttendanceDTO> list = attendanceService.getFilteredAttendance(criteria);

        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/chamCong/{attendanceId}")
    public ResponseEntity<String> deleteAttendance(@PathVariable int attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.ok("Xóa thành công!");
    }
}
