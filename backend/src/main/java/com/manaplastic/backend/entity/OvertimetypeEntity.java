package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "overtimetypes")
public class OvertimetypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OvertimeTypeID", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "OtCode", nullable = false, length = 20)
    private String otCode;

    @Size(max = 100)
    @NotNull
    @Column(name = "OtName", nullable = false, length = 100)
    private String otName;

    @NotNull
    @Column(name = "Rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;

    @ColumnDefault("1")
    @Column(name = "IsTaxExemptPart")
    private Boolean isTaxExemptPart;

    @ColumnDefault("'MULTIPLIER'")
    @Lob
    @Column(name = "CalculationType")
    private String calculationType;

    @ColumnDefault("'EXCESS_ONLY'")
    @Lob
    @Column(name = "TaxExemptFormula")
    private String taxExemptFormula;

    @ColumnDefault("0.00")
    @Column(name = "TaxExemptPercentage", precision = 5, scale = 2)
    private BigDecimal taxExemptPercentage;

    @Lob
    @Column(name = "Description")
    private String description;

}