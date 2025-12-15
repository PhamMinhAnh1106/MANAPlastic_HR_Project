package com.manaplastic.backend.controller.legalsetting;

import com.manaplastic.backend.DTO.legalsetting.TaxSettingDTO;
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
    public ResponseEntity<TaxsettingEntity> create(@RequestBody TaxSettingDTO req) {
        return ResponseEntity.ok(service.create(req));
    }

    //Sửa
    @PutMapping("/{id}")
    public ResponseEntity<TaxsettingEntity> update(@PathVariable int id, @RequestBody TaxSettingDTO req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    //Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.ok("Đã xóa thành công cấu hình ID: " + id);
    }
}
