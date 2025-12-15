package com.manaplastic.backend.controller.legalsetting;


import com.manaplastic.backend.DTO.legalsetting.LeavePolicyDTO;
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
    public ResponseEntity<LeavepolicyEntity> create(@RequestBody LeavePolicyDTO req) {
        return ResponseEntity.ok(service.createPolicy(req));
    }

    //Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<LeavepolicyEntity> update(@PathVariable int id, @RequestBody LeavePolicyDTO req) {
        return ResponseEntity.ok(service.updatePolicy(id, req));
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        service.deletePolicy(id);
        return ResponseEntity.ok("Đã xóa thành công chính sách ID: " + id);
    }
}
