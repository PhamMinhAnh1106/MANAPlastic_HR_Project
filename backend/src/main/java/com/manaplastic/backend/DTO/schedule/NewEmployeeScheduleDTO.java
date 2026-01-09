package com.manaplastic.backend.DTO.schedule;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class NewEmployeeScheduleDTO {
    private String username;
    // Khoảng thời gian áp dụng
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer shiftId;

    // Các ngày ngoại lệ (đổi ca hoặc cho nghỉ phép)
    private List<DraftRegistrationDTO> specificDays;
}