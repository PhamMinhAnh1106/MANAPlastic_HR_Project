package com.manaplastic.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    private Integer userID;
    private String username;
    //private String password;
    private String fullname;
    private String email;
    private String phonenumber;
    private String address;
    private String roleName;
    private Integer departmentID;

}