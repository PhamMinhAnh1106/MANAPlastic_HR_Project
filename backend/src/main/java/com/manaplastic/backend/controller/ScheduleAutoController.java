package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.ScheduleRequirementDTO;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.ScheduleRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager/requirements")
@PreAuthorize("hasAuthority('Manager')")
@RequiredArgsConstructor
public class ScheduleAutoController {

    private final ScheduleRequirementService requirementService;

    @GetMapping
    public ResponseEntity<List<ScheduleRequirementDTO>> getRequirements(
            @AuthenticationPrincipal UserEntity manager) {

        List<ScheduleRequirementDTO> requirements = requirementService.getRequirementsForManager(manager);
        return ResponseEntity.ok(requirements);
    }

    @PostMapping
    public ResponseEntity<ScheduleRequirementDTO> createRequirement(
            @AuthenticationPrincipal UserEntity manager,
            @RequestBody ScheduleRequirementDTO requirementDTO) {

        ScheduleRequirementDTO savedDto = requirementService.createRequirement(manager, requirementDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ScheduleRequirementDTO> updateRequirement(
            @AuthenticationPrincipal UserEntity manager,
            @PathVariable Integer id,
            @RequestBody ScheduleRequirementDTO requirementDTO) {

        ScheduleRequirementDTO updatedDto = requirementService.updateRequirement(id, requirementDTO, manager);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequirement(
            @AuthenticationPrincipal UserEntity manager,
            @PathVariable Integer id) {

        requirementService.deleteRequirement(id, manager);
        return ResponseEntity.noContent().build();
    }
}
