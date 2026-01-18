package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.payroll.*;
import com.manaplastic.backend.DTO.criteria.ContractFilterCriteria;
import com.manaplastic.backend.entity.ContractEntity;
import com.manaplastic.backend.entity.ContractTemplateEntity;
import com.manaplastic.backend.entity.ContractallowanceEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.exportfile.ExcelHelper;
import com.manaplastic.backend.filters.ContractFilter;
import com.manaplastic.backend.repository.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${app.upload.contracts}")
    private String uploadDir;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ContractAllowancesRepository contractAllowancesRepository;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private ContractTemplateRepository templateRepository;


    //Tạo hdld
//    @Transactional
//    public ContractEntity createContract(ContractCreateDTO request) throws IOException {
////        UserEntity employee = userRepository.findById(request.getUserId())
////                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại với ID: " + request.getUserId()));
//        UserEntity employee = userRepository.findByUsername(request.getUserName())
//                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại với Username: " + request.getUserName()));
//        // Chỉ check nếu HR đang cố tạo HĐ có thời hạn (FIXED_TERM)
//        if ("FIXED_TERM".equalsIgnoreCase(request.getType())) { // Có thời hạn - luật Việt Nam Bộ luật Lao động 2019 (Điều 20)
//            int count = contractRepository.countFixedTermContracts(employee.getId());
//            if (count >= 2) {
//                throw new RuntimeException("Nhân viên này đã ký đủ 2 lần HĐ xác định thời hạn. Theo luật, lần này bắt buộc phải ký HĐ Vô thời hạn (INDEFINITE)!");
//            }
//
//            if (request.getStartDate() != null && request.getEndDate() != null) {
//                long months = calculateMonths(request.getStartDate(), request.getEndDate());
//
//                if (request.getEndDate().isBefore(request.getStartDate())) {
//                    throw new RuntimeException("Ngày kết thúc hợp đồng không được nhỏ hơn ngày bắt đầu.");
//                }
//                if (months > 36) {
//                    throw new RuntimeException("Theo luật, Hợp đồng xác định thời hạn không được vượt quá 36 tháng.");
//                }
//            } else {
//                throw new RuntimeException("Hợp đồng xác định thời hạn bắt buộc phải có ngày bắt đầu và ngày kết thúc.");
//            }
//        }
//        else {// Vô thời hạn
//            if (request.getStartDate() == null) {
//                throw new RuntimeException("Hợp đồng vô thời hạn phải có ngày bắt đầu.");
//            }
//            request.setEndDate(null);
//        }
//
//        String fileUrl = null;
//        if (request.getFile() != null && !request.getFile().isEmpty()) {
//            fileUrl = storeFile(request.getFile());
//        }
//
//        Optional<ContractEntity> activeContractOpt = contractRepository.findByUserIdAndStatus(employee.getId(), "ACTIVE");
//
//        if (activeContractOpt.isPresent()) {
//            ContractEntity oldContract = activeContractOpt.get();
//
//            if (request.getStartDate().isBefore(oldContract.getEnddate()) || request.getStartDate().isEqual(oldContract.getEnddate())) {
//                throw new RuntimeException("Hợp đồng cũ vẫn còn hiệu lực đến ngày " + oldContract.getEnddate() +
//                        ". Ngày bắt đầu hợp đồng mới phải sau ngày này.");
//            }
//
//            oldContract.setStatus("HISTORY");
//            contractRepository.save(oldContract);
//        }
//

    /// /        // Xử lý Hợp đồng cũ (Nếu có cái đang ACTIVE thì phải đóng lại)
    /// /        contractRepository.findByUserIdAndStatus(employee.getId(), "ACTIVE")
    /// /                .ifPresent(oldContract -> {oldContract.setStatus("HISTORY"); contractRepository.save(oldContract); });
//
//        ContractEntity newContract = new ContractEntity();
//        newContract.setUserID(employee);
//        newContract.setContractname(request.getContractName());
//        newContract.setType(request.getType());
//        newContract.setBasesalary(request.getBaseSalary());
//        newContract.setInsuranceSalary(request.getInsuranceSalary());
//        newContract.setAllowanceToxicType(request.getAllowanceToxicType());
//        newContract.setSigndate(request.getSignDate());
//        newContract.setStartdate(request.getStartDate());
//        newContract.setEnddate(request.getEndDate());
//        newContract.setFileurl(fileUrl); // Lưu đường dẫn file vào DB
//        newContract.setStatus("ACTIVE");
//
//        return contractRepository.save(newContract);
//    }
    @Transactional(rollbackFor = Exception.class)
    public ContractEntity createContractDraft(ContractCreateDTO request) {
        UserEntity employee;
        // Phần tài khoản========================================================================================

        if (request.getUserId() == null) {
            // NV mới
            if (userRepository.existsByCccd(request.getCccd())) {
                throw new RuntimeException("CCCD " + request.getCccd() + " đã tồn tại.");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email " + request.getEmail() + " đã được sử dụng.");
            }

            employee = new UserEntity();
            employee.setFullname(request.getFullname());
            employee.setCccd(request.getCccd());
            employee.setEmail(request.getEmail());
            employee.setPhonenumber(request.getPhone());
            employee.setAddress(request.getAddress());
            employee.setBirth(request.getDob());
            employee.setGender(request.getGender());

            if (request.getDepartmentId() != null) {
                employee.setDepartmentID(departmentRepository.findById(request.getDepartmentId().intValue()).orElse(null));
            }
            if (request.getRoleId() != null) {
                employee.setRoleID(roleRepository.findById(request.getRoleId().intValue()).orElse(null));
            }

            employee.setStatus("inactive");
            employee.setJobtype("NORMAL");
            employee.setSkillGrade(1);
            employee.setUsername(null);
            employee.setPassword(null);

            employee = userRepository.save(employee);

        } else {
            employee = userRepository.findById(request.getUserId().intValue())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên ID: " + request.getUserId()));

            validateLaborLawForRenewal(employee, request);
            validateContractDates(employee, request);

            if (request.getDepartmentId() != null)
                employee.setDepartmentID(departmentRepository.findById(request.getDepartmentId().intValue()).orElse(null));
            if (request.getRoleId() != null)
                employee.setRoleID(roleRepository.findById(request.getRoleId().intValue()).orElse(null));

            employee = userRepository.save(employee);
        }

        // Phần hợp đồng================================================================================================

        ContractEntity contract = new ContractEntity();
        contract.setUserID(employee);

        contract.setType(request.getContractType()); // xác định time hay không
//        contract.setWorkType(request.getWorkType() != null ? request.getWorkType() : "FULLTIME"); //Full hay Part

        if (request.getWorkType() == null || request.getWorkType().trim().isEmpty()) {
            throw new RuntimeException("Loại hình làm việc là bắt buộc, không được để trống.");
        }

        String normalizedWorkType = request.getWorkType().toUpperCase();
        if (!"FULLTIME".equals(normalizedWorkType) && !"PART_TIME".equals(normalizedWorkType)) {
            throw new RuntimeException("Loại hình làm việc không hợp lệ. Chỉ chấp nhận 'FULLTIME' hoặc 'PART_TIME'.");
        }

        contract.setWorkType(normalizedWorkType);
        contract.setStartdate(request.getStartDate());
        if ("INDEFINITE".equalsIgnoreCase(request.getContractType())) {
            contract.setEnddate(null);
        } else {
            contract.setEnddate(request.getEndDate());
        }

        contract.setBasesalary(request.getBaseSalary());
//        Double insurancePercent = request.getInsurancePercent() != null ? request.getInsurancePercent() : 100.0; // %lương
        Double insurancePercent;
        if (request.getInsurancePercent() != null) {
            insurancePercent = request.getInsurancePercent();
        } else {
            if ("PART_TIME".equalsIgnoreCase(request.getWorkType())) {
                insurancePercent = 0.0; //%
            } else {
                insurancePercent = 100.0; //%
            }
        }
        contract.setInsurancePercent(BigDecimal.valueOf(insurancePercent));

        // Tính InsuranceSalary = BaseSalary * Percent / 100
        BigDecimal insSalary = request.getBaseSalary()
                .multiply(BigDecimal.valueOf(insurancePercent))
                .divide(BigDecimal.valueOf(100));

        contract.setInsuranceSalary(insSalary);
        contract.setStandardHours(request.getStandardHours());

        String typeNameVN = "FIXED_TERM".equalsIgnoreCase(request.getContractType()) ? "Xác định thời hạn" : "Vô thời hạn";
        contract.setContractname("Hợp đồng lao động " + typeNameVN + " - " + employee.getFullname());
        contract.setSigndate(LocalDate.now());

        // vị trí làm việc
        if (request.getDepartmentId() != null)
            contract.setDepartment(departmentRepository.findById(request.getDepartmentId().intValue()).orElse(null));
        if (request.getRoleId() != null)
            contract.setRole(roleRepository.findById(request.getRoleId().intValue()).orElse(null));

        contract.setStatus("DRAFT");
        contract.setContractCode(generateContractCode(request.getContractType(), employee.getId()));

        if (request.getTemplateId() != null) {
            // Nếu người dùng chọn mẫu cụ thể
            ContractTemplateEntity template = templateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new RuntimeException("Mẫu hợp đồng không tồn tại!"));
            contract.setContractTemplate(template);
        } else {
            // Nếu không chọn, tự động lấy mẫu ACTIVE đầu tiên theo loại hợp đồng
            templateRepository.findFirstByTypeAndIsActiveTrue(request.getContractType())
                    .ifPresent(contract::setContractTemplate);
        }
        ContractEntity savedContract = contractRepository.save(contract);

        //Phần phụ cấp (allowance contracts)================================================================================================
        if (request.getAllowances() != null && !request.getAllowances().isEmpty()) {
            List<ContractallowanceEntity> allowanceList = new ArrayList<>();
            for (ContractsAllowanceDTO dto : request.getAllowances()) {
                ContractallowanceEntity item = new ContractallowanceEntity();
                item.setContractID(savedContract);
                item.setAllowanceName(dto.getAllowanceName());
                item.setAllowanceType(dto.getAllowanceType());
                item.setAmount(dto.getAmount());
                item.setIsTaxable(Boolean.TRUE.equals(dto.getIsTaxable()));
                item.setIsInsuranceBase(Boolean.TRUE.equals(dto.getIsInsuranceBase()));
                item.setTaxFreeAmount(dto.getTaxFreeAmount() != null ? dto.getTaxFreeAmount() : BigDecimal.ZERO);
                allowanceList.add(item);
            }
            contractAllowancesRepository.saveAll(allowanceList);
        }

        return savedContract;
    }

    //  Các hàm hỗ trợ helper

    private void validateLaborLawForRenewal(UserEntity employee, ContractCreateDTO request) {
        if ("FIXED_TERM".equalsIgnoreCase(request.getContractType())) {
            int count = contractRepository.countFixedTermContracts(employee.getId());
            if (count >= 2) {
                throw new RuntimeException("Nhân viên đã ký 2 HĐ xác định thời hạn. Lần này bắt buộc phải là Vô thời hạn (INDEFINITE).");
            }
        }
    }

    private void validateContractDates(UserEntity employee, ContractCreateDTO request) {
        Optional<ContractEntity> activeContractOpt = contractRepository.findActiveContractByUserId(employee);
        if (activeContractOpt.isPresent()) {
            ContractEntity oldContract = activeContractOpt.get();
            // Ngày bắt đầu HĐ mới không được đè lên HĐ cũ
            if (request.getStartDate().isBefore(oldContract.getEnddate()) || request.getStartDate().isEqual(oldContract.getEnddate())) {
                throw new RuntimeException("Hợp đồng cũ còn hiệu lực đến " + oldContract.getEnddate() + ". Ngày bắt đầu mới phải sau ngày này.");
            }
        }
    }


    private String generateContractCode(String type, Integer userId) {
        String prefix = "FIXED_TERM".equalsIgnoreCase(type) ? "HDXD" : "HDKXD";
        int year = LocalDate.now().getYear();

        String baseCode = String.format("%s-%d-%d", prefix, year, userId);

        String finalCode = baseCode; // trường hợp ký lần 2-3
        int count = 1;

        while (contractRepository.existsByContractCode(finalCode)) {
            finalCode = baseCode + "-" + count;
            count++;
        }

        return finalCode;
    }


    public boolean checkIfFixedTermAllowed(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Nhân viên không tồn tại với ID: " + userId);
        }
        int count = contractRepository.countFixedTermContracts(userId);
        return count < 2;
    }

    private long calculateMonths(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return ChronoUnit.MONTHS.between(startDate, endDate);
    }

    public byte[] generateContractPdf(Integer contractId) {
        ContractEntity contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Hợp đồng ID: " + contractId));
        UserEntity employee = contract.getUserID();

        // Lấy HTML Template
        String htmlContent = "";
        if (contract.getContractTemplate() != null) {
            htmlContent = contract.getContractTemplate().getContent();
        } else {
            ContractTemplateEntity defaultTemplate = templateRepository.findFirstByTypeAndIsActiveTrue(contract.getType())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu hợp đồng phù hợp cho loại: " + contract.getType()));
            htmlContent = defaultTemplate.getContent();
        }

        // định dạng dữ liệu
        NumberFormat vnMoney = NumberFormat.getInstance(new Locale("vi", "VN"));
        DateTimeFormatter vnDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate now = LocalDate.now();
        LocalDate signDate = contract.getSigndate() != null ? contract.getSigndate() : now;

        // MAPPING DATA (Phải khớp chính xác với biến trong CKEditor ở HTML)
        Map<String, String> data = new HashMap<>();

        // -- Thông tin chung --
        data.put("{{contract_code}}", contract.getContractCode());
        data.put("{{day}}", String.valueOf(signDate.getDayOfMonth()));
        data.put("{{month}}", String.valueOf(signDate.getMonthValue()));
        data.put("{{year}}", String.valueOf(signDate.getYear()));

        // -- Thông tin nhân sự --
        data.put("{{employee_name}}", employee.getFullname() != null ? employee.getFullname().toUpperCase() : "...");
        data.put("{{dob}}", employee.getBirth() != null ? employee.getBirth().format(vnDate) : "...");
        data.put("{{cccd}}", employee.getCccd() != null ? employee.getCccd() : "...");
        data.put("{{address}}", employee.getAddress() != null ? employee.getAddress() : "...");

        // -- Vị trí & Lương --
        data.put("{{department}}", contract.getDepartment() != null ? contract.getDepartment().getDepartmentname() : (employee.getDepartmentID() != null ? employee.getDepartmentID().getDepartmentname() : "..."));
        data.put("{{position}}", contract.getRole() != null ? contract.getRole().getRolename() : (employee.getRoleID() != null ? employee.getRoleID().getRolename() : "..."));

        String typeVN = "FIXED_TERM".equalsIgnoreCase(contract.getType()) ? "Xác định thời hạn" : "Vô thời hạn";
        data.put("{{contract_type}}", typeVN);

        String workTypeVN = "PART_TIME".equalsIgnoreCase(contract.getWorkType()) ? "Bán thời gian" : "Toàn thời gian";
        data.put("{{work_type}}", workTypeVN);

        data.put("{{start_date}}", contract.getStartdate().format(vnDate));

        if (contract.getEnddate() != null) {
            data.put("{{end_date}}", contract.getEnddate().format(vnDate));
        } else {
            // Nếu là HĐ Vô thời hạn (enddate = null) -> Hiển thị text phù hợp hoặc để trống
            data.put("{{end_date}}", "...");
        }
        data.put("{{base_salary}}", vnMoney.format(contract.getBasesalary()));

        BigDecimal insSalary = contract.getInsuranceSalary() != null ? contract.getInsuranceSalary() : contract.getBasesalary();
        data.put("{{insurance_salary}}", vnMoney.format(insSalary));

        Number percent = contract.getInsurancePercent() != null ? contract.getInsurancePercent() : 100.0;
        data.put("{{insurance_percent}}", String.valueOf(percent));

        String stdHours = contract.getStandardHours() != null ? String.valueOf(contract.getStandardHours()) : "...";
        data.put("{{standard_hours}}", stdHours);
        // -- Bảng Phụ cấp --
        String tableHtml = generateAllowanceTableHtml(contract);
        // Lưu ý: CKEditor có thể lưu biến trong thẻ span, ví dụ <span style="...">{{allowances_table}}</span>

        // THỰC HIỆN REPLACE
        // Biến table xử lý riêng để tránh loop
        htmlContent = htmlContent.replace("{{allowances_table}}", tableHtml);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String val = entry.getValue() != null ? entry.getValue() : "";
            htmlContent = htmlContent.replace(entry.getKey(), val);
        }
        Document doc = Jsoup.parse(htmlContent);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // Bắt buộc xuất ra XML/XHTML chuẩn
        doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml); // Encode ký tự lạ
        doc.charset(java.nio.charset.StandardCharsets.UTF_8);

        String css = "body { font-family: 'ArialCustom', sans-serif; } table { border-collapse: collapse; }";
        doc.head().appendElement("style").text(css);

        // Lấy chuỗi HTML đã được clean
        String xhtml = doc.html();

        // Render PDF
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            // Load Font (Giữ nguyên)
            try {
                File fontFile = new ClassPathResource("fonts/arial.ttf").getFile();
                builder.useFont(fontFile, "ArialCustom");
            } catch (Exception e) {
                // Log warning
            }

            // Dùng chuỗi xhtml đã chuẩn hóa thay vì htmlContent thô
            builder.withHtmlContent(xhtml, null);

            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi render PDF: " + e.getMessage());
        }
    }

    // Hàm phụ sinh HTML bảng phụ cấp
    private String generateAllowanceTableHtml(ContractEntity contract) {
        List<ContractallowanceEntity> list = contractAllowancesRepository.findByContractID(contract);
        if (list == null || list.isEmpty()) {
            return "<p><i>(Không có phụ cấp)</i></p>";
        }

        StringBuilder sb = new StringBuilder();
        // Style inline cứng cho table để đảm bảo PDF render đúng khung
        sb.append("<table style='width: 100%; border-collapse: collapse; border: 1px solid black; margin-top: 10px;'>");
        sb.append("<thead>");
        sb.append("<tr style='background-color: #f2f2f2;'>");
        sb.append("<th style='border: 1px solid black; padding: 8px; text-align: center; width: 50px;'>STT</th>");
        sb.append("<th style='border: 1px solid black; padding: 8px; text-align: left;'>Tên phụ cấp</th>");
        sb.append("<th style='border: 1px solid black; padding: 8px; text-align: right; width: 150px;'>Số tiền (VNĐ)</th>");
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");

        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
        int i = 1;
        for (ContractallowanceEntity item : list) {
            sb.append("<tr>");
            sb.append("<td style='border: 1px solid black; padding: 8px; text-align: center;'>").append(i++).append("</td>");
            sb.append("<td style='border: 1px solid black; padding: 8px;'>").append(item.getAllowanceName()).append("</td>");
            sb.append("<td style='border: 1px solid black; padding: 8px; text-align: right;'>").append(fmt.format(item.getAmount())).append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody></table>");
        return sb.toString();
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
        ContractDTO dto = new ContractDTO();

        // Map các trường cơ bản của Contract
        dto.setId(entity.getId());
        dto.setContractTemplateId(entity.getContractTemplate().getId());
        dto.setContractTemplateName(entity.getContractTemplate().getName());
        dto.setContractname(entity.getContractname());
        dto.setType(entity.getType());
        dto.setBasesalary(entity.getBasesalary());
        dto.setInsuranceSalary(entity.getInsuranceSalary());
        dto.setStandardHours(entity.getStandardHours());
        dto.setFileurl(entity.getFileurl());
        dto.setSigndate(entity.getSigndate());
        dto.setStartdate(entity.getStartdate());
        dto.setEnddate(entity.getEnddate());
        dto.setStatus(entity.getStatus());
        dto.setWorkType(entity.getWorkType());
        dto.setInsurancePercent(entity.getInsurancePercent());

        // Map thông tin User (Nếu có)
        UserEntity user = entity.getUserID();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setFullname(user.getFullname());
            dto.setCccd(user.getCccd());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhonenumber());
            dto.setAddress(user.getAddress());
            dto.setDob(user.getBirth());
            dto.setGender(user.getGender());

            // Lấy phòng ban
            if (user.getDepartmentID() != null) {
                dto.setDepartmentName(user.getDepartmentID().getDepartmentname());
                dto.setDepartmentId(user.getDepartmentID().getId());
            }

            // Lấy chức vụ
            if (user.getRoleID() != null) {
                dto.setRoleName(user.getRoleID().getRolename());
            }
        }

        // Map danh sách Phụ cấp
        List<ContractsAllowanceDTO> allowanceDTOs = new ArrayList<>();
        List<ContractallowanceEntity> allowanceEntities = contractAllowancesRepository.findByContractID(entity);

        if (allowanceEntities != null && !allowanceEntities.isEmpty()) {
            allowanceDTOs = allowanceEntities.stream().map(a -> {
                ContractsAllowanceDTO ad = new ContractsAllowanceDTO();
                ad.setAllowanceCategoryId(a.getId());
                ad.setAllowanceName(a.getAllowanceName());
                ad.setAllowanceType(a.getAllowanceType());
                ad.setAmount(a.getAmount());
                ad.setIsTaxable(a.getIsTaxable());
                ad.setIsInsuranceBase(a.getIsInsuranceBase());
                ad.setTaxFreeAmount(a.getTaxFreeAmount());
                return ad;
            }).collect(Collectors.toList());
        }

        dto.setAllowances(allowanceDTOs);

        return dto;
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
//
//    //Lưu PDF
//    private String storeFile(MultipartFile file) throws IOException {
//        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
//        String newFileName = UUID.randomUUID().toString() + fileExtension;
//
//        // Tạo thư mục nếu chưa có
//        Path uploadPath = Paths.get(uploadDir.trim());
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        // Copy file vào thư mục
//        Path filePath = uploadPath.resolve(newFileName);
//        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//

    /// /        return "/" + uploadDir + "/" + newFileName;
//        return newFileName;
//    }
    @Transactional(rollbackFor = Exception.class)
    public ContractEntity updateContractFull(int contractId, ContractUpdateDTO request) throws IOException {
        // Tìm hợp đồng cũ
        ContractEntity contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng ID: " + contractId));

        if (!"DRAFT".equals(contract.getStatus())) {
            throw new RuntimeException("Chỉ được sửa thông tin khi hợp đồng đang ở trạng thái Nháp.");
        }

        // CẬP NHẬT THÔNG TIN HỢP ĐỒNG
        if (request.getContractName() != null) contract.setContractname(request.getContractName());
        if (request.getSignDate() != null) contract.setSigndate(request.getSignDate());
//        if (request.getWorkType() != null) contract.setWorkType(request.getWorkType());
        if (request.getWorkType() != null) {
            if (request.getWorkType().trim().isEmpty()) {
                throw new IllegalArgumentException("Loại hình làm việc không được để trống khi cập nhật.");
            }
            String normalizedType = request.getWorkType().toUpperCase();
            if (!"FULLTIME".equals(normalizedType) && !"PART_TIME".equals(normalizedType)) {
                throw new IllegalArgumentException("Loại hình làm việc cập nhật không hợp lệ (Chỉ nhận FULLTIME/PART_TIME).");
            }
            contract.setWorkType(normalizedType);
        }
        // Cập nhật Template (Mẫu hợp đồng)
        if (request.getContractTemplateId() != null) {
            ContractTemplateEntity tpl = templateRepository.findById(request.getContractTemplateId())
                    .orElseThrow(() -> new RuntimeException("Mẫu hợp đồng không tồn tại"));
            contract.setContractTemplate(tpl);
        }

        // Logic Lương & BHXH
        if (request.getBaseSalary() != null) contract.setBasesalary(request.getBaseSalary());

        // Cập nhật % BHXH và tính lại Lương đóng BHXH
        if (request.getInsurancePercent() != null) {
            contract.setInsurancePercent(BigDecimal.valueOf(request.getInsurancePercent()));
        }
        if (request.getStandardHours() != null) {
            contract.setStandardHours(request.getStandardHours());
        }
        // Tự động tính lại InsuranceSalary nếu có thay đổi Lương hoặc % (Giống logic create)
        if (request.getBaseSalary() != null || request.getInsurancePercent() != null) {
            BigDecimal base = request.getBaseSalary() != null ? request.getBaseSalary() : contract.getBasesalary();
            BigDecimal percent = request.getInsurancePercent() != null ? BigDecimal.valueOf(request.getInsurancePercent()) : contract.getInsurancePercent();

            if (percent == null) percent = BigDecimal.valueOf(100.0); // Mặc định 100%

            BigDecimal newInsSalary = base.multiply(percent).divide(BigDecimal.valueOf(100));
            contract.setInsuranceSalary(newInsSalary);
        }
        // Nếu người dùng cố tình nhập tay InsuranceSalary thì ưu tiên lấy số nhập tay
        if (request.getInsuranceSalary() != null) {
            contract.setInsuranceSalary(request.getInsuranceSalary());
        }

        // Logic Ngày tháng & Loại hợp đồng
        String type = (request.getType() != null) ? request.getType() : contract.getType();
        LocalDate start = (request.getStartDate() != null) ? request.getStartDate() : contract.getStartdate();
        if (request.getStartDate() != null && !request.getStartDate().equals(contract.getStartdate())) {
            validateContractOverlap(contract.getUserID().getId(), start, contractId);
        }
        LocalDate end = request.getEndDate();


        if ("FIXED_TERM".equalsIgnoreCase(type)) {
            if (end == null && contract.getEnddate() != null) end = contract.getEnddate();
            if (start != null && end != null) {
                if (end.isBefore(start)) throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu.");
                long months = ChronoUnit.MONTHS.between(start, end);
                if (months > 36) throw new RuntimeException("Hợp đồng xác định thời hạn không được vượt quá 36 tháng.");
            }
            contract.setEnddate(end);
        } else {
            contract.setEnddate(null); // Indefinite không có ngày kết thúc
        }
        contract.setType(type);
        contract.setStartdate(start);

        // CẬP NHẬT THÔNG TIN NHÂN SỰ
        UserEntity employee = contract.getUserID();
        if (employee != null) {
            boolean isUserChanged = false;
            if (request.getFullname() != null) {
                employee.setFullname(request.getFullname());
                isUserChanged = true;
            }
            if (request.getCccd() != null) {
                employee.setCccd(request.getCccd());
                isUserChanged = true;
            }
            if (request.getEmail() != null) {
                employee.setEmail(request.getEmail());
                isUserChanged = true;
            }
            if (request.getPhone() != null) {
                employee.setPhonenumber(request.getPhone());
                isUserChanged = true;
            }
            if (request.getAddress() != null) {
                employee.setAddress(request.getAddress());
                isUserChanged = true;
            }
            if (request.getDob() != null) {
                employee.setBirth(request.getDob());
                isUserChanged = true;
            }
            if (request.getGender() != null) {
                employee.setGender(request.getGender());
                isUserChanged = true;
            }

            // Cập nhật Phòng ban & Chức vụ
            if (request.getDepartmentId() != null) {
                employee.setDepartmentID(departmentRepository.findById(request.getDepartmentId()).orElse(null));
                contract.setDepartment(employee.getDepartmentID());
                isUserChanged = true;
            }
            if (request.getRoleId() != null) {
                employee.setRoleID(roleRepository.findById(request.getRoleId()).orElse(null));
                contract.setRole(employee.getRoleID());
                isUserChanged = true;
            }

            if (isUserChanged) {
                userRepository.save(employee);
            }
        }

        // XỬ LÝ PHỤ CẤP (Xóa cũ - Thêm mới)
        if (request.getAllowances() != null) {
            contractAllowancesRepository.deleteByContractID(contract);

            if (!request.getAllowances().isEmpty()) {
                List<ContractallowanceEntity> newAllowances = new ArrayList<>();
                for (ContractsAllowanceDTO dto : request.getAllowances()) {
                    ContractallowanceEntity item = new ContractallowanceEntity();
                    item.setContractID(contract);
                    item.setAllowanceName(dto.getAllowanceName());
                    item.setAllowanceType(dto.getAllowanceType());
                    item.setAmount(dto.getAmount());
                    item.setIsTaxable(Boolean.TRUE.equals(dto.getIsTaxable()));
                    item.setIsInsuranceBase(Boolean.TRUE.equals(dto.getIsInsuranceBase()));
                    item.setTaxFreeAmount(dto.getTaxFreeAmount() != null ? dto.getTaxFreeAmount() : BigDecimal.ZERO);
                    newAllowances.add(item);
                }
                contractAllowancesRepository.saveAll(newAllowances);
            }
        }


        return contractRepository.save(contract);
    }

    // Hàm validate chặn trùng lặp thời gian
    private void validateContractOverlap(Integer userId, LocalDate startDate, Integer currentContractId) {
        // Nếu là tạo mới (currentContractId == null) thì truyền -1 để query không lỗi
        int excludeId = (currentContractId == null) ? -1 : currentContractId;
        int conflictCount = contractRepository.countOverlappingActiveContracts(userId, startDate, excludeId);

        if (conflictCount > 0) {
            throw new RuntimeException("Ngày bắt đầu hợp đồng mới bị trùng với thời gian hiệu lực của Hợp đồng cũ đang Active!");
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void autoUpdateContractStatus() {
        LocalDate today = LocalDate.now();

        // Xử lý HĐ hết hạn (ACTIVE -> HISTORY)
        // Tìm các HĐ đang Active mà ngày kết thúc < hôm nay (tức là hôm qua là ngày cuối)
        List<ContractEntity> expiredContracts = contractRepository.findByStatusAndEnddateBefore("ACTIVE", today);

        for (ContractEntity contract : expiredContracts) {
            contract.setStatus("HISTORY");
        }
        contractRepository.saveAll(expiredContracts);

        // Kích hoạt HĐ mới (SIGNED -> ACTIVE)
        // Logic này dành cho HĐ đã ký trước (Status = SIGNED) và hôm nay bắt đầu có hiệu lực
        List<ContractEntity> newContracts = contractRepository.findByStatusAndStartdateLessThanEqual("SIGNED", today);

        for (ContractEntity contract : newContracts) {
            contract.setStatus("ACTIVE");
            UserEntity user = contract.getUserID();
            user.setStatus("active");
            userRepository.save(user);
        }
        contractRepository.saveAll(newContracts);

        System.out.println("Đã quét cập nhật trạng thái hợp đồng: " + today);
    }
}
