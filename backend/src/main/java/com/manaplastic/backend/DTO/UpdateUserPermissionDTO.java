package com.manaplastic.backend.DTO;

import lombok.Data;

@Data
public class UpdateUserPermissionDTO {
    private Integer userId;
    private String username;
    private Integer permissionId;
    private Integer activePermission;
}
