package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "taxsettings")
public class TaxsettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SettingID", nullable = false)
    private Integer id;

    @NotNull
    @Lob
    @Column(name = "SettingKey", nullable = false)
    private String settingKey;

    @NotNull
    @Column(name = "Value", nullable = false, precision = 15, scale = 4)
    private BigDecimal value;

    @NotNull
    @Column(name = "EffectiveDate", nullable = false)
    private LocalDate effectiveDate;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = false;

    @Size(max = 255)
    @Column(name = "Description")
    private String description;

}