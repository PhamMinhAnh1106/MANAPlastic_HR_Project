package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.criteria.PayrollFilterCriteria;
import com.manaplastic.backend.entity.DepartmentEntity;
import com.manaplastic.backend.entity.PayrollEntity;
import com.manaplastic.backend.entity.UserEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PayrollFilter {
    public static Specification<PayrollEntity> filterBy(PayrollFilterCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<PayrollEntity, UserEntity> userJoin = root.join("userID", JoinType.INNER);

            // Lọc theo Kỳ Lương
            if (criteria.getMonth() != null && criteria.getYear() != null) {
                String period = String.format("%d-%02d", criteria.getYear(), criteria.getMonth());
                predicates.add(cb.equal(root.get("payperiod"), period));
            }

            // Lọc theo mã Phòng Ban
            if (criteria.getDepartmentId() != null) {
                Join<UserEntity, DepartmentEntity> deptJoin = userJoin.join("departmentID", JoinType.INNER);
                predicates.add(cb.equal(deptJoin.get("id"), criteria.getDepartmentId()));
            }

           // Lọc userName
            if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
                String searchKey = "%" + criteria.getUsername().toLowerCase() + "%";
                Predicate checkUsername = cb.like(cb.lower(userJoin.get("username")), searchKey);
                predicates.add(cb.or(checkUsername));
            }

            // Lọc theo Khoảng Lương
            // Lọc lương thực lĩnh (netsalary) >= Min
            if (criteria.getMinSalary() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("netsalary"), criteria.getMinSalary()));
            }
            // Lọc lương thực lĩnh (netsalary) <= Max
            if (criteria.getMaxSalary() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("netsalary"), criteria.getMaxSalary()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}