package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.payroll.RewardPunishmentDTO;
import com.manaplastic.backend.entity.RewardpunishmentdecisionEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.RewardPunishmentRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import static com.manaplastic.backend.entity.RewardpunishmentdecisionEntity.DecisionStatus.PENDING;

import java.util.ArrayList;
import java.util.List;

@Service
public class RewardPunishmentService {

    @Autowired
    private RewardPunishmentRepository repository;

    //Lấy
    public Page<RewardPunishmentDTO> getList(Pageable pageable, String keyword) {
        Specification<RewardpunishmentdecisionEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

//                Predicate hasReason = cb.like(cb.lower(root.get("reason")), likePattern);
                Join<RewardpunishmentdecisionEntity, UserEntity> userJoin = root.join("user", JoinType.LEFT);
                Predicate hasUsername = cb.like(cb.lower(userJoin.get("username")), likePattern);


                predicates.add(cb.or(hasUsername));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<RewardpunishmentdecisionEntity> entities = repository.findAll(spec, pageable);
        return entities.map(this::convertToDTO);
    }

    // Lấy chi tiết
    public RewardPunishmentDTO getById(Integer id) {
        RewardpunishmentdecisionEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quyết định không tồn tại với ID: " + id));
        return convertToDTO(entity);
    }

    // Thêm
    @Transactional
    public RewardPunishmentDTO create(RewardPunishmentDTO dto) {
        RewardpunishmentdecisionEntity entity = new RewardpunishmentdecisionEntity();
        mapDtoToEntity(dto, entity);

        if (entity.getStatus() == null) {
            entity.setStatus(PENDING);
        }

        RewardpunishmentdecisionEntity savedEntity = repository.save(entity);
        return convertToDTO(savedEntity);
    }

    // Sửa
    @Transactional
    public RewardPunishmentDTO update(Integer id, RewardPunishmentDTO dto) {
        RewardpunishmentdecisionEntity existingEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi để cập nhật"));

        mapDtoToEntity(dto, existingEntity);

        RewardpunishmentdecisionEntity updatedEntity = repository.save(existingEntity);
        return convertToDTO(updatedEntity);
    }

    // Xóa
    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy bản ghi để xóa");
        }
        repository.deleteById(id);
    }

    // hàm phụ helper
    private RewardPunishmentDTO convertToDTO(RewardpunishmentdecisionEntity entity) {
        RewardPunishmentDTO dto = new RewardPunishmentDTO();

        dto.setRewaid(entity.getId());
        dto.setUserID(entity.getUserID());

        if (entity.getUser() != null) {
            dto.setUserName(entity.getUser().getUsername());
        }

        dto.setType(entity.getType() != null ? entity.getType().name() : null);
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);

        dto.setReason(entity.getReason());
        dto.setDecisionDate(entity.getDecisionDate());

        dto.setAmount(entity.getAmount());
        dto.setIsTaxExempt(entity.getIsTaxExempt());

        return dto;
    }


    private void mapDtoToEntity(RewardPunishmentDTO dto, RewardpunishmentdecisionEntity entity) {
        entity.setUserID(dto.getUserID());
        entity.setReason(dto.getReason());
        entity.setDecisionDate(dto.getDecisionDate());
        entity.setAmount(dto.getAmount());
        entity.setIsTaxExempt(dto.getIsTaxExempt());

        if (StringUtils.hasText(dto.getType())) {
            entity.setType(RewardpunishmentdecisionEntity.RewardType.valueOf(dto.getType()));
        }
        if (StringUtils.hasText(dto.getStatus())) {
            entity.setStatus(RewardpunishmentdecisionEntity.DecisionStatus.valueOf(dto.getStatus()));
        }
    }
}