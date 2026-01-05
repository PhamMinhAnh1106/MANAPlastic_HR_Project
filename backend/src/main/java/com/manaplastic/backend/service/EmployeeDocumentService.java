package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.account.DocumentApprovalRequestDTO;
import com.manaplastic.backend.DTO.account.DocumentUploadRequestDTO;
import com.manaplastic.backend.DTO.account.EmployeeDocumentDTO;
import com.manaplastic.backend.entity.EmployeeDocumentEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.EmployeeDocumentRepository;
import com.manaplastic.backend.repository.UserRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeDocumentService {

    @Autowired
    private EmployeeDocumentRepository docRepo;

    @Autowired
    private UserRepository userRepo;
    @Value("${app.upload.documents}")
    private String uploadDir;

    // Upload hồ sơ mới
    public EmployeeDocumentDTO uploadDocument(DocumentUploadRequestDTO request, MultipartFile file) {
        UserEntity user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));
        String storedFileName = storeFile(file);

        EmployeeDocumentEntity doc = EmployeeDocumentEntity.builder()
                .userID(user)
                .documentType(request.getDocumentType())
                .documentName(request.getDocumentName())
                .issuingAuthority(request.getIssuingAuthority())
                .issueDate(request.getIssueDate())
                .expiryDate(request.getExpiryDate())
                .fileUrl(storedFileName)
                .status(EmployeeDocumentEntity.DocumentStatus.PENDING) // Mặc định chờ duyệt
                .note(request.getNote())
                .build();

        EmployeeDocumentEntity savedDoc = docRepo.save(doc);
        return mapToDTO(savedDoc);
    }

    // Hàm hỗ trợ lưu file vào ổ cứng
    private String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }
        try {
            // Lấy tên gốc và làm sạch path
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            // Tạo tên file mới duy nhất (UUID) để tránh trùng lặp
            // VD: avatar.png -> 550e8400-e29b...png
            String fileExtension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFilename.substring(dotIndex);
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            // Xác định đường dẫn lưu
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            // Nếu thư mục chưa tồn tại thì tạo mới
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // Copy file vào thư mục đích
            Path targetLocation = uploadPath.resolve(uniqueFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return uniqueFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    // Duyệt/Từ chối hồ sơ
    public EmployeeDocumentDTO approveDocument(Integer docId, DocumentApprovalRequestDTO request) {
        EmployeeDocumentEntity doc = docRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        doc.setStatus(request.getStatus());
        if (request.getNote() != null) {
            doc.setNote(request.getNote());
        }

        return mapToDTO(docRepo.save(doc));
    }

    public Page<EmployeeDocumentDTO> getDocumentsByUserId(Integer targetUserId, Pageable pageable, String keyword) {

        if (!userRepo.existsById(targetUserId)) {
            throw new RuntimeException("Không tìm thấy user: " + targetUserId);
        }

        Specification<EmployeeDocumentEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<EmployeeDocumentEntity, UserEntity> userJoin = root.join("userID", JoinType.LEFT);
            predicates.add(cb.equal(userJoin.get("id"), targetUserId));

            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                Predicate hasDocName = cb.like(cb.lower(root.get("documentName")), likePattern);
                Predicate hasNote = cb.like(cb.lower(root.get("note").as(String.class)), likePattern);
                Predicate hasStatus = cb.like(cb.lower(root.get("status").as(String.class)), likePattern);

                predicates.add(cb.or(hasDocName, hasNote,hasStatus));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 3. Query DB & Map sang DTO
        Page<EmployeeDocumentEntity> entities = docRepo.findAll(spec, pageable);
        return entities.map(this::mapToDTO);
    }

    public List<EmployeeDocumentDTO> getDocumentsByStatus(EmployeeDocumentEntity.DocumentStatus status) {
        List<EmployeeDocumentEntity> docs = docRepo.findByStatus(status);
        return docs.stream().map(this::mapToDTO).toList();
    }

    public Page<EmployeeDocumentDTO> getMyDocuments(Pageable pageable, String keyword) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
        Integer currentUserId = currentUser.getId();

        Specification<EmployeeDocumentEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<EmployeeDocumentEntity, UserEntity> userJoin = root.join("userID", JoinType.LEFT);

            predicates.add(cb.equal(userJoin.get("id"), currentUserId));

            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                Predicate hasDocName = cb.like(cb.lower(root.get("documentName")), likePattern);
                Predicate hasNote = cb.like(cb.lower(root.get("note").as(String.class)), likePattern);
                Predicate hasStatus = cb.like(cb.lower(root.get("status").as(String.class)), likePattern);

                predicates.add(cb.or(hasDocName, hasNote,hasStatus));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<EmployeeDocumentEntity> entities = docRepo.findAll(spec, pageable);
        return entities.map(this::mapToDTO);
    }

    // Mapper utility
    private EmployeeDocumentDTO mapToDTO(EmployeeDocumentEntity doc) {
        return EmployeeDocumentDTO.builder()
                .documentID(doc.getId())
                .employeeName(doc.getUserID().getFullname())
                .type(doc.getDocumentType().name())
                .name(doc.getDocumentName())
                .status(doc.getStatus().name())
                .fileUrl(doc.getFileUrl())
                .expiryDate(doc.getExpiryDate())
                .build();
    }
}