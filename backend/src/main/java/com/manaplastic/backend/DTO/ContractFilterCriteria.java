package com.manaplastic.backend.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractFilterCriteria(
        String username,
        String contractname,
        String type,
        BigDecimal basesalary,
        String status,
        LocalDate validFrom,
        LocalDate validTo,
        String allowanceToxicType
) {
}
