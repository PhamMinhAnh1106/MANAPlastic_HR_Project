package com.manaplastic.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    private Integer userID;
    private String username;
//    private String password;
    private String fullname;
    private Long cccd;
    private String email;
    private String phonenumber;
    private Boolean gender;
    private LocalDate birth;
    private String address;
    private String bankAccount;
    private String bankName;
    private LocalDate hireDate;
    private String roleName;
    private String status;
    private Integer departmentID;
    private Integer skillGrade;

}