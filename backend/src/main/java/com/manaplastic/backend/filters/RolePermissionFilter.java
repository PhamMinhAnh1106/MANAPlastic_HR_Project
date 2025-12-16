package com.manaplastic.backend.filters;

import com.manaplastic.backend.DTO.criteria.RolePermissionCriteria;
import com.manaplastic.backend.entity.PermissionEntity;
import com.manaplastic.backend.entity.RolespermissionEntity;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RolePermissionFilter {
    public static Specification<PermissionEntity> filter(Integer roleId, RolePermissionCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo Keyword (Mã quyền hoặc Mô tả)
            if (criteria.getPermissionCode() != null && !criteria.getPermissionCode().isEmpty()) {
                String keyword = "%" + criteria.getPermissionCode().toLowerCase() + "%";
                Predicate hasCode = cb.like(cb.lower(root.get("permissionname")), keyword);
                Predicate hasDesc = cb.like(cb.lower(root.get("description").as(String.class)), keyword);
                predicates.add(cb.or(hasCode, hasDesc));
            }
            // Tìm xem quyền này có tồn tại trong bảng RolesPermission với roleId cụ thể không?

            if (criteria.getAssigned() != null || criteria.getActive() != null) {
                // Tạo câu truy vấn con: Select * from RolespermissionEntity rp where rp.permissionID = root.id AND rp.roleID = roleId
                Subquery<RolespermissionEntity> subquery = query.subquery(RolespermissionEntity.class);
                Root<RolespermissionEntity> subRoot = subquery.from(RolespermissionEntity.class);

                subquery.select(subRoot);

                // Điều kiện join: Permission ID khớp nhau và Role ID phải đúng cái đang xét
                Predicate joinPermission = cb.equal(subRoot.get("permissionID"), root); // Join 2 bảng
                Predicate matchRole = cb.equal(subRoot.get("roleID").get("id"), roleId); // Check Role ID

                List<Predicate> subPredicates = new ArrayList<>();
                subPredicates.add(joinPermission);
                subPredicates.add(matchRole);

                // lọc theo Active (Bật/Tắt)
                if (criteria.getActive() != null) {
                    subPredicates.add(cb.equal(subRoot.get("active"), criteria.getActive()));
                }

                subquery.where(subPredicates.toArray(new Predicate[0]));

                // Lọc theo Assigned (Đã gán hay chưa)
                if (criteria.getAssigned() != null) {
                    if (criteria.getAssigned()) {
                        // Nếu lọc "Đã gán": Phải TỒN TẠI trong bảng con
                        predicates.add(cb.exists(subquery));
                    } else {
                        // Nếu lọc "Chưa gán": Phải KHÔNG TỒN TẠI trong bảng con
                        predicates.add(cb.not(cb.exists(subquery)));
                    }
                } else if (criteria.getActive() != null) {
                    // Trường hợp admin không chọn assigned nhưng chọn active=true/false
                    // Thì ngầm hiểu là phải tồn tại (assigned=true) mới xét active được
                    predicates.add(cb.exists(subquery));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
