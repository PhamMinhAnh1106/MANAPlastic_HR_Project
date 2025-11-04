package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.AttendanceFilterCriteria;
import com.manaplastic.backend.entity.AttendanceEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceFilter {
    public static Specification<AttendanceEntity> withCriteria(AttendanceFilterCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.status() != null && !criteria.status().isBlank()) {
                predicates.add(cb.equal(root.get("status").as(String.class), criteria.status()));
            }

            // Lọc theo Năm
            if (criteria.year() != null) {
                // year() trên cột date
                predicates.add(cb.equal(cb.function("year", Integer.class, root.get("date")), criteria.year()));
            }

            // Lọc theo Tháng
            if (criteria.month() != null) {
                // month() trên cột date
                predicates.add(cb.equal(cb.function("month", Integer.class, root.get("date")), criteria.month()));
            }

            // Lọc theo UserID (Dùng cho Employee/Manager)
            if (criteria.userId() != null) {
                predicates.add(cb.equal(root.get("userID").get("id"), criteria.userId()));
            }

            // Lọc theo DepartmentID (Dùng cho HR)
            if (criteria.departmentId() != null) {
                //  JOIN qua User Entity: attendance.user.departmentID.id (mã phòng ban của user)
                predicates.add(cb.equal(root.get("userID").get("departmentID").get("id"), criteria.departmentId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
