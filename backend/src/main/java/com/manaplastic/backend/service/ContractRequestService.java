package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.payroll.ContractFileHistoryDTO;
import com.manaplastic.backend.DTO.payroll.ApproveRequestDTO;
import com.manaplastic.backend.DTO.payroll.ContractChangeRequestResponseDTO;
import com.manaplastic.backend.DTO.payroll.ContractRequestDTO;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.repository.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContractRequestService {

    @Autowired
    private ContractChangeRequestRepository requestRepo;
    @Autowired
    private ContractFileHistoryRepository historyRepo;
    @Autowired
    private ContractRepository contractRepo;
    @Autowired
    private DepartmentRepository departmentRepo;
    @Autowired
    private UserRepository userRepo;
    @Value("${app.upload.contracts}")
    private String uploadDir;

    // Tạo yêu cầu sửa đổi
    public ContractchangerequestEntity createRequest(ContractRequestDTO dto, UserEntity requester) {
        ContractEntity contract = contractRepo.findById(dto.getContractId())
                .orElseThrow(() -> new RuntimeException("Hợp đồng không tồn tại"));

        String fileUrl = "";
        try {
            fileUrl = saveFile(dto.getFile());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
        }

        ContractchangerequestEntity request = new ContractchangerequestEntity();
        request.setContract(contract);
        request.setRequester(requester);
        request.setNewFileUrl(fileUrl);
        request.setReason(dto.getReason());
        request.setStatus(ContractchangerequestEntity.RequestStatus.PENDING);

        return requestRepo.save(request);
    }

    // hàm hỗ trợ helper - Luu
    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }

        String originalName = file.getOriginalFilename();
        String fileName = "contract_req_" + UUID.randomUUID().toString() + "_" + originalName;


        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }


        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


        return fileName;
    }

    // Xem danh sách yêu cầu
    public Page<ContractChangeRequestResponseDTO> getAllRequests(String keyword, Pageable pageable) {

        Specification<ContractchangerequestEntity> spec = (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String search = "%" + keyword.trim().toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();
            Join<Object, Object> requesterJoin = root.join("requester", JoinType.LEFT);
            Join<Object, Object> contractJoin = root.join("contract", JoinType.LEFT);

            predicates.add(cb.like(cb.lower(root.get("reason").as(String.class)), search));
            predicates.add(cb.like(cb.lower(root.get("status").as(String.class)), search));
            predicates.add(cb.like(cb.lower(requesterJoin.get("username")), search));
            predicates.add(cb.like(contractJoin.get("contractID").as(String.class), search));

            return cb.or(predicates.toArray(new Predicate[0]));
        };

        Page<ContractchangerequestEntity> entities = requestRepo.findAll(spec, pageable);

        return entities.map(
                this::mapToDTO);
    }

    private ContractChangeRequestResponseDTO mapToDTO(ContractchangerequestEntity entity) {
        ContractChangeRequestResponseDTO dto = new ContractChangeRequestResponseDTO();
        dto.setRequestId(entity.getId());
        dto.setReason(entity.getReason());
        dto.setNewFileUrl(entity.getNewFileUrl());
        dto.setStatus(entity.getStatus().toString());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setAdminNote(entity.getAdminNote());

        // Map thông tin Requester
        if (entity.getRequester() != null) {
            dto.setRequesterId(entity.getRequester().getId());
            dto.setRequesterName(entity.getRequester().getUsername());
        }

        // Map thông tin Admin
        if (entity.getAdmin() != null) {
            dto.setAdminName(entity.getAdmin().getUsername());
        }

        // Map thông tin Contract (Chỉ lấy ID và File cũ)
        if (entity.getContract() != null) {
            dto.setContractId(entity.getContract().getId());
            dto.setOldFileUrl(entity.getContract().getFileurl());
            dto.setContractOwnerName(entity.getContract().getUserID().getUsername());
        }

        return dto;
    }

    // Duyệt hoặc Từ chối yêu cầu (PUT)
    @Transactional
    public ContractchangerequestEntity processRequest(Integer requestId, ApproveRequestDTO dto, UserEntity approver) {
        boolean isAuthorized = false;


        // Lấy thông tin phòng HR từ DB để xem ai là Manager hiện tại
        DepartmentEntity hrDept = departmentRepo.findById(1)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng Nhân sự"));


        if (hrDept.getManagerID() != null &&
                hrDept.getManagerID().getId().equals(approver.getId())) {
            isAuthorized = true;
        }

        if (!isAuthorized) {
            throw new RuntimeException("Bạn không có quyền duyệt! Chỉ Admin hoặc Trưởng phòng Nhân sự mới được phép.");
        }

        ContractchangerequestEntity request = getRequestDetail(requestId);

        if (request.getStatus() != ContractchangerequestEntity.RequestStatus.PENDING) {
            throw new RuntimeException("Yêu cầu này đã được xử lý trước đó!");
        }

        // Cập nhật thông tin người duyệt
        request.setAdmin(approver);
        request.setReviewedAt(LocalDateTime.now());
        request.setAdminNote(dto.getAdminNote());

        if ("APPROVED".equalsIgnoreCase(dto.getStatus())) {
            request.setStatus(ContractchangerequestEntity.RequestStatus.APPROVED);

            ContractEntity contract = request.getContract();
            String oldFile = contract.getFileurl(); // Lấy file hiện tại (bị sai)

            ContractfilehistoryEntity history = new ContractfilehistoryEntity();
            history.setContract(contract);
            history.setOldFileUrl(oldFile);
            history.setReasonForChange(request.getReason());
            history.setReplacedByRequest(request);
            historyRepo.save(history);

            contract.setFileurl(request.getNewFileUrl());
            contractRepo.save(contract);

        } else {
            request.setStatus(ContractchangerequestEntity.RequestStatus.REJECTED);
        }

        return requestRepo.save(request);
    }

    public ContractchangerequestEntity getRequestDetail(Integer id) {
        return requestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tìm thấy với ID: " + id));
    }

    // Xem lịch sử version của hợp đồng
    public List<ContractFileHistoryDTO> getContractHistory(Integer contractId) {
        List<ContractfilehistoryEntity> entities = historyRepo.findByContractIdOrderByArchivedAtDesc(contractId);
        return entities.stream().map(this::mapHistoryToDTO).collect(Collectors.toList());
    }


    private ContractFileHistoryDTO mapHistoryToDTO(ContractfilehistoryEntity entity) {
        ContractFileHistoryDTO dto = new ContractFileHistoryDTO();

        dto.setHistoryId(entity.getId());
        dto.setOldFileUrl(entity.getOldFileUrl());
        dto.setArchivedAt(entity.getArchivedAt());
        dto.setReasonForChange(entity.getReasonForChange());


        if (entity.getContract() != null) {
            dto.setContractId(entity.getContract().getId());
        }

        if (entity.getReplacedByRequest() != null) {
            ContractchangerequestEntity request = entity.getReplacedByRequest();

            if (request.getRequester() != null) {
                dto.setRequestedBy(request.getRequester().getUsername());
            }

            if (request.getAdmin() != null) {
                dto.setApprovedBy(request.getAdmin().getUsername());
            }
        }

        return dto;
    }
}