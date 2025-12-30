package com.manaplastic.backend.DTO.payroll;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RewardPunishmentDTO {
    private Integer rewaid;
    private Integer userID;
    private String userName;
    private String type; // "REWARD" hoặc "PUNISHMENT"
    private String reason;
    private LocalDate decisionDate;
    private BigDecimal amount;
    private Boolean isTaxExempt;
    private String status; // "PENDING", "APPROVED"...
}
