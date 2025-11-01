package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.UserProfileDTO;
import com.manaplastic.backend.DTO.UserUpdatein4DTO;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
@PreAuthorize("hasRole('Employee')")
public class EmployeeController {

    @Autowired
    private UserService userService;

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
    @PutMapping("/updateAccount")
    public ResponseEntity<String> updateMyProfile(@AuthenticationPrincipal UserEntity currentUser, @RequestBody UserUpdatein4DTO updateRequest) {
        try {
            userService.updateUserProfile(currentUser.getId(), updateRequest);
            String responseMessage = "Tài khoản đã được cập nhật thành công.";
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
