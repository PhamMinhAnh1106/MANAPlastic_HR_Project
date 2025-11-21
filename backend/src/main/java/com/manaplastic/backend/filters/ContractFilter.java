package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.ContractFilterCriteria;
import com.manaplastic.backend.entity.ContractEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ContractFilter {
    public static Specification<ContractEntity> filterContracts(ContractFilterCriteria filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();


            // Lọc theo Username
            if (filter.username() != null && !filter.username().isBlank()) {
                predicates.add(cb.like(root.get("userID").get("username"), "%" + filter.username() + "%"));
            }


            // Lọc theo Loại hợp đồng (Full-time, Part-time...)
            if (filter.type() != null && !filter.type().isBlank()) {
                predicates.add(cb.equal(root.get("type"), filter.type()));
            }

            // Lọc theo Trạng thái (ACTIVE, EXPIRED...)
            if (filter.status() != null && !filter.status().isBlank()) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            // Lọc theo Lương cơ bản
            if (filter.basesalary() != null) {
                predicates.add(cb.equal(root.get("basesalary"), filter.basesalary()));
            }

            // Lọc theo Loại độc hại
            if (filter.allowanceToxicType() != null && !filter.allowanceToxicType().isBlank()) {
                predicates.add(cb.equal(root.get("allowanceToxicType"), filter.allowanceToxicType()));
            }


            // Lọc các hợp đồng ĐANG HIỆU LỰC trong khoảng [validFrom - validTo]

            if (filter.validFrom() != null && filter.validTo() != null) {
                Predicate startCond = cb.lessThanOrEqualTo(root.get("startdate"), filter.validTo());
                Predicate endCondA = cb.greaterThanOrEqualTo(root.get("enddate"), filter.validFrom());
                Predicate endCondB = cb.isNull(root.get("enddate"));
                Predicate endCond = cb.or(endCondA, endCondB);

                predicates.add(cb.and(startCond, endCond));
            }

            // Mặc định sắp xếp: Mới nhất lên đầu (theo ID giảm dần)
            if (query != null) {
                query.orderBy(cb.desc(root.get("id")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
