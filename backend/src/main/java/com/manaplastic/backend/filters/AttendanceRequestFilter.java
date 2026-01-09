package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.criteria.AttendanceRequestFilterCriteria;
import com.manaplastic.backend.entity.AttendanceRequestEntity;
import com.manaplastic.backend.entity.UserEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AttendanceRequestFilter {
    public static Specification<AttendanceRequestEntity> filterRequests(AttendanceRequestFilterCriteria filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo User ID
            if (filter.getUserId() != null) {
                // query: where request.user.id = ?
                predicates.add(criteriaBuilder.equal(
                        root.get("userid").get("id"),
                        filter.getUserId()
                ));
            }

            // Lọc theo Phòng ban (Department ID)
            if (filter.getDepartmentId() != null) {
                // Join bảng User để lấy Department
                // query: where request.user.departmentID.id = ?
                Join<AttendanceRequestEntity, UserEntity> userJoin = root.join("userid", JoinType.INNER);

                predicates.add(criteriaBuilder.equal(
                        userJoin.get("departmentID").get("id"),
                        filter.getDepartmentId()
                ));
            }

            // Lọc theo Trạng thái (Status)
            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        filter.getStatus()
                ));
            }

            // Lọc theo khoảng thời gian (Từ ngày - Đến ngày)
            if (filter.getFromDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("date"),
                        filter.getFromDate()
                ));
            }
            if (filter.getToDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("date"),
                        filter.getToDate()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
