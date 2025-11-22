package com.manaplastic.backend.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


public record ContractDTO(
        Integer id,
        String contractname,
        String type,
        BigDecimal basesalary,
        BigDecimal insuranceSalary,
        String allowanceToxicType,
        String fileurl,
        LocalDate signdate,
        LocalDate startdate,
        LocalDate enddate,
        String status,
        String username) {

}
