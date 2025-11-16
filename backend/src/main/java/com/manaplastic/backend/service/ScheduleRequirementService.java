package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.RequirementRuleDTO;
import com.manaplastic.backend.DTO.ScheduleRequirementDTO;
import com.manaplastic.backend.entity.RequirementrulesEntity;
import com.manaplastic.backend.entity.SchedulerequirementEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import com.manaplastic.backend.repository.DepartmentRepository;
import com.manaplastic.backend.repository.RequirementRuleRepository;
import com.manaplastic.backend.repository.ScheduleRequirementRepository;
import com.manaplastic.backend.repository.ShiftRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleRequirementService {
    private final ScheduleRequirementRepository requirementRepository;
    private final RequirementRuleRepository ruleRepository;
    private final ShiftRepository shiftRepository;
    private final DepartmentRepository departmentRepository;


    public List<ScheduleRequirementDTO> getRequirementsForManager(UserEntity manager) {
        Integer departmentId = getManagerDepartmentId(manager);
        List<SchedulerequirementEntity> entities = requirementRepository.findByDepartmentID_Id(departmentId);
        return entities.stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }


    public ScheduleRequirementDTO createRequirement(UserEntity manager, ScheduleRequirementDTO dto) {
        Integer departmentId = getManagerDepartmentId(manager);
        if (!dto.departmentId().equals(departmentId)) {
            throw new AccessDeniedException("Manager chỉ có thể tạo quy tắc cho phòng ban của mình.");
        }

        SchedulerequirementEntity entity = new SchedulerequirementEntity();
        mapDtoToEntityOnCreate(entity, dto);

        SchedulerequirementEntity savedEntity = requirementRepository.save(entity);

        List<RequirementrulesEntity> rules = dto.rules().stream()
                .map(ruleDto -> mapRuleDtoToEntity(new RequirementrulesEntity(), ruleDto, savedEntity))
                .collect(Collectors.toList());
        ruleRepository.saveAll(rules);

        savedEntity.setRules(rules);
        return mapEntityToDTO(savedEntity);
    }


    @Transactional
    public ScheduleRequirementDTO updateRequirement(Integer id, ScheduleRequirementDTO dto, UserEntity manager) {

        SchedulerequirementEntity entity = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quy tắc với ID: " + id));

        checkAccess(manager, entity);
        mapDtoToEntityOnUpdate(entity, dto);
        List<RequirementrulesEntity> existingRules = entity.getRules();
        existingRules.clear();

        for (RequirementRuleDTO ruleDto : dto.rules()) {
            RequirementrulesEntity newChildRule = new RequirementrulesEntity();
            mapRuleDtoToEntity(newChildRule, ruleDto, entity);
            existingRules.add(newChildRule);
        }

        SchedulerequirementEntity savedEntity = requirementRepository.save(entity);

        return mapEntityToDTO(savedEntity);
    }


    public void deleteRequirement(Integer id, UserEntity manager) {
        SchedulerequirementEntity entity = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quy tắc với ID: " + id));
        checkAccess(manager, entity);

        requirementRepository.delete(entity);
    }


    private Integer getManagerDepartmentId(UserEntity manager) {
        if (manager.getDepartmentID() == null) {
            throw new AccessDeniedException("Tài khoản Manager không được gán vào phòng ban nào.");
        }
        return manager.getDepartmentID().getId();
    }

    private void checkAccess(UserEntity manager, SchedulerequirementEntity requirement) {
        Integer managerDeptId = getManagerDepartmentId(manager);
        if (!requirement.getDepartmentID().getId().equals(managerDeptId)) {
            throw new AccessDeniedException("Manager không có quyền truy cập quy tắc này.");
        }
    }

    private ScheduleRequirementDTO mapEntityToDTO(SchedulerequirementEntity entity) {
        List<RequirementRuleDTO> ruleDTOs = entity.getRules().stream()
                .map(rule -> new RequirementRuleDTO(
                        rule.getId(),
                        rule.getRequiredSkillgrade(),
                        rule.getMinStaffCount()))
                .collect(Collectors.toList());

        return new ScheduleRequirementDTO(
                entity.getId(),
                entity.getDepartmentID().getId(),
                entity.getShiftID().getId(),
                entity.getTotalStaffNeeded(),
                ruleDTOs
        );
    }

    private void mapDtoToEntityOnCreate(SchedulerequirementEntity entity, ScheduleRequirementDTO dto) {
        entity.setDepartmentID(departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new RuntimeException("Phòng ban không tồn tại.")));
        entity.setShiftID(shiftRepository.findById(dto.shiftId())
                .orElseThrow(() -> new RuntimeException("Ca làm việc không tồn tại.")));
        entity.setTotalStaffNeeded(dto.totalStaffNeeded());
    }


    private void mapDtoToEntityOnUpdate(SchedulerequirementEntity entity, ScheduleRequirementDTO dto) {
        entity.setTotalStaffNeeded(dto.totalStaffNeeded());
    }

//    private void mapDtoToEntity(SchedulerequirementEntity entity, ScheduleRequirementDTO dto) {
//        entity.setDepartmentID(departmentRepository.findById(dto.departmentId())
//                .orElseThrow(() -> new RuntimeException("Phòng ban không tồn tại.")));
//        entity.setShiftID(shiftRepository.findById(dto.shiftId())
//                .orElseThrow(() -> new RuntimeException("Ca làm việc không tồn tại.")));
//        entity.setTotalStaffNeeded(dto.totalStaffNeeded());
//    }

    private RequirementrulesEntity mapRuleDtoToEntity(RequirementrulesEntity entity, RequirementRuleDTO dto, SchedulerequirementEntity parent) {
        entity.setRequirementID(parent);
        entity.setRequiredSkillgrade(dto.requiredSkillGrade());
        entity.setMinStaffCount(dto.minStaffCount());
        return entity;
    }
}