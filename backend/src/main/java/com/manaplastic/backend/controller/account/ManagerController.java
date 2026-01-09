package com.manaplastic.backend.controller.account;

import com.manaplastic.backend.DTO.account.ChangePasswordDTO;
import com.manaplastic.backend.DTO.account.UpdateSelfIn4DTO;
import com.manaplastic.backend.DTO.account.UserProfileDTO;
import com.manaplastic.backend.DTO.account.UserSuggestionDTO;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.AttendanceService;
import com.manaplastic.backend.service.CheckPermissionService;
import com.manaplastic.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@PreAuthorize("hasAuthority('Manager')")
public class ManagerController {

    @Autowired
    private UserService userService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private CheckPermissionService checkPermissionService;

    //Xem thông tin
    @GetMapping("/profile")
    @RequiredPermission(PermissionConst.PROFILE_VIEW)
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
                .departmentName(currentUser.getDepartmentID().getDepartmentname())
                .build();
        return ResponseEntity.ok(userProfile);
    }

    //sửa thông tin cá nhân
    @PutMapping("/updateProfile")
    @RequiredPermission(PermissionConst.PROFILE_UPDATE)
    public ResponseEntity<String> updateMyProfile(@AuthenticationPrincipal UserEntity currentUser, @RequestBody UpdateSelfIn4DTO updateRequest) {
            userService.updateUserProfile(currentUser.getId(), updateRequest);
            String responseMessage = "Tài khoản đã được cập nhật thành công.";
            return ResponseEntity.ok(responseMessage);

    }

    // sửa pass
    @PutMapping("/changePass")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserEntity currentUser, @RequestBody ChangePasswordDTO request) {
            userService.changeUserPassword(currentUser, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Đổi mật khẩu thành công.");
    }

    //Xem và lọc dữ liueeuj chấm công
//    @GetMapping("/chamCong")
//    public ResponseEntity<List<AttendanceDTO>> getMyAttendance(
//            @RequestParam(required = false) Integer month,
//            @RequestParam(required = false) Integer year,
//            @RequestParam(required = false) String status,
//            @AuthenticationPrincipal UserEntity currentUser) {
//
//        AttendanceFilterCriteria criteria = new AttendanceFilterCriteria(
//                month, year, null, currentUser.getId() , status// userId là bắt buộc
//        );
//
//        List<AttendanceDTO> list = attendanceService.getFilteredAttendance(criteria);
//
//        return ResponseEntity.ok(list);
//    }

    // API Search Autocomplete (Tìm theo Username)
    @GetMapping("/searchUsers")
    @PreAuthorize("hasAnyAuthority('Manager', 'HR', 'Admin')")
    public ResponseEntity<List<UserSuggestionDTO>> searchUsers(
            @AuthenticationPrincipal UserEntity currentUser,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword
    ) {

        List<UserSuggestionDTO> results = userService.searchUsers(currentUser, keyword);
        return ResponseEntity.ok(results);
    }
}
