package com.manaplastic.backend.service;

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

    public List<SalaryvariableEntity> getAllVariables() {
        return variableRepo.findAll();
    }

    @Transactional
    public SalaryvariableEntity saveVariable(SalaryvariableEntity payload) {
        // Validate dữ liệu đầu vào
        if (payload.getCode() == null || payload.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã biến không được để trống!");
        }

        String code = payload.getCode().trim();
        payload.setCode(code); // trim space

        // Kiểm tra trùng Code
        Optional<SalaryvariableEntity> existingOpt = variableRepo.findByCode(code);

        if (payload.getId() == null) {
            // Case: Tạo mới
            if (existingOpt.isPresent()) {
                throw new IllegalArgumentException("Mã biến '" + code + "' đã tồn tại!");
            }
        } else {
            // Case: Cập nhật
            if (existingOpt.isPresent()) {
                SalaryvariableEntity existing = existingOpt.get();
                // Nếu tìm thấy code trùng, nhưng ID lại khác ID đang sửa -> Trùng với người khác
                if (!existing.getId().equals(payload.getId())) {
                    throw new IllegalArgumentException("Mã biến '" + code + "' đã được sử dụng bởi biến khác!");
                }
            }
        }

        return variableRepo.save(payload);
    }

    @Transactional
    public void deleteVariable(int id) {
        if (!variableRepo.existsById(id)) {
            throw new IllegalArgumentException("Biến không tồn tại với ID: " + id);
        }
        variableRepo.deleteById(id);
    }
}