package com.manaplastic.backend.DTO.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDTO {
    private Integer userID;
    private String fullname;
    private String username;
    private String departmentName; // Thêm phòng ban để dễ chọn
}