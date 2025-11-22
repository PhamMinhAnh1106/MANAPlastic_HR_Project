package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "contractallowances")
public class ContractallowanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ContractAllowanceID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ContractID", nullable = false)
    private ContractEntity contractID;

    @Size(max = 100)
    @NotNull
    @Column(name = "AllowanceName", nullable = false, length = 100)
    private String allowanceName;

    @NotNull
    @ColumnDefault("0.00")
    @Column(name = "Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @ColumnDefault("1")
    @Column(name = "IsTaxable")
    private Boolean isTaxable;

    @ColumnDefault("0")
    @Column(name = "IsInsuranceBase")
    private Boolean isInsuranceBase;

}