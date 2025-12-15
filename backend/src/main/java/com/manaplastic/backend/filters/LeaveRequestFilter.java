package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.criteria.LeaveRequestFilterCriteria;
import com.manaplastic.backend.entity.LeaverequestEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestFilter {
    public static Specification<LeaverequestEntity> withCriteria(LeaveRequestFilterCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getStatus() != null && !criteria.getStatus().isBlank()) {
                try {
                    // Chuyển String "PENDING" thành Enum LeaverequestStatus.PENDING
                    LeaverequestEntity.LeaverequestStatus statusEnum =
                            LeaverequestEntity.LeaverequestStatus.valueOf(criteria.getStatus().toUpperCase());

                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) { // Bỏ qua nếu nhu status gửi lên không hợp lệ
                }
            }

            // Lọc theo DepartmentID
            if (criteria.getDepartmentId() != null) {
                predicates.add(cb.equal(
                        root.get("userID").get("departmentID").get("id"),
                        criteria.getDepartmentId()
                ));
            }

            // Lọc theo Username (tìm kiếm gần đúng, không phân biệt hoa thường)
            if (criteria.getUsername() != null && !criteria.getUsername().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("userID").get("username")),
                        "%" + criteria.getUsername().toLowerCase() + "%"
                ));
            }

            // Lọc theo "Từ ngày" (>= fromDate)
            // Lọc các đơn có ngày bắt đầu nghỉ (startdate) >= fromDate
            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("startdate"),
                        criteria.getFromDate()
                ));
            }

            // Lọc theo "Đến ngày" (<= toDate)
            // Lọc các đơn có ngày bắt đầu nghỉ (startdate) <= toDate
            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("startdate"),
                        criteria.getToDate()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
