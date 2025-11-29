package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.PayrollFilterCriteria;
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
            if (criteria.getUserName() != null && !criteria.getUserName().isEmpty()) {
                String searchKey = "%" + criteria.getUserName().toLowerCase() + "%";
                Predicate checkUsername = cb.like(cb.lower(userJoin.get("username")), searchKey);
                predicates.add(cb.or(checkUsername));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}