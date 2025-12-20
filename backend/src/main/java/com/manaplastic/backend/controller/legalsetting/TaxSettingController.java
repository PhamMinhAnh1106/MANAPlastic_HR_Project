package com.manaplastic.backend.controller.legalsetting;

import com.manaplastic.backend.DTO.legalsetting.TaxSettingDTO;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.TaxsettingEntity;
import com.manaplastic.backend.service.TaxSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/legalsetting/taxSettings")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('HR','Admin')")
public class TaxSettingController {

    @Autowired
    private TaxSettingService service;

    //Lấy all (lọc keyword)
    @GetMapping("")
    public ResponseEntity<List<TaxsettingEntity>> getList(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(service.getList(keyword));
    }


    //Lấy hiện hành
    @GetMapping("/current")
    public ResponseEntity<List<TaxsettingEntity>> getCurrentEffective() {
        return ResponseEntity.ok(service.getCurrentSettings());
    }

    //Thêm
    @PostMapping
    @LogActivity(action ="CREATE_TAX_SETTING", description = "Thêm mới chính sách thuế")
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<TaxsettingEntity> create(@RequestBody TaxSettingDTO req) {
        return ResponseEntity.ok(service.create(req));
    }

    //Sửa
    @PutMapping("/{id}")
    @LogActivity(action="UPDATE_TAX_SETTING", description = "Cập nhật chính sách thuế", logType = LogType.WARNING)
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<TaxsettingEntity> update(@PathVariable int id, @RequestBody TaxSettingDTO req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    //Xóa
    @DeleteMapping("/{id}")
    @LogActivity(action ="DELETE_TAX_SETTING", description = "Xóa chính sách thuế", logType = LogType.DANGER)
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<String> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.ok("Đã xóa thành công cấu hình ID: " + id);
    }
}
