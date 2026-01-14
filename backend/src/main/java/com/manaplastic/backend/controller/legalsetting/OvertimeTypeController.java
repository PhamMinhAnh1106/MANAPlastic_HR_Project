package com.manaplastic.backend.controller.legalsetting;

import com.manaplastic.backend.DTO.legalsetting.OvertimeTypeDTO;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.OvertimetypeEntity;
import com.manaplastic.backend.service.OvertimeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/legalsetting/overtimeTypes")
@CrossOrigin(origins = "*")
public class OvertimeTypeController {

    @Autowired
    private OvertimeTypeService service;

    // Lấy
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OvertimetypeEntity>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    //Thêm
    @PostMapping
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @LogActivity(action ="CREATE_OVERTIME_TYPE",description = "Thêm chính sách loại tăng ca mới")
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<OvertimetypeEntity> create(@RequestBody OvertimeTypeDTO req) {
        return ResponseEntity.ok(service.create(req));
    }

    //Sửa
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @LogActivity(action="UPDATE_OVERTIME_TYPE",description = "Sửa chính sách loại tăng ca",logType = LogType.WARNING)
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<OvertimetypeEntity> update(@PathVariable int id, @RequestBody OvertimeTypeDTO req) {
        return ResponseEntity.ok(service.update(id, req));
    }
        // Xóa
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @LogActivity(action="DELETE_OVERTIME_TYPE", description = "Xóa chính sách loại tăng ca", logType = LogType.DANGER)
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<String> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.ok("Đã xóa thành công loại tăng ca ID: " + id);
    }
}
