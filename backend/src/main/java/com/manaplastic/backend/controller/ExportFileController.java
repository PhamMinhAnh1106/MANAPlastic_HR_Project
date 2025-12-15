package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.criteria.AttendanceFilterCriteria;
import com.manaplastic.backend.DTO.criteria.ContractFilterCriteria;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.service.AttendanceService;
import com.manaplastic.backend.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/hr")
@PreAuthorize("hasAuthority('HR')")
public class ExportFileController {// Tạm thời chỉ cho HR xuất file báo cáo thôi
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private ContractService contractService;

    @GetMapping("/attendace/exportExcel")
    @RequiredPermission(PermissionConst.ATTENDANCE_EXPORT)
    public ResponseEntity<InputStreamResource> exportExcel(@ModelAttribute AttendanceFilterCriteria criteria) {

        ByteArrayInputStream in = attendanceService.exportReport(criteria);

        String filename = "Bao_Cao_Cham_Cong.xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/contract/exportExcel")
    @RequiredPermission(PermissionConst.CONTRACT_EXPORT)
    public ResponseEntity<InputStreamResource> exportContracts(@ModelAttribute ContractFilterCriteria criteria) {

        ByteArrayInputStream in = contractService.exportContracts(criteria);

        String filename = "Danh_Sach_Hop_Dong.xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
