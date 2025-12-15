package com.manaplastic.backend.DTO.account;

import lombok.Data;

@Data
public class UpdateUserPermissionDTO {
    private Integer userId;
    private String username;
    private Integer permissionId;
    private Integer activePermission;
}
