package com.manaplastic.backend.DTO.account;

import com.manaplastic.backend.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    private Integer userID;
    private String username;
//    private String password;
    private String fullname;
    private String cccd;
    private String email;
    private String phonenumber;
    private Gender gender;
    private LocalDate birth;
    private String address;
    private String bankAccount;
    private String bankName;
    private LocalDate hireDate;
    private String roleName;
    private String status;
    private Integer departmentID;
    private String departmentName;
    private Integer skillGrade;
    private String jobType;
    private String taxCode;
    private String socialInsuranceNumber;
//    private String constractName;

}