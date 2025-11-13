package com.manaplastic.backend.DTO;

// package com.manaplastic.backend.DTO;

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
    private Long cccd;
    private Boolean gender;
    private LocalDate birth;
    private String bankAccount;
    private String bankName;

    // ===== CÁC TRƯỜNG ĐẶC BIỆT CỦA HR =====
    private Integer departmentID;
    private Integer roleID;
    private String status;
    private LocalDate hireDate;
    private Integer skillGrade;
}