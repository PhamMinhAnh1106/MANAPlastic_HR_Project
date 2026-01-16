package com.manaplastic.backend.controller.payroll;

import com.manaplastic.backend.DTO.payroll.PayrollDTO;
import com.manaplastic.backend.DTO.criteria.PayrollFilterCriteria;
import com.manaplastic.backend.DTO.payroll.PayrollDetailDTO;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.payrollengine.service.PayslipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/payroll")
@CrossOrigin(origins = "*")
public class PayslipController {

    @Autowired
    private PayslipService payslipService;

    // Lấy của toi
    @GetMapping("/my-payslip")
    @PreAuthorize("isAuthenticated()")
    @RequiredPermission(PermissionConst.PAYROLL_VIEW_SELF)
    public ResponseEntity<?> getMyPayslip(
            @RequestParam int month,
            @RequestParam int year,
            @AuthenticationPrincipal UserEntity currentUser) {
        try {
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User chưa đăng nhập!");
            }

            PayrollDetailDTO payslip = payslipService.getMyPayslip(currentUser, month, year);
            return ResponseEntity.ok(payslip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    //    // Lọc
//    @GetMapping("/filter")
//    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
//    public ResponseEntity<?> filterPayrolls(
//            @ModelAttribute PayrollFilterCriteria criteria,
//            // tham số phân trang
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "netsalary") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDir
//    ) {
//        try {
//            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//            Pageable pageable = PageRequest.of(page, size, sort);
//            Page<PayrollDTO> result = payslipService.getPayrollList(criteria, pageable);
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body("Lỗi lọc lương: " + e.getMessage());
//        }
//    }
    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @RequiredPermission(PermissionConst.PAYROLL_VIEW_ALL)
    public ResponseEntity<Page<PayrollDTO>> filterPayrolls(
            @ModelAttribute PayrollFilterCriteria criteria,
            @PageableDefault(page = 0, size = 10, sort = "netsalary", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PayrollDTO> result = payslipService.getPayrollList(criteria, pageable);
        return ResponseEntity.ok(result);
    }

    // Lấy của user nhân sự muốn xem
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @RequiredPermission(PermissionConst.PAYROLL_VIEW_ALL)
    public ResponseEntity<?> getPayrollDetailById(
            @PathVariable Integer userId,
            @RequestParam int month,
            @RequestParam int year
    ) {
        PayrollDetailDTO payslipDetail = payslipService.getPayrollDetailById(userId, month, year);
            return ResponseEntity.ok(payslipDetail);
    }
}