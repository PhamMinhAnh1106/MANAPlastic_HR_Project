package com.manaplastic.backend.DTO.criteria;

import lombok.Data;

@Data
public class PermissionFilterCriteria {
    // Tìm kiếm chung (keyword trong code hoặc description)
    private String keyword;
    // Lọc theo trạng thái (Active/Inactive)
    private Integer activeStatus;
    // Chỉ lấy quyền có cấu hình override
    private Boolean onlyOverride;
    // Lọc chính xác theo ID
    private Integer permissionId;
    // Lọc chính xác theo Code
    private String permissionCode;
}
