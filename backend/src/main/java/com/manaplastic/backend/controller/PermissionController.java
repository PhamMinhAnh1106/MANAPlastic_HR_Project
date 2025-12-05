package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.PermissionDTO;
import com.manaplastic.backend.DTO.PermissionFilterCriteria;
import com.manaplastic.backend.DTO.UpdateUserPermissionDTO;
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

import java.util.List;

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
    @RequiredPermission(PermissionConst.ACCOUNT_PERMISSION)
    public ResponseEntity<String> updatePermission(@RequestBody UpdateUserPermissionDTO request) {
        try {
            permissionManageService.updateUserPermission(request);
            String action = (request.getActivePermission() == 1) ? "CẤP (Whitelist)" : "CHẶN (Blacklist)";
            return ResponseEntity.ok("Đã " + action + " quyền thành công cho user.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
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
    @RequiredPermission(PermissionConst.ACCOUNT_PERMISSION)
    public ResponseEntity<String> resetPermission(
            @PathVariable Integer permissionId,
            @RequestParam String username
    ) {
        try {
            permissionManageService.resetUserPermission(username, permissionId);
            return ResponseEntity.ok("Đã reset quyền (ID: " + permissionId + ") của user [" + username + "] về mặc định.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}