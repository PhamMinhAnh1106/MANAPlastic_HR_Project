package com.manaplastic.backend.DTO.account;

import lombok.Data;

@Data
public class UpdateRolePermissionDTO {
    private Integer roleId;
    private Integer permissionId;
    private Boolean active; // True = Active, False = Inactive
    private Boolean assigned; // True = Gán quyền, False = Gỡ quyền hẳn khỏi DB
}
