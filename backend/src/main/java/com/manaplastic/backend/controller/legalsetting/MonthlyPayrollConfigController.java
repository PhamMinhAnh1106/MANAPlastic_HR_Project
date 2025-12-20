package com.manaplastic.backend.controller.legalsetting;


import com.manaplastic.backend.DTO.legalsetting.GenegatedYearlyConfigRequestDTO;
import com.manaplastic.backend.DTO.legalsetting.UpdateMonthlyConfigRequestDTO;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.MonthlypayrollconfigEntity;
import com.manaplastic.backend.service.MonthlyConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/legalsetting/monthlyConfig")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('HR', 'Admin')")
public class MonthlyPayrollConfigController {

    @Autowired
    private MonthlyConfigService service;

    // Generate cả năm
    @PostMapping("/generateYear")
    public ResponseEntity<String> generateYearly(@RequestBody GenegatedYearlyConfigRequestDTO req) {
        return ResponseEntity.ok(service.generateYearlyConfig(req));
    }

    // Cập nhật 1 tháng
    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_MONTHLY_CONFIG", description = "Cập nhật chu kỳ lương", logType = LogType.WARNING)
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody UpdateMonthlyConfigRequestDTO req) {
        return ResponseEntity.ok(service.updateConfig(id, req));
    }

    // Lấy danh sách
    @GetMapping
    public ResponseEntity<List<MonthlypayrollconfigEntity>> getList(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        if (month != null && year == null) {
            throw new IllegalArgumentException("Nếu lọc theo tháng, vui lòng chọn thêm năm.");
        }

        return ResponseEntity.ok(service.getConfigFilter(year, month));
    }

}
