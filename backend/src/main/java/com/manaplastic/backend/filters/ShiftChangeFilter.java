package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.schedule.ShiftChangeFilterCriteria;
import com.manaplastic.backend.constant.RequestStatus;
import com.manaplastic.backend.entity.ShiftChangeRequestEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ShiftChangeFilter {

    public static Specification<ShiftChangeRequestEntity> withCriteria(ShiftChangeFilterCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            //Lọc theo Status
            if (criteria.getStatus() != null && !criteria.getStatus().isBlank()) {
                try {
                    RequestStatus statusEnum = RequestStatus.valueOf(criteria.getStatus().toUpperCase());
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                }
            }

            // Lọc theo DepartmentID ( để service dùng )
            if (criteria.getDepartmentId() != null) {
                predicates.add(cb.equal(
                        root.get("employeeID").get("departmentID").get("id"),
                        criteria.getDepartmentId()
                ));
            }

            // Lọc theo EmployeeID ( cho bản thân nhân viên)
            if (criteria.getEmployeeId() != null) {
                predicates.add(cb.equal(
                        root.get("employeeID").get("id"),
                        criteria.getEmployeeId()
                ));
            }

            // Tìm kiếm theo username nhân viên
            if (criteria.getUsername() != null && !criteria.getUsername().isBlank()) {
                predicates.add(cb.like(
                        root.get("employeeID").get("username"),
                        "%" + criteria.getUsername() + "%"
                ));
            }

            // Lọc theo "Từ ngày"
            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("date"),
                        criteria.getFromDate()
                ));
            }

            // Lọc theo "Đến ngày"
            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("date"),
                        criteria.getToDate()
                ));
            }

            // Sắp xếp Mới nhất lên đầu (ORDER BY createdAt DESC)
            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
