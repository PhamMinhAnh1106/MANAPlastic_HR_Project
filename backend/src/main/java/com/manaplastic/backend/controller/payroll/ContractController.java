package com.manaplastic.backend.controller.payroll;

import com.manaplastic.backend.DTO.payroll.ContractCreateDTO;
import com.manaplastic.backend.DTO.criteria.ContractFilterCriteria;
import com.manaplastic.backend.DTO.payroll.ContractDTO;
import com.manaplastic.backend.DTO.payroll.ContractExpiringDTO;
import com.manaplastic.backend.DTO.payroll.ContractUpdateDTO;
import com.manaplastic.backend.constant.LogType;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.constant.customAnotation.RequiredPermission;
import com.manaplastic.backend.constant.permission.PermissionConst;
import com.manaplastic.backend.entity.ContractEntity;
import com.manaplastic.backend.service.ContractService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hr/contracts")
@PreAuthorize("hasAnyAuthority('HR','Admin')")
@CrossOrigin(origins = "*") // Cho phép Frontend gọi API
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Value("${app.upload.contracts}")
    private String uploadDir;

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

    // Xem file hdld
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            //Tạo đường dẫn trỏ đến file thực tế
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {

                String contentType = "application/octet-stream";
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException ex) {
                    // ignore
                }
                if (contentType == null) {
                    contentType = "application/pdf";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    @LogActivity(action = "CREATE_CONTRACT", description = "Tạo mới hợp đồng lao động")
    @RequiredPermission(PermissionConst.CONTRACT_CREATE)
    public ResponseEntity<?> createContract(@RequestBody ContractCreateDTO request) {

        ContractEntity newContract = contractService.createContractDraft(request);

        // Trả về ID cho Frontend dùng
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tạo nháp thành công!");
        response.put("contractId", newContract.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/print")
    public ResponseEntity<byte[]> printContract(@PathVariable Integer id) {
        try {
            byte[] pdfContent = contractService.generateContractPdf(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=hop_dong_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
//    // Tạo hợp đồng mới (Kèm upload file)
//    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @LogActivity(action = "CREATE_CONTRACT", description = "Tạo mới hợp đồng lao động")
//    @RequiredPermission(PermissionConst.CONTRACT_CREATE)
//    public ResponseEntity<?> createContract(@ModelAttribute ContractCreateDTO contractDTO) {
//        try {
//            if (contractDTO.getFile() == null || contractDTO.getFile().isEmpty()) {
//                return ResponseEntity.badRequest().body("Vui lòng đính kèm file scan hợp đồng! (file PDF)");
//            }
//
//            contractService.createContract(contractDTO);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "Tạo hợp đồng mới thành công!");
//            return ResponseEntity.ok(response);
//
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body("Lỗi nghiệp vụ: " + e.getMessage()); // Bắt lỗi neeus HR cố tình thêm hdld CÓ THOỜI HẠN quá 2 lần
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
//        }
//    }

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
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
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

    //Sửa
    @PutMapping(value = "/{id}")
    @RequiredPermission(PermissionConst.CONTRACT_UPDATE)
    @LogActivity(action = "UPDATE_CONTRACT", description = "Cập nhật thông tin hợp đồng nháp")
    public ResponseEntity<?> updateContract(
            @PathVariable int id,
            @RequestBody ContractUpdateDTO request) throws IOException {

        contractService.updateContractFull(id, request);
        return ResponseEntity.ok("Đã cập nhật dữ liệu thành công!");

    }
}
