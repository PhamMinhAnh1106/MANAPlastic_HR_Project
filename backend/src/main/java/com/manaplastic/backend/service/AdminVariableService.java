package com.manaplastic.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manaplastic.backend.DTO.payroll.VariableRuleRequest;
import com.manaplastic.backend.entity.SalaryvariableEntity;
import com.manaplastic.backend.repository.SalaryVariableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminVariableService {

    @Autowired
    private SalaryVariableRepository variableRepo;
    @Autowired
    private SqlGeneratorService sqlGenerator;
    @Autowired
    private ObjectMapper objectMapper;

    public List<SalaryvariableEntity> getAllVariables() {
        return variableRepo.findAll();
    }

    @Transactional
    public SalaryvariableEntity createVariable(VariableRuleRequest request) {
        String code = request.getCode().trim();

        if (variableRepo.findByCode(code).isPresent()) {
            throw new IllegalArgumentException("Mã biến '" + code + "' đã tồn tại!");
        }

        SalaryvariableEntity entity = new SalaryvariableEntity();
        return processAndSave(entity, request);
    }


    @Transactional
    public SalaryvariableEntity updateVariable(Integer id, VariableRuleRequest request) {
        SalaryvariableEntity entity = variableRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biến với ID: " + id));

        String newCode = request.getCode().trim();
        Optional<SalaryvariableEntity> duplicateCheck = variableRepo.findByCode(newCode);
        if (duplicateCheck.isPresent() && !duplicateCheck.get().getId().equals(id)) {
            throw new IllegalArgumentException("Mã biến '" + newCode + "' đã được sử dụng!");
        }

        return processAndSave(entity, request);
    }

    @Transactional
    public void deleteVariable(int id) {
        if (!variableRepo.existsById(id)) {
            throw new IllegalArgumentException("Biến không tồn tại với ID: " + id);
        }
        variableRepo.deleteById(id);
    }

   // Dùng chung cho save và úpdate
    private SalaryvariableEntity processAndSave(SalaryvariableEntity entity, VariableRuleRequest request) {
        // Map thông tin cơ bản
        entity.setCode(request.getCode().trim());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        // Sinh SQL từ JSON Logic (Core feature)
        String generatedSql = sqlGenerator.generateSql(request);
        entity.setSqlQuery(generatedSql);

        // Lưu Metadata (JSON gốc) để UI load lại form
        try {
            String metadataJson = objectMapper.writeValueAsString(request);
            entity.setBuilderMetadata(metadataJson);
        } catch (Exception e) {
            System.err.println("Lỗi parse metadata: " + e.getMessage());
        }

        return variableRepo.save(entity);
    }
}