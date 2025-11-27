package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.PayrollDTO;
import com.manaplastic.backend.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hr/payroll")
@PreAuthorize("hasAuthority('HR')")
@CrossOrigin(origins = "*")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    // /hr/payroll/calculate?month=11&year=2025
    @PostMapping("/calculate")
    public ResponseEntity<?> calculatePayroll(
            @RequestParam int month,
            @RequestParam int year) {
        try {
            // Validate đầu vào cơ bản
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


    // /hr/payroll/list?month=11&year=2025
    @GetMapping("/list")
    public ResponseEntity<?> getPayrollList(
            @RequestParam int month,
            @RequestParam int year) {
        try {
            List<PayrollDTO> payrolls = payrollService.getPayrollsByMonth(month, year);

            if (payrolls.isEmpty()) {
                return ResponseEntity.ok().body("Chưa có dữ liệu lương cho tháng này. Hãy chạy tính lương trước!");
            }

            return ResponseEntity.ok(payrolls);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi lấy dữ liệu: " + e.getMessage());
        }
    }



}
