package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.ContractCreateDTO;
import com.manaplastic.backend.DTO.ContractFilterCriteria;
import com.manaplastic.backend.DTO.ContractDTO;
import com.manaplastic.backend.entity.ContractEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.filters.ContractFilter;
import com.manaplastic.backend.repository.ContractRepository;
import com.manaplastic.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.upload.contracts}")
    private String uploadDir;


    //Tạo hdld
    @Transactional
    public ContractEntity createContract(ContractCreateDTO request) throws IOException {
        UserEntity employee = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại với ID: " + request.getUserId()));

        // Chỉ check nếu HR đang cố tạo HĐ có thời hạn (FIXED_TERM)
        if ("FIXED_TERM".equalsIgnoreCase(request.getType())) {
            int count = contractRepository.countFixedTermContracts(request.getUserId());
            if (count >= 2) {
                throw new RuntimeException("Nhân viên này đã ký đủ 2 lần HĐ xác định thời hạn. Theo luật, lần này bắt buộc phải ký HĐ Vô thời hạn (INDEFINITE)!");
            }
        }

        String fileUrl = null;
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            fileUrl = storeFile(request.getFile());
        }

        // Xử lý Hợp đồng cũ (Nếu có cái đang ACTIVE thì phải đóng lại)
        contractRepository.findByUserIdAndStatus(request.getUserId(), "ACTIVE")
                .ifPresent(oldContract -> {
                    oldContract.setStatus("HISTORY");
                    contractRepository.save(oldContract);
                });

        ContractEntity newContract = new ContractEntity();
        newContract.setUserID(employee);
        newContract.setContractname(request.getContractName());
        newContract.setType(request.getType());
        newContract.setBasesalary(request.getBaseSalary());
        newContract.setInsuranceSalary(request.getInsuranceSalary());
        newContract.setAllowanceToxicType(request.getAllowanceToxicType());
        newContract.setSigndate(request.getSignDate());
        newContract.setStartdate(request.getStartDate());
        newContract.setEnddate(request.getEndDate());
        newContract.setFileurl(fileUrl); // Lưu đường dẫn file vào DB
        newContract.setStatus("ACTIVE");

        return contractRepository.save(newContract);
    }

    public boolean checkIfFixedTermAllowed(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Nhân viên không tồn tại với ID: " + userId);
        }
        int count = contractRepository.countFixedTermContracts(userId);
        return count < 2;
    }

    //Lưu PDF
    private String storeFile(MultipartFile file) throws IOException {
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        // Tạo thư mục nếu chưa có
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Copy file vào thư mục
        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/" + uploadDir + "/" + newFileName;
    }

    //Lọc
    public List<ContractDTO> searchContracts(ContractFilterCriteria filter) {
        Specification<ContractEntity> spec = ContractFilter.filterContracts(filter);
        List<ContractEntity> entities = contractRepository.findAll(spec);

        return entities.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Lấy ds hdld của nhân sự đó
    public List<ContractDTO> getContractsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Nhân viên không tồn tại với ID: " + userId);
        }
        List<ContractEntity> entities = contractRepository.findAllByUserId(userId);

        return entities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ContractDTO mapToDTO(ContractEntity entity) {
        String username = (entity.getUserID() != null) ? entity.getUserID().getUsername() : null;
        return new ContractDTO(
                entity.getId(),
                entity.getContractname(),
                entity.getType(),
                entity.getBasesalary(),
                entity.getInsuranceSalary(),
                entity.getAllowanceToxicType(),
                entity.getFileurl(),
                entity.getSigndate(),
                entity.getStartdate(),
                entity.getEnddate(),
                entity.getStatus(),
                username
        );
    }
}
