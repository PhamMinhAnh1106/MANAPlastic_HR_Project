package com.manaplastic.backend.service;


import com.manaplastic.backend.DTO.legalsetting.LeavePolicyDTO;
import com.manaplastic.backend.constant.Gender;
import com.manaplastic.backend.entity.LeavepolicyEntity;
import com.manaplastic.backend.entity.ShiftEntity;
import com.manaplastic.backend.repository.LeavePolicyRepository;
import com.manaplastic.backend.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeavePolicyService {

    @Autowired
    private LeavePolicyRepository repo;
    @Autowired
    private ShiftRepository shiftRepo;

    // Lấy tất cả
    public List<LeavepolicyEntity> getAllPolicies() {
        return repo.findAll();
    }

    // Tạo mới chính sách
    @Transactional
    public LeavepolicyEntity createPolicy(LeavePolicyDTO req) {
        validateRequest(req);

        LeavepolicyEntity policy = new LeavepolicyEntity();
        mapDtoToEntity(req, policy);

        return repo.save(policy);
    }

    // Cập nhật chính sách
    @Transactional
    public LeavepolicyEntity updatePolicy(int id, LeavePolicyDTO req) {
        LeavepolicyEntity policy = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chính sách nghỉ phép với ID: " + id));

        validateRequest(req);
        mapDtoToEntity(req, policy);

        return repo.save(policy);
    }

    // Xóa chính sách
    @Transactional
    public void deletePolicy(int id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy chính sách để xóa.");
        }
        repo.deleteById(id);
    }

    // --- Private Helper Methods ---

    private void mapDtoToEntity(LeavePolicyDTO req, LeavepolicyEntity entity) {
        if (req.getLeaveType() == null || req.getLeaveType().isEmpty()) {
            throw new IllegalArgumentException("Loại nghỉ phép (leaveType) không được để trống.");
        }

        // Chuyển String sang Enum
        try {
            entity.setLeavetype(LeavepolicyEntity.LeaveType.valueOf(req.getLeaveType()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Loại nghỉ phép không hợp lệ: " + req.getLeaveType());
        }

        entity.setMinyearsservice(req.getMinYearsService());
        entity.setMaxyearsservice(req.getMaxYearsService());
        entity.setJobtype(req.getJobType());
        if (req.getGenderTarget() != null && !req.getGenderTarget().isEmpty()) {
            try {
                entity.setGendertarget(Gender.valueOf(req.getGenderTarget()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Giới tính không hợp lệ: " + req.getGenderTarget());
            }
        } else {
            entity.setGendertarget(null);
        }
        entity.setDays(req.getDays());
        entity.setDescription(req.getDescription());
        if (req.getLeaveTypeId() != null) {
            ShiftEntity shift = shiftRepo.findById(req.getLeaveTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Mã loại công (Shift ID) không tồn tại: " + req.getLeaveTypeId()));

            entity.setLeavetypeid(shift);
        } else {
            entity.setLeavetypeid(null);
        }
    }

    private void validateRequest(LeavePolicyDTO req) {
        if (req.getMinYearsService() < 0) {
            throw new IllegalArgumentException("Thâm niên tối thiểu không được âm.");
        }

        if (req.getMaxYearsService() != null && req.getMaxYearsService() < req.getMinYearsService()) {
            throw new IllegalArgumentException("Thâm niên tối đa phải lớn hơn hoặc bằng thâm niên tối thiểu.");
        }

        if (req.getDays() == null || req.getDays() < 0) {
            throw new IllegalArgumentException("Số ngày phép được hưởng không hợp lệ.");
        }
    }
}