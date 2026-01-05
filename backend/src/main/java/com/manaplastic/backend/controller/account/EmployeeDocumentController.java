package com.manaplastic.backend.controller.account;

import com.manaplastic.backend.DTO.account.DocumentApprovalRequestDTO;
import com.manaplastic.backend.DTO.account.DocumentUploadRequestDTO;
import com.manaplastic.backend.DTO.account.EmployeeDocumentDTO;
import com.manaplastic.backend.constant.customAnotation.LogActivity;
import com.manaplastic.backend.entity.EmployeeDocumentEntity;
import com.manaplastic.backend.service.EmployeeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/user/documents")
public class EmployeeDocumentController {

    @Autowired
    private EmployeeDocumentService docService;
    @Value("${app.upload.documents}")
    private String uploadDir;
    //upload hồ sơ
    @PostMapping(value = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("isAuthenticated()")
    @LogActivity(action = "UPLOAD_DOCUMENT", description = "Đăng tải tài liệu, hồ sơ")
    public ResponseEntity<EmployeeDocumentDTO> uploadDocument(
            @ModelAttribute DocumentUploadRequestDTO request,
            @RequestParam("file") MultipartFile file
    ) {
        EmployeeDocumentDTO result = docService.uploadDocument(request, file);
        return ResponseEntity.ok(result);
    }

    //duyệt/ từ chối (đổi trạng thái thôi)
    @PutMapping("/{docId}/status")
    @PreAuthorize("hasAnyAuthority('HR', 'Admin')")
    @LogActivity(action = "APPROVED_DOCUMENT", description = "Duyệt/ Từ chối tài liệu, hồ sơ")
    public ResponseEntity<EmployeeDocumentDTO> updateStatus(
            @PathVariable Integer docId,
            @RequestBody DocumentApprovalRequestDTO request) {
        EmployeeDocumentDTO result = docService.approveDocument(docId, request);
        return ResponseEntity.ok(result);
    }

    //  Xem list hồ sơ của nhân viên đó
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('HR','Admin')")
    public ResponseEntity<Page<EmployeeDocumentDTO>> getDocumentsByUser(
            @PathVariable Integer userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword
    ) {
        Page<EmployeeDocumentDTO> result = docService.getDocumentsByUserId(userId, pageable, keyword);
        return ResponseEntity.ok(result);
    }

    // Xem list hồ sơ bản thân
    @GetMapping("/myDocuments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<EmployeeDocumentDTO>> getMyDocuments(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword
    ) {
        Page<EmployeeDocumentDTO> result = docService.getMyDocuments(pageable, keyword);
        return ResponseEntity.ok(result);
    }

    //xem ds duyệt
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('HR', 'Admin')")
    public ResponseEntity<List<EmployeeDocumentDTO>> getDocumentsByStatus(
            @RequestParam(defaultValue = "PENDING") EmployeeDocumentEntity.DocumentStatus status
    ) {
        return ResponseEntity.ok(docService.getDocumentsByStatus(status));
    }

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
//    @GetMapping("/check-benefit")
//    public ResponseEntity<Boolean> checkBenefit(
//            @RequestParam Integer userId,
//            @RequestParam EmployeeDocumentEntity.DocumentType type) {
//        boolean isEligible = docService.isEligibleForBenefit(userId, type);
//        return ResponseEntity.ok(isEligible);
//    }
}