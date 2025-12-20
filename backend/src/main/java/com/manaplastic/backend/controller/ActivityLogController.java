package com.manaplastic.backend.controller;


import com.manaplastic.backend.entity.ActivitylogEntity;
import com.manaplastic.backend.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<ActivitylogEntity>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {

        Page<ActivitylogEntity> result = logService.getLogs(keyword, page, size);
        return ResponseEntity.ok(result);
    }
}