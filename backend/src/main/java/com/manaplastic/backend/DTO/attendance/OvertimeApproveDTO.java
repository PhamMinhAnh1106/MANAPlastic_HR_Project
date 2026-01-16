package com.manaplastic.backend.DTO.attendance;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class OvertimeApproveDTO {
    private List<OvertimeDetailUpdateDTO> details;
    private String note;

}

