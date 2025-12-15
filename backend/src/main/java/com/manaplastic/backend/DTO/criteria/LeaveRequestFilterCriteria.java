package com.manaplastic.backend.DTO.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

//public record LeaveRequestFilterCriteria (
//        Integer departmentId,
//        String username,
//        String status,
//        LocalDate fromDate,
//        LocalDate toDate
//) {
//} ==> chưa quen dùng
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestFilterCriteria {

    private Integer departmentId;
    private String username;
    private String status;

    // Mặc định Spring hiểu format yyyy-MM-dd (ISO),
    // nhưng nếu cần chắc chắn bạn có thể thêm annotation này:
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;
}
