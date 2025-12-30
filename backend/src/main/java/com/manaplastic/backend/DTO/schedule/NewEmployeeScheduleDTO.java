package com.manaplastic.backend.DTO.schedule;


import lombok.Data;

import java.time.LocalDate;

@Data
public class NewEmployeeScheduleDTO {
//    Integer employeeId;
    String username;
    LocalDate startDate;
    Integer shiftId;
}
