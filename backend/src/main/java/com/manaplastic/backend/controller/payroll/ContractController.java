package com.manaplastic.backend.controller.payroll;

import com.manaplastic.backend.DTO.payroll.ContractCreateDTO;
import com.manaplastic.backend.DTO.criteria.ContractFilterCriteria;
import com.manaplastic.backend.DTO.payroll.ContractDTO;
import com.manaplastic.backend.DTO.payroll.ContractExpiringDTO;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hr/contracts")
@PreAuthorize("hasAuthority('HR')")
@CrossOrigin(origins = "*") // Cho phép Frontend gọi API
public class ContractController {

    @Autowired
    private ContractService contractService;

    // Kiểm tra nhân viên này đã ký bao nhiêu HĐ có thời hạn rồi
    @GetMapping("/checkRenewal/{userId}")
    @RequiredPermission(PermissionConst.CONTRACT_VIEW)
    public ResponseEntity<?> checkRenewalStatus(@PathVariable Integer userId) {
        try {
            boolean allowFixedTerm = contractService.checkIfFixedTermAllowed(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("allowFixedTerm", allowFixedTerm);
            if (!allowFixedTerm) {
                response.put("message", "Nhân viên đã ký đủ 02 HĐ có thời hạn. Bắt buộc ký HĐ Vô thời hạn.");
            } else {
                response.put("message", "Đủ điều kiện ký HĐ có thời hạn.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // Tạo hợp đồng mới (Kèm upload file)
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @LogActivity(action = "CREATE_CONTRACT",description = "Tạo mới hợp đồng lao động")
    @RequiredPermission(PermissionConst.CONTRACT_CREATE)
    public ResponseEntity<?> createContract(@ModelAttribute ContractCreateDTO contractDTO) {
        try {
            if (contractDTO.getFile() == null || contractDTO.getFile().isEmpty()) {
                return ResponseEntity.badRequest().body("Vui lòng đính kèm file scan hợp đồng! (file PDF)");
            }

            contractService.createContract(contractDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tạo hợp đồng mới thành công!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi nghiệp vụ: " + e.getMessage()); // Bắt lỗi neeus HR cố tình thêm hdld CÓ THOỜI HẠN quá 2 lần
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // Lọc
//    @GetMapping("/contractFilter")
//    public ResponseEntity<?> getContracts(@ModelAttribute ContractFilterCriteria filter) {

    /// /            List<ContractFilterResponse> contracts = contractService.searchContracts(filter);
//            return ResponseEntity.ok(contractService.searchContracts(filter));
//    }

    @GetMapping("/contractFilter")
    @RequiredPermission(PermissionConst.CONTRACT_VIEW)
    public ResponseEntity<Page<ContractDTO>> getContracts(
            @ModelAttribute ContractFilterCriteria filter,
            @PageableDefault(page = 0,size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ContractDTO> result = contractService.searchContracts(filter, pageable);
        return ResponseEntity.ok(result);
    }

    //Lấy ds hdld của nhân sự này
    @GetMapping("/user/{userId}")
    @RequiredPermission(PermissionConst.CONTRACT_VIEW)
    public ResponseEntity<List<ContractDTO>> getContractsByEmployee(@PathVariable Integer userId) {
        return ResponseEntity.ok(contractService.getContractsByUserId(userId));
    }

    // Noti
    @GetMapping("/expiringNoti")
    @RequiredPermission(PermissionConst.CONTRACT_VIEW)
    public ResponseEntity<List<ContractExpiringDTO>> getExpiringContractsForNotification() {
        // Mặc định quét trước 30 ngày
        List<ContractExpiringDTO> list = contractService.getExpiringContracts(30);
        return ResponseEntity.ok(list);
    }
}
