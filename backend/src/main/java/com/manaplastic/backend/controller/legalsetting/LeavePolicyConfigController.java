package com.manaplastic.backend.controller.legalsetting;


import com.manaplastic.backend.DTO.legalsetting.LeavePolicyDTO;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.LeavepolicyEntity;
import com.manaplastic.backend.service.LeavePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/legalsetting/leavePolicies")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('HR', 'Admin')")
public class LeavePolicyConfigController {
    @Autowired
    private LeavePolicyService service;

    // Lấy danh sách
    @GetMapping
    public ResponseEntity<List<LeavepolicyEntity>> getAll() {
        return ResponseEntity.ok(service.getAllPolicies());
    }

    // Tạo mới
    @PostMapping
    @LogActivity(action = "CREATE_LEAVE_POLICY", description = "Thêm mới chính sách nghỉ phép")
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<LeavepolicyEntity> create(@RequestBody LeavePolicyDTO req) {
        return ResponseEntity.ok(service.createPolicy(req));
    }

    //Cập nhật
    @PutMapping("/{id}")
    @LogActivity(action="UPDATE_LEAVE_POLICY",description = "Cập nhật chính sách nghỉ phép",logType = LogType.WARNING)
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<LeavepolicyEntity> update(@PathVariable int id, @RequestBody LeavePolicyDTO req) {
        return ResponseEntity.ok(service.updatePolicy(id, req));
    }

    // Xóa
    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_LEAVE_POLICY", description = "Xóa chính sách nghỉ phép",logType = LogType.DANGER)
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<String> delete(@PathVariable int id) {
        service.deletePolicy(id);
        return ResponseEntity.ok("Đã xóa thành công chính sách ID: " + id);
    }
}
