package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.payroll.ContractCreateDTO;
import com.manaplastic.backend.DTO.criteria.ContractFilterCriteria;
import com.manaplastic.backend.DTO.payroll.ContractDTO;
import com.manaplastic.backend.DTO.payroll.ContractExpiringDTO;
import com.manaplastic.backend.entity.ContractEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.exportfile.ExcelHelper;
import com.manaplastic.backend.filters.ContractFilter;
import com.manaplastic.backend.repository.ContractRepository;
import com.manaplastic.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
//        UserEntity employee = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại với ID: " + request.getUserId()));
        UserEntity employee = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại với Username: " + request.getUserName()));
        // Chỉ check nếu HR đang cố tạo HĐ có thời hạn (FIXED_TERM)
        if ("FIXED_TERM".equalsIgnoreCase(request.getType())) {
            int count = contractRepository.countFixedTermContracts(employee.getId());
            if (count >= 2) {
                throw new RuntimeException("Nhân viên này đã ký đủ 2 lần HĐ xác định thời hạn. Theo luật, lần này bắt buộc phải ký HĐ Vô thời hạn (INDEFINITE)!");
            }
        }

        String fileUrl = null;
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            fileUrl = storeFile(request.getFile());
        }

        // Xử lý Hợp đồng cũ (Nếu có cái đang ACTIVE thì phải đóng lại)
        contractRepository.findByUserIdAndStatus(employee.getId(), "ACTIVE")
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
//    public List<ContractDTO> searchContracts(ContractFilterCriteria filter) {
//        Specification<ContractEntity> spec = ContractFilter.filterContracts(filter);
//        List<ContractEntity> entities = contractRepository.findAll(spec);
//        return entities.stream().map(this::mapToContractDTO).collect(Collectors.toList());
//    }
    public Page<ContractDTO> searchContracts(ContractFilterCriteria filter, Pageable pageable) {
        Specification<ContractEntity> spec = ContractFilter.filterContracts(filter);
        Page<ContractEntity> pageResult = contractRepository.findAll(spec, pageable);

        return pageResult.map(this::mapToContractDTO);
    }

    //Xuất file hdld
    public ByteArrayInputStream exportContracts(ContractFilterCriteria criteria) {
        Specification<ContractEntity> spec = ContractFilter.filterContracts(criteria);
        List<ContractEntity> entities = contractRepository.findAll(spec);

        List<ContractDTO> dtos = entities.stream()
                .map(this::mapToContractDTO)
                .collect(Collectors.toList());

        return ExcelHelper.contractsToExcel(dtos);
    }

    // Lấy ds hdld của nhân sự đó
    public List<ContractDTO> getContractsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Nhân viên không tồn tại với ID: " + userId);
        }
        List<ContractEntity> entities = contractRepository.findAllByUserId(userId);

        return entities.stream()
                .map(this::mapToContractDTO)
                .collect(Collectors.toList());
    }

    private ContractDTO mapToContractDTO(ContractEntity entity) {
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

    // Noti cho hdld sắp hết hạn
    public List<ContractExpiringDTO> getExpiringContracts(int days) {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(days);

        List<ContractEntity> contracts = contractRepository.findExpiringContracts(today, thresholdDate);

        return contracts.stream().map(contract -> {
            ContractExpiringDTO dto = new ContractExpiringDTO();
            dto.setId(contract.getId());
            dto.setContractCode(contract.getId() + "-" + contract.getContractname());
            dto.setEmployeeName(contract.getUserID().getFullname());
            dto.setEndDate(contract.getEnddate());

            // Tính số ngày còn lại: EndDate - Today
            long daysLeft = ChronoUnit.DAYS.between(today, contract.getEnddate());
            dto.setDaysRemaining(daysLeft);

            return dto;
        }).collect(Collectors.toList());
    }
}
