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
@Table(name = "insurancesettings")
public class InsurancesettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SettingID", nullable = false)
    private Integer id;

    @NotNull
    @Lob
    @Column(name = "Type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "EmployeeRate", nullable = false, precision = 5, scale = 2)
    private BigDecimal employeeRate;

    @NotNull
    @Column(name = "CompanyRate", nullable = false, precision = 5, scale = 2)
    private BigDecimal companyRate;

    @NotNull
    @Column(name = "EffectiveDate", nullable = false)
    private LocalDate effectiveDate;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

}