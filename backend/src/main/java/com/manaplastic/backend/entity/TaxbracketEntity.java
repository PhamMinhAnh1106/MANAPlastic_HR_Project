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
@Table(name = "taxbrackets")
public class TaxbracketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BracketID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "Level", nullable = false)
    private Integer level;

    @NotNull
    @Column(name = "MinIncome", nullable = false, precision = 15, scale = 2)
    private BigDecimal minIncome;

    @Column(name = "MaxIncome", precision = 15, scale = 2)
    private BigDecimal maxIncome;

    @NotNull
    @Column(name = "TaxRate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate;

    @ColumnDefault("0.00")
    @Column(name = "SubtractionAmount", precision = 15, scale = 2)
    private BigDecimal subtractionAmount;

    @NotNull
    @Column(name = "EffectiveDate", nullable = false)
    private LocalDate effectiveDate;

}