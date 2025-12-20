package com.manaplastic.backend.controller.account;

import com.manaplastic.backend.DTO.account.ChangePasswordDTO;
import com.manaplastic.backend.DTO.account.UpdateAccountDTO;
import com.manaplastic.backend.DTO.account.UpdateSelfIn4DTO;
import com.manaplastic.backend.DTO.account.UserProfileDTO;
import com.manaplastic.backend.DTO.criteria.UserFilterCriteria;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.AttendanceService;
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
@RequestMapping("/hr")
@PreAuthorize("hasAuthority('HR')")
public class HrController {
    @Autowired
    private UserService userService;

    @Autowired
    private AttendanceService attendanceService;

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

    // Bộ lọc user theo các tiêu chi
//    @GetMapping("/userFilter")
//    public ResponseEntity<List<UserProfileDTO>> filterUsers(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) Integer departmentId,
//            @RequestParam(required = false) Integer roleId,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) Integer gender,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDateStart,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDateEnd,
//            Pageable pageable) {
//
//        // Gói các tham số gọi 1 lần
//        UserFilterCriteria criteria = new UserFilterCriteria(
//                keyword, departmentId, roleId, status, gender, hireDateStart, hireDateEnd
//        );
//
//        List<UserProfileDTO> userList = userService.filterUsersList(criteria, pageable);
//
//        return ResponseEntity.ok(userList);
//    }
//    @GetMapping("/userFilter")
//    public ResponseEntity<List<UserProfileDTO>> filterUsers(@ModelAttribute UserFilterCriteria criteria, Pageable pageable) {
//        return ResponseEntity.ok(userService.filterUsersList(criteria, pageable));
//    }
    @GetMapping("/userFilter")
    @RequiredPermission(PermissionConst.ACCOUNT_VIEW_LIST)
    public ResponseEntity<Page<UserProfileDTO>> filterUsers(
            @ModelAttribute UserFilterCriteria criteria,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserProfileDTO> result = userService.filterUsersList(criteria, pageable);
        return ResponseEntity.ok(result);
    }

    //lấy thông tin tài khoản nhân sự muốn xem
    @GetMapping("/user/{userId}")
    @RequiredPermission(PermissionConst.ACCOUNT_VIEW_DETAIL)
    public ResponseEntity<UserProfileDTO> getUserDetails(@PathVariable int userId) {
        UserProfileDTO userDetails = userService.getUserDetailsById(userId);
        return ResponseEntity.ok(userDetails);
    }

    //sửa thông tin tài khoản cho nhân sự
    @PutMapping("/user/{userId}")
    @LogActivity(action = "UPDATE_ACCOUNT", description = "Sửa thông tin tài khoản")
    @RequiredPermission(PermissionConst.ACCOUNT_UPDATE)
    public ResponseEntity<String> hrUpdateUser(
            @PathVariable int userId,
            @RequestBody UpdateAccountDTO request,
            @AuthenticationPrincipal UserEntity currentUser) {
        userService.updateAccount(userId, request, currentUser);
        return ResponseEntity.ok("Cập nhật tài khoản thành công!");

        //return ResponseEntity.ok(updatedUser);
    }

    //    //Xem và lọc dữ liueeuj chấm công theo tháng năm
//    @GetMapping("/chamCong")
//    public ResponseEntity<List<AttendanceDTO>> getMyAttendance(
//            @RequestParam(required = false) Integer month,
//            @RequestParam(required = false) Integer year,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) Integer departmentId) {
//
//        AttendanceFilterCriteria criteria = new AttendanceFilterCriteria(
//                month, year, departmentId, null, status
//        );
//
//        List<AttendanceDTO> list = attendanceService.getFilteredAttendance(criteria);
//
//        return ResponseEntity.ok(list);
//    }
//    @GetMapping("/chamCong")
//    @RequiredPermission(PermissionConst.ATTENDANCE_VIEW_ALL)
//    public ResponseEntity<Page<AttendanceDTO>> getMyAttendance(@ModelAttribute AttendanceFilterCriteria criteria,
//                                                               @PageableDefault(page=0,size=10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
//        return ResponseEntity.ok(attendanceService.getFilteredAttendance(criteria,pageable));
//    }

//    @DeleteMapping("/chamCong/{attendanceId}")
//    @RequiredPermission(PermissionConst.ATTENDANCE_UPDATE)
//    public ResponseEntity<String> deleteAttendance(@PathVariable int attendanceId) {
//        attendanceService.deleteAttendance(attendanceId);
//        return ResponseEntity.ok("Xóa thành công!");
//    }
}
