package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.*;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    //Role Nhân viên - employee và quản lý - manager dùng chung
    // vì nhân viên hay quản lý đều có thể dăng ky lịch làm việc
    @PostMapping("/user/shiftSchedule/myDraft")
    public ResponseEntity<?> handleDraftScheduleRegistration(
            @RequestBody List<DraftRegistrationDTO> registrationDTOs,
            @AuthenticationPrincipal UserEntity user
    ) {
        Integer employeeId = user.getId();
        scheduleService.registerDraftSchedule(registrationDTOs, employeeId);
        return ResponseEntity.ok("Đăng ký ca làm việc thành công! Bạn đợi quản lý xếp lịch nhé <3 .");
    }

    @GetMapping("/user/shiftSchedule/myDraft")
    public ResponseEntity<List<DraftRegistrationDTO>> getMyDraftSchedule(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam("month_year") String monthYear
    ) {
        Integer employeeId = user.getId();
        List<DraftRegistrationDTO> draftSchedules = scheduleService.getDraftSchedule(employeeId, monthYear);
        return ResponseEntity.ok(draftSchedules);
    }

    @GetMapping("/user/shiftSchedule/myOfficial")
    public ResponseEntity<List<DraftRegistrationDTO>> getMyOfficialSchedule(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam("month_year") String monthYear // Bắt buộc: ?month_year=2025-12
    ) {
        Integer employeeId = user.getId();
        List<DraftRegistrationDTO> officialSchedules = scheduleService.getOfficialSchedule(employeeId, monthYear);
        return ResponseEntity.ok(officialSchedules);
    }

    //Role Quản Lý - manager
    @GetMapping("/manager/shiftSchedule/drafts")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<List<EmployeeDraftSummaryDTO>> getDepartmentDrafts(
            @AuthenticationPrincipal UserEntity manager,
            @RequestParam("month_year") String monthYear
    ) {
        List<EmployeeDraftSummaryDTO> drafts = scheduleService.getDepartmentDraftSchedules(manager.getId(), monthYear);
        return ResponseEntity.ok(drafts);
    }

    @PostMapping("/manager/shiftSchedule/drafts/batch")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<?> updateDepartmentDrafts(
            @AuthenticationPrincipal UserEntity manager,
            @RequestBody List<ManagerDraftUpdateDTO> dtos
    ) {
        scheduleService.updateDraftScheduleBatch(dtos, manager.getId());
        return ResponseEntity.ok("Cập nhật thành công.");
    }

    @PostMapping("/manager/shiftSchedule/finalize") // cho nút "Hoàn Tất"
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<?> finalizeDepartmentSchedule(
            @AuthenticationPrincipal UserEntity manager,
            @RequestBody FinalizeScheduleDTO dto
    ) {
        scheduleService.finalizeSchedule(dto.month_year(), manager.getId());
        return ResponseEntity.ok("Lịch làm việc chính thức cho tháng " + dto.month_year() + " đã được đăng tải!.");
    }

    @GetMapping("/manager/shiftSchedule/official")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<List<EmployeeDraftSummaryDTO>> getDepartmentOfficialSchedules(
            @AuthenticationPrincipal UserEntity manager,
            @RequestParam("month_year") String monthYear
    ) {
        List<EmployeeDraftSummaryDTO> officialSchedules = scheduleService.getDepartmentOfficialSchedules(manager.getId(), monthYear);
        return ResponseEntity.ok(officialSchedules);
    }

    @PutMapping("/manager/shiftSchedule/official/batch")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<?> updateDepartmentOfficialSchedule(
            @AuthenticationPrincipal UserEntity manager,
            @RequestBody List<ManagerOfficialUpdateDTO> dtos
    ) {
        scheduleService.updateOfficialScheduleBatch(dtos, manager.getId());
        return ResponseEntity.ok("Đã cập nhật lịch chính thức.");
    }
}
