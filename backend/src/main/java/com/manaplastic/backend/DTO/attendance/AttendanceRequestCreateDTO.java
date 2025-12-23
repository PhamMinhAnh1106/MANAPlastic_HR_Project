package com.manaplastic.backend.DTO.attendance;


import com.manaplastic.backend.entity.AttendanceRequestEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceRequestCreateDTO {
        private LocalDate date;
        private Integer shiftId;
        private AttendanceRequestEntity.RequestType requestType;
        private LocalDateTime checkInTime;
        private LocalDateTime checkOutTime;
        private String imgProof;
        private String reason;

}
