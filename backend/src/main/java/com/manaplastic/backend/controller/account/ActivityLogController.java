package com.manaplastic.backend.controller.account;


import com.manaplastic.backend.DTO.account.ActivityLogDTO;
import com.manaplastic.backend.entity.ActivitylogEntity;
import com.manaplastic.backend.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/activityLogs")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAuthority('Admin')")
public class ActivityLogController {

    @Autowired
    private ActivityLogService logService;

    @GetMapping
    public ResponseEntity<Page<ActivityLogDTO>> getLogs(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
            @PageableDefault(page = 0, size = 10, sort ="actiontime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword
    ) {

        Page<ActivityLogDTO> result = logService.getLogs(pageable,keyword);
        return ResponseEntity.ok(result);
    }
}