package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.criteria.ContractFilterCriteria;
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
            if (filter.getUsername() != null && !filter.getUsername().isBlank()) {
                predicates.add(cb.like(root.get("userID").get("username"), "%" + filter.getUsername() + "%"));
            }


            // Lọc theo Loại hợp đồng (Full-time, Part-time...)
            if (filter.getType() != null && !filter.getType().isBlank()) {
                predicates.add(cb.equal(root.get("type"), filter.getType()));
            }

            // Lọc theo Trạng thái (ACTIVE, EXPIRED...)
            if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            // Lọc theo Lương cơ bản
            if (filter.getBasesalary() != null) {
                predicates.add(cb.equal(root.get("basesalary"), filter.getBasesalary()));
            }

            // Lọc theo Loại độc hại
            if (filter.getAllowanceToxicType() != null && !filter.getAllowanceToxicType().isBlank()) {
                predicates.add(cb.equal(root.get("allowanceToxicType"), filter.getAllowanceToxicType()));
            }

            // Lọc theo ngày bắt đầu ( sattrt date)
            if (filter.getStartdate() != null && filter.getEnddate() == null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startdate"), filter.getStartdate()));
            }

            // Lọc theo ngày kết thúc (enddate)
            if (filter.getEnddate() != null && filter.getStartdate() == null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("enddate"), filter.getEnddate()));
            }

            // Lọc các hợp đồng ĐANG HIỆU LỰC trong khoảng
            if (filter.getStartdate() != null && filter.getEnddate() != null) {
                Predicate startRangeCond = cb.greaterThanOrEqualTo(root.get("startdate"), filter.getStartdate());
                Predicate endRangeCond = cb.lessThanOrEqualTo(root.get("startdate"), filter.getEnddate());
                predicates.add(cb.and(startRangeCond, endRangeCond));
            }

            // Mặc định sắp xếp: Mới nhất lên đầu (theo ID giảm dần)
            if (query != null) {
                query.orderBy(cb.desc(root.get("id")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
