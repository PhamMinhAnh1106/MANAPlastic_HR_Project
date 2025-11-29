package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.PayrollDTO;
import com.manaplastic.backend.DTO.PayrollFilterCriteria;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.PayrollService;
import com.manaplastic.backend.service.PayrollServiceHC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payroll")
@CrossOrigin(origins = "*")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @PostMapping("/calculate")
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> calculatePayroll(
            @RequestParam int month,
            @RequestParam int year) {
        try {
            if (month < 1 || month > 12) {
                return ResponseEntity.badRequest().body("Tháng không hợp lệ!");
            }
            payrollService.calculatePayrollForMonth(month, year);
            return ResponseEntity.ok().body("Đã tính lương xong cho kỳ " + month + "/" + year);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi tính lương: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> getPayrollList(@ModelAttribute PayrollFilterCriteria criteria) {
        try {
            if (criteria.getMonth() == null || criteria.getYear() == null) {
                return ResponseEntity.badRequest().body("Vui lòng chọn Tháng và Năm kỳ lương!");
            }
            List<PayrollDTO> payrolls = payrollService.getPayrollsByFilter(criteria);
            if (payrolls.isEmpty()) {
                return ResponseEntity.ok().body("Không tìm thấy dữ liệu lương phù hợp.");
            }
            return ResponseEntity.ok(payrolls);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi lấy dữ liệu: " + e.getMessage());
        }
    }

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
            PayrollDTO myPayroll = payrollService.getPayrollDetailForUser(userId, month, year);
            return ResponseEntity.ok(myPayroll);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

}
