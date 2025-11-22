package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "monthlypayrollconfigs")
public class MonthlypayrollconfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConfigID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "Month", nullable = false)
    private Integer month;

    @NotNull
    @Column(name = "Year", nullable = false)
    private Integer year;

    @NotNull
    @Column(name = "CycleStartDate", nullable = false)
    private LocalDate cycleStartDate;

    @NotNull
    @Column(name = "CycleEndDate", nullable = false)
    private LocalDate cycleEndDate;

    @ColumnDefault("26.00")
    @Column(name = "StandardWorkDays", precision = 4, scale = 2)
    private BigDecimal standardWorkDays;

}