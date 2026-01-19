package com.manaplastic.backend.DTO.criteria;

//public record AttendanceFilterCriteria(
//        Integer month,
//        Integer year,
//        Integer departmentId, // Chỉ dùng cho HR
//        Integer userId, // Dùng để giới hạn cho Employee/Manager
//        String status
//) {} // Quá phiền v không có Getter/Setter => mỗi lần làm gì đều phải tạo mới 1 Object (Chưa biết cách dùng tối ưu)

import com.manaplastic.backend.entity.AttendanceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceFilterCriteria {

    private Integer month;
    private Integer year;
    private Integer departmentId;
    private Integer userId;
    private AttendanceEntity.AttendanceStatus status;
}
