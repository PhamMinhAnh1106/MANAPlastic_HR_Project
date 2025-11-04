package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.UserFilterCriteria;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserFilter {

    public static Specification<UserEntity> withCriteria(UserFilterCriteria criteria) {
        return (root, query, cb) -> {
            // List chứa các điều kiện
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo departmentId
            if (criteria.departmentId() != null) {
//                predicates.add(cb.equal(root.get("departmentID"), criteria.departmentId()));
                predicates.add(cb.equal(root.get("departmentID").get("id"), criteria.departmentId()));
            }

            // 2. Lọc theo roleId
            if (criteria.roleId() != null) {
                predicates.add(cb.equal(root.get("roleID"), criteria.roleId()));
            }

            // 3. Lọc theo status
            if (criteria.status() != null && !criteria.status().isBlank()) {
                predicates.add(cb.equal(root.get("status"), criteria.status()));
            }

            // 4. Lọc theo gender
            if (criteria.gender() != null) {
                predicates.add(cb.equal(root.get("gender"), criteria.gender()));
            }

            // 5. Lọc theo khoảng ngày vào làm (hiredate)
            if (criteria.hireDateStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("hiredate"), criteria.hireDateStart()));
            }
            if (criteria.hireDateEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("hiredate"), criteria.hireDateEnd()));
            }

            // 6. Lọc theo TỪ KHÓA (Search Bar) - Đây là logic OR
            if (criteria.keyword() != null && !criteria.keyword().isBlank()) {
                String likePattern = "%" + criteria.keyword().toLowerCase() + "%";

                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("username")), likePattern),
                        cb.like(cb.lower(root.get("fullname")), likePattern),
                        cb.like(cb.lower(root.get("email")), likePattern),
                        cb.like(root.get("phonenumber"), likePattern),
                        cb.like(cb.toString(root.get("cccd")), likePattern)
                );
                predicates.add(searchPredicate);
            }

            // Kết hợp tất cả điều kiện lại bằng AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}