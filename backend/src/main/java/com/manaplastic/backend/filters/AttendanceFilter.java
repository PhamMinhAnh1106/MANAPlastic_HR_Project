package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.criteria.AttendanceFilterCriteria;
import com.manaplastic.backend.entity.AttendanceEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceFilter {
    public static Specification<AttendanceEntity> withCriteria(AttendanceFilterCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            // Lọc theo Năm
            if (criteria.getYear() != null) {
                // year() trên cột date
                predicates.add(cb.equal(cb.function("year", Integer.class, root.get("date")), criteria.getYear()));
            }

            // Lọc theo Tháng
            if (criteria.getMonth() != null) {
                // month() trên cột date
                predicates.add(cb.equal(cb.function("month", Integer.class, root.get("date")), criteria.getMonth()));
            }

            // Lọc theo UserID (Dùng cho Employee/Manager)
            if (criteria.getUserId() != null) {
                predicates.add(cb.equal(root.get("userID").get("id"), criteria.getUserId()));
            }

            // Lọc theo DepartmentID (Dùng cho HR)
            if (criteria.getDepartmentId() != null) {
                //  JOIN qua User Entity: attendance.user.departmentID.id (mã phòng ban của user)
                predicates.add(cb.equal(root.get("userID").get("departmentID").get("id"), criteria.getDepartmentId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
