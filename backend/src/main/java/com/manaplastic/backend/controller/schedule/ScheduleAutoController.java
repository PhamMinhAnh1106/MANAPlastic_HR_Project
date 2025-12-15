package com.manaplastic.backend.controller.schedule;

import com.manaplastic.backend.DTO.schedule.FinalizeScheduleDTO;
import com.manaplastic.backend.DTO.schedule.ScheduleRequirementDTO;
import com.manaplastic.backend.DTO.schedule.ScheduleValidationDTO;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.AutoAssignScheduleService;
import com.manaplastic.backend.service.ScheduleRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@PreAuthorize("hasAuthority('Manager')")
@RequiredArgsConstructor
public class ScheduleAutoController {

    private final ScheduleRequirementService requirementService;
    private final AutoAssignScheduleService autoAssignScheduleService;

    // Auto xếp ca
    @PostMapping("/shiftSchedule/auto-assign")
    @RequiredPermission(PermissionConst.SHIFT_ASSIGN)
    public ResponseEntity<?> autoAssignBlanks(
            @AuthenticationPrincipal UserEntity manager,
            @RequestBody FinalizeScheduleDTO dto // Dùng lại DTO (chỉ cần month_year)
    ) {
        autoAssignScheduleService.autoAssignBlankSchedules(dto.month_year(), manager.getId());
        return ResponseEntity.ok("Đã tự động xếp ca cho các ngày trống. Vui lòng kiểm tra lại bảng nháp.");
    }


    @GetMapping("/shiftSchedule/drafts/validate")
    @RequiredPermission(PermissionConst.SHIFT_ASSIGN)
    public ResponseEntity<List<ScheduleValidationDTO>> validateDepartmentDraft(
            @AuthenticationPrincipal UserEntity manager,
            @RequestParam("month_year") String monthYear
    ) {
        List<ScheduleValidationDTO> validationResults = autoAssignScheduleService.validateDraftSchedule(monthYear, manager.getId());
        return ResponseEntity.ok(validationResults);
    }

    @GetMapping("/requirements")
    @RequiredPermission(PermissionConst.SHIFT_ASSIGN)
    public ResponseEntity<List<ScheduleRequirementDTO>> getRequirements(
            @AuthenticationPrincipal UserEntity manager) {

        List<ScheduleRequirementDTO> requirements = requirementService.getRequirementsForManager(manager);
        return ResponseEntity.ok(requirements);
    }

    @PostMapping("/requirements")
    @RequiredPermission(PermissionConst.SHIFT_ASSIGN)
    public ResponseEntity<ScheduleRequirementDTO> createRequirement(
            @AuthenticationPrincipal UserEntity manager,
            @RequestBody ScheduleRequirementDTO requirementDTO) {

        ScheduleRequirementDTO savedDto = requirementService.createRequirement(manager, requirementDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }


    @PutMapping("/requirements/{id}")
    @RequiredPermission(PermissionConst.SHIFT_ASSIGN)
    public ResponseEntity<ScheduleRequirementDTO> updateRequirement(
            @AuthenticationPrincipal UserEntity manager,
            @PathVariable Integer id,
            @RequestBody ScheduleRequirementDTO requirementDTO) {

        ScheduleRequirementDTO updatedDto = requirementService.updateRequirement(id, requirementDTO, manager);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/requirements/{id}")
    @RequiredPermission(PermissionConst.SHIFT_ASSIGN)
    public ResponseEntity<Void> deleteRequirement(
            @AuthenticationPrincipal UserEntity manager,
            @PathVariable Integer id) {

        requirementService.deleteRequirement(id, manager);
        return ResponseEntity.noContent().build();
    }
}
