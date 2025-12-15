package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "salary_variable_cache")
public class SalaryVariableCacheEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cache_id", nullable = false)
    private Integer id;

    @Column(name = "variable_id")
    private Integer variableId;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Size(max = 7)
    @Column(name = "payperiod", length = 7)
    private String payperiod;

    @Column(name = "value", precision = 20, scale = 4)
    private BigDecimal value;

    @Column(name = "evaluated_at")
    private Instant evaluatedAt;

    @Column(name = "rule_id")
    private Integer ruleId;

}