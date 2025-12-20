package com.manaplastic.backend.service;

import com.manaplastic.backend.entity.ActivitylogEntity;
import com.manaplastic.backend.repository.ActivityLogRepository;
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

    public Page<ActivitylogEntity> getLogs(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("actiontime").descending());

        // Specification (Logic lọc)
        Specification<ActivitylogEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                // Tìm kiếm keyword trong Action HOẶC Details HOẶC Username
                Predicate hasAction = cb.like(cb.lower(root.get("action")), likePattern);
                Predicate hasDetails = cb.like(cb.lower(root.get("details")), likePattern);
                Predicate hasUsername = cb.like(cb.lower(root.get("username")), likePattern);

                predicates.add(cb.or(hasAction, hasDetails, hasUsername));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return logRepo.findAll(spec, pageable);
    }
}