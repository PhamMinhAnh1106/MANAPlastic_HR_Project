package com.manaplastic.backend.DTO.criteria;

import lombok.Data;

@Data
public class RolePermissionCriteria {
    private Boolean assigned;       // true (Đã gán), false (Chưa gán), null (Tất cả)
    private Boolean active;         // true (Đang bật), false (Đang tắt)
    private String permissionCode;
}
