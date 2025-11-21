package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.ContractCreateDTO;
import com.manaplastic.backend.DTO.ContractFilterCriteria;
import com.manaplastic.backend.DTO.ContractDTO;
import com.manaplastic.backend.entity.ContractEntity;
import com.manaplastic.backend.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> createContract(@ModelAttribute ContractCreateDTO contractDTO) {
        try {
            if (contractDTO.getFile() == null || contractDTO.getFile().isEmpty()) {
                return ResponseEntity.badRequest().body("Vui lòng đính kèm file scan hợp đồng! (file PDF)");
            }

            ContractEntity newContract = contractService.createContract(contractDTO);

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
    @GetMapping("/contractFilter")
    public ResponseEntity<?> getContracts(@ModelAttribute ContractFilterCriteria filter) {
//            List<ContractFilterResponse> contracts = contractService.searchContracts(filter);
            return ResponseEntity.ok(contractService.searchContracts(filter));
    }

    //Lấy ds hdld của nhân sự này
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ContractDTO>> getContractsByEmployee(@PathVariable Integer userId) {
        return ResponseEntity.ok(contractService.getContractsByUserId(userId));
    }
}
