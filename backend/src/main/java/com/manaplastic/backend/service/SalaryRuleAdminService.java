package com.manaplastic.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manaplastic.backend.entity.SalaryRuleEntity;
import com.manaplastic.backend.entity.SalaryRuleVersionEntity;
import com.manaplastic.backend.repository.SalaryRuleRepository;
import com.manaplastic.backend.repository.SalaryRuleVersionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SalaryRuleAdminService {

    @Autowired
    private SalaryRuleRepository ruleRepo;

    @Autowired
    private SalaryRuleVersionRepository versionRepo;

    @Autowired
    private ObjectMapper objectMapper;

    public List<SalaryRuleEntity> getAllRules() {
        return ruleRepo.findAll();
    }

    @Transactional
    public void saveRule(String code, String name, String dslJson) throws Exception {
        Map<String, Object> jsonMap;
        try {
            jsonMap = objectMapper.readValue(dslJson, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cú pháp JSON công thức không hợp lệ!");
        }


        SalaryRuleEntity rule = ruleRepo.findByRuleCode(code)
                .orElse(new SalaryRuleEntity());

        boolean isNew = (rule.getId() == null);

        rule.setRuleCode(code);
        rule.setName(name);
        rule.setStatus("APPROVED"); // Mặc định approve để test nhanh

        // Lưu Rule cha trước để có ID
        rule = ruleRepo.save(rule);

        // Tạo Version Mới
        // Logic: Nếu là mới -> Version 1. Nếu cũ -> Thì lấy version hiện tại + 1
        int nextVer = 1;
        if (!isNew && rule.getCurrentVersionId() != null) {
            SalaryRuleVersionEntity currentVer = versionRepo.findById(rule.getCurrentVersionId()).orElse(null);
            if (currentVer != null) {
                nextVer = currentVer.getVersionNumber() + 1;
            }
        }

        SalaryRuleVersionEntity newVersion = new SalaryRuleVersionEntity();
        newVersion.setRuleId(rule.getId());
        newVersion.setVersionNumber(nextVer);
        newVersion.setDslJson(jsonMap); // Lưu chuỗi JSON
        newVersion.setCreatedAt(LocalDateTime.now());
        newVersion = versionRepo.save(newVersion);
        rule.setCurrentVersionId(newVersion.getId());
        ruleRepo.save(rule);
    }

    @Transactional
    public void deleteRule(int id) { // chỉ đổi trạng thái thôi chứ không xóa cứng vì nó dính bảng cache
        SalaryRuleEntity rule = ruleRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy Rule"));
        rule.setStatus("RETIRED");
        ruleRepo.save(rule);
    }
}
