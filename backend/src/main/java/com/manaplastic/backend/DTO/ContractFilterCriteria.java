package com.manaplastic.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

//public record ContractFilterCriteria(
//        String username,
//        String contractname,
//        String type,
//        BigDecimal basesalary,
//        String status,
//        LocalDate validFrom,
//        LocalDate validTo,
//        String allowanceToxicType
//) {
//}
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractFilterCriteria {

    private String username;
    private String contractname;
    private String type;
    private BigDecimal basesalary;
    private String status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validTo;

    private String allowanceToxicType;
}
