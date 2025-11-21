package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "salaryformulas")
public class SalaryformulaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FormulaID", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "Code", nullable = false, length = 50)
    private String code;

    @NotNull
    @Lob
    @Column(name = "Calculation", nullable = false)
    private String calculation;

    @NotNull
    @Lob
    @Column(name = "Type", nullable = false)
    private String type;

    @ColumnDefault("0")
    @Column(name = "Priority")
    private Integer priority;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

}