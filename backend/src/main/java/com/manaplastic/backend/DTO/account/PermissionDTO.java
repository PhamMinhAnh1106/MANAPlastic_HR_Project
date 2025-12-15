package com.manaplastic.backend.DTO.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Integer permissionId;
    private String permissionCode;
    private String description;
    private Integer activePermission;
    private boolean enabledByRole;
}