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
public class UpdateSelfIn4DTO {
//    private String password;
    private String fullname;
    private String cccd;
    private String email;
    private String taxCode;
    private String socialInsuranceNumber;
    private String phonenumber;
    private Gender gender;
    private LocalDate birth;
    private String address;
    private String bankAccount;
    private String bankName;
}
