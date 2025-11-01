package com.manaplastic.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatein4DTO {
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
}
