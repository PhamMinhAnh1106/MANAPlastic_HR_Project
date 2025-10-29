package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.UserProfileDTO;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Nhân Viên")
public class EmployeeController {
    @GetMapping("/homePage")
    public ResponseEntity<UserProfileDTO> getMyInfo(@AuthenticationPrincipal UserEntity currentUser) {

        UserProfileDTO userProfile = UserProfileDTO.builder()
                .userID(currentUser.getId())
                .username(currentUser.getUsername())
                .fullname(currentUser.getFullname())
                .email(currentUser.getEmail())
                .phonenumber(currentUser.getPhonenumber())
                .address(currentUser.getAddress())
                .roleName(currentUser.getRoleID().getRolename())
                .departmentID(currentUser.getDepartmentID().getId())
                .build();
        return ResponseEntity.ok(userProfile);
    }
}
