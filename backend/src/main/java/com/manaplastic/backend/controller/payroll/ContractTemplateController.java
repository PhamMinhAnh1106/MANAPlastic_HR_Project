package com.manaplastic.backend.controller.payroll;

import com.manaplastic.backend.entity.ContractTemplateEntity;
import com.manaplastic.backend.repository.ContractTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/hr/contract/templates")
@PreAuthorize("hasAnyAuthority('HR','Admin')")
@CrossOrigin(origins = "*")
public class ContractTemplateController {

    @Autowired
    private ContractTemplateRepository templateRepository;

    // Lấy danh sách tất cả các mẫu (Để hiển thị lên Dropdown)
    @GetMapping
    public List<ContractTemplateEntity> getAllTemplates() {
        return templateRepository.findAll();
    }

    // Lấy chi tiết nội dung HTML của 1 mẫu (Khi chọn Dropdown)
    @GetMapping("/{id}")
    public ResponseEntity<ContractTemplateEntity> getTemplateById(@PathVariable Integer id) {
        return templateRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Cập nhật nội dung mẫu
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTemplate(@PathVariable Integer id, @RequestBody ContractTemplateEntity request) {
        return templateRepository.findById(id).map(template -> {
            // Cập nhật các trường
            template.setName(request.getName());
            template.setContent(request.getContent()); // HTML từ CKEditor
            template.setType(request.getType());

            templateRepository.save(template);
            return ResponseEntity.ok().body("Cập nhật mẫu hợp đồng thành công!");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Tạo mới mẫu
    @PostMapping
    public ContractTemplateEntity createTemplate(@RequestBody ContractTemplateEntity newTemplate) {
        newTemplate.setCreatedAt(Instant.now());
        return templateRepository.save(newTemplate);
    }
}