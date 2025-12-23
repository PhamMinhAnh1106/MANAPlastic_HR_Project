package com.manaplastic.backend.DTO.attendance;

import com.manaplastic.backend.entity.AttendanceRequestEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
    public class AttendanceRequestResponseDTO {
        private Integer requestId;
        private Integer userId;
        private String employeeName;      // Tên nhân viên
        private String departmentName;    // Tên phòng ban
        private LocalDate date;
        private String shiftName;
        private AttendanceRequestEntity.RequestType requestType;
        private LocalDateTime checkInTime;
        private LocalDateTime checkOutTime;
        private String imgProof;
        private String reason;
        private AttendanceRequestEntity.RequestStatus status;
        private String approverName;      // Tên người duyệt
        private String comment;           // Lý do từ chối/duyệt
        private LocalDateTime createdAt;

}
