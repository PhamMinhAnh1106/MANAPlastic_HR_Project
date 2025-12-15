package com.manaplastic.backend.controller.legalsetting;

import com.manaplastic.backend.DTO.legalsetting.OvertimeTypeDTO;
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
@PreAuthorize("hasAnyAuthority('HR','Admin')")
public class OvertimeTypeController {

    @Autowired
    private OvertimeTypeService service;

    // Lấy
    @GetMapping
    public ResponseEntity<List<OvertimetypeEntity>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    //Thêm
    @PostMapping
    public ResponseEntity<OvertimetypeEntity> create(@RequestBody OvertimeTypeDTO req) {
        return ResponseEntity.ok(service.create(req));
    }

    //Sửa
    @PutMapping("/{id}")
    public ResponseEntity<OvertimetypeEntity> update(@PathVariable int id, @RequestBody OvertimeTypeDTO req) {
        return ResponseEntity.ok(service.update(id, req));
    }
        // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.ok("Đã xóa thành công loại tăng ca ID: " + id);
    }
}
