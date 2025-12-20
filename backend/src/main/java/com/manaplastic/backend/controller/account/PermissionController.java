package com.manaplastic.backend.controller.account;

import com.manaplastic.backend.DTO.account.PermissionDTO;
import com.manaplastic.backend.DTO.criteria.PermissionFilterCriteria;
import com.manaplastic.backend.DTO.account.UpdateUserPermissionDTO;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/permissions")
@PreAuthorize("hasAuthority('Admin')")
public class PermissionController {

    @Autowired
    private PermissionService permissionManageService;

    // Xem danh sách quyền của 1 user
    @GetMapping("/user/{username}")
    @RequiredPermission(PermissionConst.ACCOUNT_PERMISSION)
    public ResponseEntity<Page<PermissionDTO>> getUserPermissions(@PathVariable String username,
                                                                  @ModelAttribute PermissionFilterCriteria criteria,
                                                                  @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(permissionManageService.getAllPermissionsForUser(username, criteria,pageable));
//        return ResponseEntity.ok(permissionManageService.getAllPermissionsForUser(username));
    }

    // Cấp hoặc Chặn quyền
    @PostMapping("/update")
    @LogActivity(action="ADD_ACCOUNT_PERMISSION", description = "Cấp / Chặn quyền của tài khoản")
    @RequiredPermission(PermissionConst.ACCOUNT_PERMISSION)
    public ResponseEntity<String> updatePermission(@RequestBody UpdateUserPermissionDTO request) {
            permissionManageService.updateUserPermission(request);
            String action = (request.getActivePermission() == 1) ? "CẤP (Whitelist)" : "CHẶN (Blacklist)";
            return ResponseEntity.ok("Đã " + action + " quyền thành công cho user.");
    }

    // Xóa cấu hình (Reset về mặc định Role)
//    @DeleteMapping("/reset")
//    @RequiredPermission(PermissionConst.ACCOUNT_PERMISSION)
//    public ResponseEntity<String> resetPermission(@RequestParam Integer userId, @RequestParam Integer permissionId) {
//        try {
//            permissionManageService.resetUserPermission(userId, permissionId);
//            return ResponseEntity.ok("Đã xóa cấu hình quyền riêng. User sẽ tuân theo quyền của Role.");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
//        }
//    }


    @DeleteMapping("/reset/{permissionId}")
    @LogActivity(action = "RESET_ACCOUNT_PERMISSION", description = "Trả về quyền mặc định của tài khoản",logType = LogType.DANGER)
    @RequiredPermission(PermissionConst.ACCOUNT_PERMISSION)
    public ResponseEntity<String> resetPermission(
            @PathVariable Integer permissionId,
            @RequestParam String username
    ) {

        permissionManageService.resetUserPermission(username, permissionId);
        return ResponseEntity.ok("Đã reset quyền (ID: " + permissionId + ") của user [" + username + "] về mặc định.");
    }
}