package com.manaplastic.backend.DTO.account;

// package com.manaplastic.backend.DTO;

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
public class UpdateAccountDTO {

    private String fullname;
    private String email;
    private String phonenumber;
    private String address;
    private String cccd;
    private  String taxCode;
    private String socialInsuranceNumber;
    private Gender gender;
    private LocalDate birth;
    private String bankAccount;
    private String bankName;

    // ===== CÁC TRƯỜNG ĐẶC BIỆT CỦA HR =====
    private Integer departmentID;
    private Integer roleID;
    private String status;
    private LocalDate hireDate;
    private Integer skillGrade;
    private String jobType;
}