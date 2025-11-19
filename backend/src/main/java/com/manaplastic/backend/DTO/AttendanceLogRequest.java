package com.manaplastic.backend.DTO;

import lombok.Data;

@Data
public class AttendanceLogRequest {
    private Integer userId;
    private String imageBase64;  //dữ liệu ảnh base64
}
