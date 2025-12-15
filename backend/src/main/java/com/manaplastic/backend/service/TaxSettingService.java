package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.legalsetting.TaxSettingDTO;
import com.manaplastic.backend.entity.TaxsettingEntity;
import com.manaplastic.backend.repository.TaxSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaxSettingService {

    @Autowired
    private TaxSettingRepository repo;

    // Lấy danh sách tất cả (cho Admin xem lịch sử)
    public List<TaxsettingEntity> getList(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return repo.findBySettingKeyContaining(keyword.trim());
        }
        return repo.findAll();
    }

    // Lấy danh sách HIỆN HÀNH
    public List<TaxsettingEntity> getCurrentSettings() {
        return repo.findCurrentEffectiveSettings(LocalDate.now());
    }

    //Tạo
    @Transactional
    public TaxsettingEntity create(TaxSettingDTO req) {
        validateRequest(req);

        // Kiểm tra Unique Constraint: Một Key không thể có 2 dòng cùng ngày hiệu lực
        if (repo.existsBySettingKeyAndEffectiveDate(req.getSettingKey(), req.getEffectiveDate())) {
            throw new IllegalArgumentException("Tham số '" + req.getSettingKey() + "' đã có cấu hình cho ngày " + req.getEffectiveDate());
        }

        TaxsettingEntity entity = new TaxsettingEntity();
        mapDtoToEntity(req, entity);

        return repo.save(entity);
    }

    // Cập nhật
    @Transactional
    public TaxsettingEntity update(int id, TaxSettingDTO req) {
        TaxsettingEntity entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình thuế ID: " + id));

        validateRequest(req);

        // Check trùng nếu user cố tình sửa ngày thành ngày đã tồn tại của key đó
        if (repo.existsBySettingKeyAndEffectiveDateAndIdNot(req.getSettingKey(), req.getEffectiveDate(), id)) {
            throw new IllegalArgumentException("Ngày hiệu lực này đã tồn tại cho tham số " + req.getSettingKey());
        }

        mapDtoToEntity(req, entity);
        return repo.save(entity);
    }

    // Xóa
    @Transactional
    public void delete(int id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy cấu hình để xóa.");
        }
        repo.deleteById(id);
    }

 // Helper
    private void validateRequest(TaxSettingDTO req) {
        if (req.getSettingKey() == null || req.getSettingKey().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tham số (Key) không được để trống.");
        }
        if (req.getValue() == null) {
            throw new IllegalArgumentException("Giá trị không được để trống.");
        }
        if (req.getEffectiveDate() == null) {
            throw new IllegalArgumentException("Ngày hiệu lực không được để trống.");
        }
    }

    private void mapDtoToEntity(TaxSettingDTO req, TaxsettingEntity entity) {
        entity.setSettingKey(req.getSettingKey().toUpperCase().trim()); // Key viết hoa
        entity.setValue(req.getValue());
        entity.setEffectiveDate(req.getEffectiveDate());
        entity.setDescription(req.getDescription());

        if (req.getIsActive() != null) {
            entity.setIsActive(req.getIsActive());
        } else {
            entity.setIsActive(true);
        }
    }
}