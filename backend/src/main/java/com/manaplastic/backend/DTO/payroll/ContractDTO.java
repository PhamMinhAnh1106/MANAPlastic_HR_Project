package com.manaplastic.backend.DTO.payroll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


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
        String username,
        List<ContractsAllowanceDTO> allowances) {

}
