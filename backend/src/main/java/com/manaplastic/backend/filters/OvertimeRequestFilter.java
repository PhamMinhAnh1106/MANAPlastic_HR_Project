package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.criteria.OvertimeFilterCriteria;
import com.manaplastic.backend.entity.OvertimeRequestEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class OvertimeRequestFilter {

    public static Specification<OvertimeRequestEntity> filterRequests(OvertimeFilterCriteria criteria, UserEntity currentUser) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            String roleName = (currentUser.getRoleID() != null) ? currentUser.getRoleID().getRolename() : "";

            if ("Employee".equalsIgnoreCase(roleName)) {
                // Employee: Chỉ xem của chính mình
                predicates.add(cb.equal(root.get("userid").get("id"), currentUser.getId()));
            }

            else if ("Manager".equalsIgnoreCase(roleName)) {
                // Manager: Xem của mình HOẶC của phòng ban mình quản lý
                if (currentUser.getDepartmentID() != null) {
                    predicates.add(cb.equal(root.get("departmentid"), currentUser.getDepartmentID().getId()));
                } else {
                    // Manager không có phòng ban -> Chỉ xem được của mình
                    predicates.add(cb.equal(root.get("userid").get("id"), currentUser.getId()));
                }
            }

            // HR / Admin: Xem hết

            // Lọc theo Status
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("status").as(String.class), criteria.getStatus()));
            }

            // Lọc theo Phòng ban
            if (criteria.getDepartmentId() != null) {
                predicates.add(cb.equal(root.get("departmentid").get("id"), criteria.getDepartmentId()));
            }

            // Lọc theo ngày
            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), criteria.getFromDate()));
            }
            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), criteria.getToDate()));
            }

            // Lọc theo Keyword (Tìm trong Tên NV, Mã NV, hoặc Lý do)
            if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
                String pattern = "%" + criteria.getKeyword().toLowerCase() + "%";
                Predicate keywordPred = cb.or(
                        cb.like(cb.lower(root.get("userid").get("fullname").as(String.class)), pattern),
                        cb.like(cb.lower(root.get("userid").get("username")), pattern),
                        cb.like(cb.lower(root.get("reason").as(String.class)), pattern)
                );
                predicates.add(keywordPred);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}