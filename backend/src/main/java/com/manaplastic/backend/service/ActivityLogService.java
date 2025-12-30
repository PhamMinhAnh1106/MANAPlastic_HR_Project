package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.account.ActivityLogDTO;
import com.manaplastic.backend.entity.ActivitylogEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.ActivityLogRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityLogService {
    @Autowired
    private ActivityLogRepository logRepo;

    public Page<ActivityLogDTO> getLogs(Pageable pageable, String keyword) {

//        Pageable pageable = PageRequest.of(page, size, Sort.by("actiontime").descending()); // test cách viết

        Specification<ActivitylogEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<ActivitylogEntity, UserEntity> userJoin = root.join("userID", JoinType.LEFT);
            if (StringUtils.hasText(keyword)) {
                // Chỉ cần lower keyword đầu vào
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate hasAction = cb.like(cb.lower(root.get("action")), likePattern);
                Predicate hasDetails = cb.like(root.get("details"), likePattern);
                Predicate hasUsername = cb.like(cb.lower(root.get("username")), likePattern);
                Predicate hasFullName = cb.like(cb.lower(userJoin.get("fullname")), likePattern);

                predicates.add(cb.or(hasAction, hasDetails, hasUsername, hasFullName));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<ActivitylogEntity> entities = logRepo.findAll(spec, pageable);
        return entities.map(this::convertToDTO);
    }

    private ActivityLogDTO convertToDTO(ActivitylogEntity entity) {
        ActivityLogDTO dto = new ActivityLogDTO();
        dto.setLogID(entity.getId());
        dto.setAction(entity.getAction());
        dto.setLogType(entity.getLogType() != null ? entity.getLogType().toString() : "INFO");
        dto.setDetails(entity.getDetails());
        dto.setActionTime(entity.getActiontime());

        if (entity.getUserID() != null) {
            dto.setPerformedBy(entity.getUserID().getUsername());
            dto.setExecutorName(entity.getUserID().getFullname());
        } else {
            dto.setPerformedBy(entity.getUsername());
            dto.setExecutorName("Unknown/System");
        }

        return dto;
    }
}