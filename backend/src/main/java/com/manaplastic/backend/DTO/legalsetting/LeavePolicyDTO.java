package com.manaplastic.backend.DTO.legalsetting;


import lombok.Data;

@Data
public class LeavePolicyDTO {
    private String leaveType;
    private Integer minYearsService;
    private Integer maxYearsService;
    private String jobType;
    private String genderTarget;
    private Integer days;
    private String description;
    private Integer leaveTypeId;
}