package com.manaplastic.backend.DTO.criteria;
import com.manaplastic.backend.entity.AttendanceRequestEntity;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AttendanceRequestFilterCriteria {
    private Integer userId;
    private Integer departmentId;
    private AttendanceRequestEntity.RequestStatus status;
    private LocalDate fromDate;
    private LocalDate toDate;
}