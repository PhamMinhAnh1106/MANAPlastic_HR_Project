package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.PayrollDTO;
import com.manaplastic.backend.DTO.PayrollFilterCriteria;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.payrollengine.service.PayslipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<?> getMyPayslip(
            @RequestParam int month,
            @RequestParam int year,
            @AuthenticationPrincipal UserEntity currentUser) {
        try {
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User chưa đăng nhập!");
            }
            int userId = currentUser.getId();
            Map<String, Object> payslip = payslipService.getMyPayslip(userId, month, year);
            return ResponseEntity.ok(payslip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // Lọc
    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
    public ResponseEntity<?> filterPayrolls(
            @ModelAttribute PayrollFilterCriteria criteria,
            // tham số phân trang
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "netsalary") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<PayrollDTO> result = payslipService.getPayrollList(criteria, pageable);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi lọc lương: " + e.getMessage());
        }
    }

    // Lấy của user nhân sự muốn xem
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> getPayrollDetailById(
            @PathVariable Integer userId,
            @RequestParam int month,
            @RequestParam int year
    ) {
        try {
            Map<String, Object> payslipDetail = payslipService.getMyPayslip(userId, month, year);
            return ResponseEntity.ok(payslipDetail);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi lấy chi tiết lương: " + e.getMessage());
        }
    }
}