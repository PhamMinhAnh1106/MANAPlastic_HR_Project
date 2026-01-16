package com.manaplastic.backend.controller.payroll;

import com.manaplastic.backend.DTO.payroll.RewardPunishmentDTO;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.service.RewardPunishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hr/rewaPunis")
@PreAuthorize("hasAnyAuthority('HR','Admin')")
public class RewardPunishmentController {

    @Autowired
    private RewardPunishmentService service;

    // Lấy / Xem
    @GetMapping
    public ResponseEntity<Page<RewardPunishmentDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("decisionDate").descending());

        Page<RewardPunishmentDTO> result = service.getList(pageable, keyword);
        return ResponseEntity.ok(result);
    }

    // Lấy chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<RewardPunishmentDTO> getById(@PathVariable Integer id) {
        RewardPunishmentDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Tạo
    @PostMapping
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    @LogActivity(action = "CREATE_REWPUN", description = "Tạo mới thưởng/phạt cho nhân sự")
    public ResponseEntity<?> create(@RequestBody RewardPunishmentDTO dto) {
        RewardPunishmentDTO created = service.create(dto);
//        return ResponseEntity.ok(created);
        return ResponseEntity.ok("Thêm mới thành công!");
    }

    // Sửa
    @PutMapping("/{id}")
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    @LogActivity(action = "UPDATE_REWPUN", description = "Cập nhật thưởng/phạt cho nhân sự", logType = LogType.WARNING)
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody RewardPunishmentDTO dto) {
        RewardPunishmentDTO updated = service.update(id, dto);
//        return ResponseEntity.ok(updated);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

   // Xóa
    @DeleteMapping("/{id}")
    @RequiredPermission(PermissionConst.PAYROLL_CALCULATE)
    @LogActivity(action = "DELETE_REWPUN", description = "Xóa thưởng/phạt cho nhân sự", logType = LogType.DANGER)
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok("Xóa thành công !");
    }
}
