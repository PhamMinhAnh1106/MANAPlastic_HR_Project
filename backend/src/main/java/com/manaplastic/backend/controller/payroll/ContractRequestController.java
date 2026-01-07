package com.manaplastic.backend.controller.payroll;

import com.manaplastic.backend.DTO.payroll.ContractFileHistoryDTO;
import com.manaplastic.backend.DTO.payroll.ApproveRequestDTO;
import com.manaplastic.backend.DTO.payroll.ContractChangeRequestResponseDTO;
import com.manaplastic.backend.DTO.payroll.ContractRequestDTO;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.service.ContractRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/contractRequests")
@CrossOrigin("*")
public class ContractRequestController {

    @Autowired
    private ContractRequestService service;

    // tạo đơn
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @LogActivity(action = "CREATE_CONTRACT_REQ", description = "Tạo mới yêu cầu sửa đổi file hợp đồng.")
    public ResponseEntity<?> createRequest(@ModelAttribute ContractRequestDTO dto,
                                           @AuthenticationPrincipal UserEntity currentUser) {
        service.createRequest(dto, currentUser);
        return ResponseEntity.ok("Đã gửi yêu cầu thành công!");
    }

    // Xem danh sách chi tiết
    @GetMapping
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    public ResponseEntity<Page<ContractChangeRequestResponseDTO>> getAllRequests(
            @RequestParam(value = "search", required = false, defaultValue = "") String keyword,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllRequests(keyword, pageable));
    }


    //Duyệt từ chối
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    @LogActivity(action = "APPROVED_CONTRACT_REQ", description = "Xử lý duyệt/từ chối yêu cầu sửa đổi file hợp đồng.")
    public ResponseEntity<?> processRequest(
            @PathVariable Integer id,
            @RequestBody ApproveRequestDTO dto,
            @AuthenticationPrincipal UserEntity currentUser) {
        service.processRequest(id, dto, currentUser);
        return ResponseEntity.ok("Đã xử lý cho đơn " + id + " thành công");
    }

    // xem lịch sử version sửa đổi
    @GetMapping("/history/contract/{contractId}")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    public ResponseEntity<List<ContractFileHistoryDTO>> getHistoryByContract(@PathVariable Integer contractId) {
        return ResponseEntity.ok(service.getContractHistory(contractId));
    }
}
