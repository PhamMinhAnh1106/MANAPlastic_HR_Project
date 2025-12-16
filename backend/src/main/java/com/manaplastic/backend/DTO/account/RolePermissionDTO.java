package com.manaplastic.backend.DTO.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RolePermissionDTO { // xem
    private Integer permissionId;
    private String permissionCode;
    private String description;
    private boolean assigned;
    private boolean active;
}
