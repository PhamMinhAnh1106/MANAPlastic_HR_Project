package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.LeaveRequestFilterCriteria;
import com.manaplastic.backend.entity.LeaverequestEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestFilter {
    public static Specification<LeaverequestEntity> withCriteria(LeaveRequestFilterCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.status() != null && !criteria.status().isBlank()) {
                try {
                    // Chuyển String "PENDING" thành Enum LeaverequestStatus.PENDING
                    LeaverequestEntity.LeaverequestStatus statusEnum =
                            LeaverequestEntity.LeaverequestStatus.valueOf(criteria.status().toUpperCase());

                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) { // Bỏ qua nếu nhu status gửi lên không hợp lệ
                }
            }

            // Lọc theo DepartmentID
            if (criteria.departmentId() != null) {
                // JOIN: leaverequest.userID.departmentID.id
                predicates.add(cb.equal(
                        root.get("userID").get("departmentID").get("id"),
                        criteria.departmentId()
                ));
            }

            // Lọc theo Username (tìm kiếm gần đúng, không phân biệt hoa thường)
            if (criteria.username() != null && !criteria.username().isBlank()) {
                // JOIN: leaverequest.userID.username
                predicates.add(cb.like(
                        cb.lower(root.get("userID").get("username")),
                        "%" + criteria.username().toLowerCase() + "%"
                ));
            }

            // Lọc theo "Từ ngày" (>= fromDate)
            // Lọc các đơn có ngày bắt đầu nghỉ (startdate) >= fromDate
            if (criteria.fromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("startdate"),
                        criteria.fromDate()
                ));
            }

            // Lọc theo "Đến ngày" (<= toDate)
            // Lọc các đơn có ngày bắt đầu nghỉ (startdate) <= toDate
            if (criteria.toDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("startdate"),
                        criteria.toDate()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
