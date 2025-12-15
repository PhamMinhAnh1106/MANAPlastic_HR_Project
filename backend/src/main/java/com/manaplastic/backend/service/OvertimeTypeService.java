package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.legalsetting.OvertimeTypeDTO;
import com.manaplastic.backend.entity.OvertimetypeEntity;
import com.manaplastic.backend.repository.OvertimeTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OvertimeTypeService {

    @Autowired
    private OvertimeTypeRepository repo;

    public List<OvertimetypeEntity> getAll() {
        return repo.findAll();
    }

    @Transactional
    public OvertimetypeEntity create(OvertimeTypeDTO req) {
        // Validate trùng mã
        if (repo.existsByOtCode(req.getOtCode())) {
            throw new IllegalArgumentException("Mã OT '" + req.getOtCode() + "' đã tồn tại.");
        }

        validateRequest(req);

        OvertimetypeEntity entity = new OvertimetypeEntity();
        mapDtoToEntity(req, entity);

        return repo.save(entity);
    }

    @Transactional
    public OvertimetypeEntity update(int id, OvertimeTypeDTO req) {
        OvertimetypeEntity entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại OT với ID: " + id));

        // Validate trùng mã (nếu người dùng sửa mã thành mã đã có của thằng khác)
        if (req.getOtCode() != null && repo.existsByOtCodeAndIdNot(req.getOtCode(), id)) {
            throw new IllegalArgumentException("Mã OT '" + req.getOtCode() + "' đã được sử dụng bởi loại khác.");
        }

        validateRequest(req);
        mapDtoToEntity(req, entity);

        return repo.save(entity);
    }

    @Transactional
    public void delete(int id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy loại OT để xóa.");
        }
        repo.deleteById(id);
    }


    private void validateRequest(OvertimeTypeDTO req) {
        if (req.getRate() == null || req.getRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Hệ số lương (Rate) phải lớn hơn hoặc bằng 0.");
        }
        if (req.getOtName() == null || req.getOtName().isEmpty()) {
            throw new IllegalArgumentException("Tên loại làm thêm (otName) không được để trống.");
        }
    }

    private void mapDtoToEntity(OvertimeTypeDTO req, OvertimetypeEntity entity) {
        entity.setOtCode(req.getOtCode());
        entity.setOtName(req.getOtName());
        entity.setRate(req.getRate());
        entity.setIsTaxExemptPart(req.getIsTaxExemptPart());
        entity.setTaxExemptPercentage(req.getTaxExemptPercentage());
        entity.setDescription(req.getDescription());

        if (req.getCalculationType() != null) {
            try {
                entity.setCalculationType(OvertimetypeEntity.CalculationType.valueOf(req.getCalculationType()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Loại tính toán không hợp lệ: " + req.getCalculationType());
            }
        } else {
            entity.setCalculationType(OvertimetypeEntity.CalculationType.MULTIPLIER);
        }

        if (req.getTaxExemptFormula() != null) {
            try {
                entity.setTaxExemptFormula(OvertimetypeEntity.TaxExemptFormula.valueOf(req.getTaxExemptFormula()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Công thức miễn thuế không hợp lệ: " + req.getTaxExemptFormula());
            }
        } else {
            entity.setTaxExemptFormula(OvertimetypeEntity.TaxExemptFormula.NONE);
        }
    }
}