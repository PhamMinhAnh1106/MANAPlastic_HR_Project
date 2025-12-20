package com.manaplastic.backend.controller.account;

import com.manaplastic.backend.DTO.account.RolePermissionDTO;
import com.manaplastic.backend.DTO.account.UpdateRolePermissionDTO;
import com.manaplastic.backend.DTO.criteria.RolePermissionCriteria;
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

import java.util.List;

@RestController
@RequestMapping("/admin/rolePermissions")
@PreAuthorize("hasAuthority('Admin')")
public class RolePermissionController {

    @Autowired
    private PermissionService permissionService;

    //Lấy danh sách quyền của Role
    @GetMapping("/{roleId}")
    @RequiredPermission(PermissionConst.ACCOUNT_PERMISSION)
    public ResponseEntity<Page<RolePermissionDTO>> getPermissionsByRole(
            @PathVariable Integer roleId,
            @ModelAttribute RolePermissionCriteria criteria,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Page<RolePermissionDTO> result = permissionService.getPermissionsByRole(roleId, criteria, pageable);
        return ResponseEntity.ok(result);
    }

    //Cập nhật trạng thái quyền của Role
    @PutMapping("/update")
    @LogActivity(action="UPDATE_ROLE_PERMISSION", description = "Mở quyền/Ngăn chặn khẩn cấp quyền mặc đinh của role", logType = LogType.WARNING)
    @RequiredPermission(PermissionConst.ACCOUNT_PERMISSION)
    public ResponseEntity<String> updateRolePermission(@RequestBody UpdateRolePermissionDTO dto) {
        permissionService.updateRolePermission(dto);
        return ResponseEntity.ok("Cập nhật quyền cho Role thành công!");
    }
}
