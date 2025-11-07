package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.DraftRegistrationDTO;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    @PostMapping("/employee/shiftSchedule/myDraft")
    public ResponseEntity<?> handleDraftScheduleRegistration(
            @RequestBody List<DraftRegistrationDTO> registrationDTOs,
            @AuthenticationPrincipal UserEntity user
    ) {
        Integer employeeId = user.getId();
        scheduleService.registerDraftSchedule(registrationDTOs, employeeId);
        return ResponseEntity.ok("Draft schedule saved successfully.");
    }

    @GetMapping("/employee/shiftSchedule/myDraft")
    public ResponseEntity<List<DraftRegistrationDTO>> getMyDraftSchedule(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam("month_year") String monthYear
    ) {
        Integer employeeId = user.getId();
        List<DraftRegistrationDTO> draftSchedules = scheduleService.getDraftSchedule(employeeId, monthYear);
        return ResponseEntity.ok(draftSchedules);
    }

}
