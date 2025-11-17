package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.FinalizeScheduleDTO;
import com.manaplastic.backend.DTO.ScheduleRequirementDTO;
import com.manaplastic.backend.DTO.ScheduleValidationDTO;
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
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<?> autoAssignBlanks(
            @AuthenticationPrincipal UserEntity manager,
            @RequestBody FinalizeScheduleDTO dto // Dùng lại DTO (chỉ cần month_year)
    ) {
        autoAssignScheduleService.autoAssignBlankSchedules(dto.month_year(), manager.getId());
        return ResponseEntity.ok("Đã tự động xếp ca cho các ngày trống. Vui lòng kiểm tra lại bảng nháp.");
    }


    @GetMapping("/shiftSchedule/drafts/validate")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<List<ScheduleValidationDTO>> validateDepartmentDraft(
            @AuthenticationPrincipal UserEntity manager,
            @RequestParam("month_year") String monthYear
    ) {
        List<ScheduleValidationDTO> validationResults = autoAssignScheduleService.validateDraftSchedule(monthYear, manager.getId());
        return ResponseEntity.ok(validationResults);
    }

    @GetMapping("/requirements")
    public ResponseEntity<List<ScheduleRequirementDTO>> getRequirements(
            @AuthenticationPrincipal UserEntity manager) {

        List<ScheduleRequirementDTO> requirements = requirementService.getRequirementsForManager(manager);
        return ResponseEntity.ok(requirements);
    }

    @PostMapping("/requirements")
    public ResponseEntity<ScheduleRequirementDTO> createRequirement(
            @AuthenticationPrincipal UserEntity manager,
            @RequestBody ScheduleRequirementDTO requirementDTO) {

        ScheduleRequirementDTO savedDto = requirementService.createRequirement(manager, requirementDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }


    @PutMapping("/requirements/{id}")
    public ResponseEntity<ScheduleRequirementDTO> updateRequirement(
            @AuthenticationPrincipal UserEntity manager,
            @PathVariable Integer id,
            @RequestBody ScheduleRequirementDTO requirementDTO) {

        ScheduleRequirementDTO updatedDto = requirementService.updateRequirement(id, requirementDTO, manager);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/requirements/{id}")
    public ResponseEntity<Void> deleteRequirement(
            @AuthenticationPrincipal UserEntity manager,
            @PathVariable Integer id) {

        requirementService.deleteRequirement(id, manager);
        return ResponseEntity.noContent().build();
    }
}
