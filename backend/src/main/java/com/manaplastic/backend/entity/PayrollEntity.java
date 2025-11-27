package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "payrolls")
public class PayrollEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payID", nullable = false)
    private Integer id;

    @Column(name = "basesalary", nullable = false, precision = 15, scale = 2)
    private BigDecimal basesalary;

    @ColumnDefault("0.00")
    @Column(name = "bonus", precision = 15, scale = 2)
    private BigDecimal bonus;

    @ColumnDefault("0.00")
    @Column(name = "penalty", precision = 15, scale = 2)
    private BigDecimal penalty;

    @Column(name = "hoursofwork")
    private Float hoursofwork;

    @ColumnDefault("0.00")
    @Column(name = "PIT", precision = 15, scale = 2)
    private BigDecimal pit;

    @Column(name = "netsalary", nullable = false, precision = 15, scale = 2)
    private BigDecimal netsalary;

    @Column(name = "payperiod", nullable = false, length = 7)
    private String payperiod;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID")
    private com.manaplastic.backend.entity.UserEntity userID;

    @ColumnDefault("0.00")
    @Column(name = "totalincome", precision = 15, scale = 2)
    private BigDecimal totalincome;

    @ColumnDefault("0.00")
    @Column(name = "totalinsuranceemployee", precision = 15, scale = 2)
    private BigDecimal totalinsuranceemployee;

    @ColumnDefault("0.00")
    @Column(name = "assessableincome", precision = 15, scale = 2)
    private BigDecimal assessableincome;

    @ColumnDefault("0.00")
    @Column(name = "taxableincome", precision = 15, scale = 2)
    private BigDecimal taxableincome;

    @ColumnDefault("0")
    @Column(name = "actualworkdays")
    private Double actualworkdays;

    @ColumnDefault("0.00")
    @Column(name = "totalovertimepay", precision = 15, scale = 2)
    private BigDecimal totalovertimepay;

    @ColumnDefault("0.00")
    @Column(name = "totalallowance", precision = 15, scale = 2)
    private BigDecimal totalallowance;

    @Column(name = "insurancebase", precision = 15, scale = 2)
    private BigDecimal insurancebase;

    @Column(name = "bhxh_emp", precision = 15, scale = 2)
    private BigDecimal bhxhEmp;

    @Column(name = "bhyt_emp", precision = 15, scale = 2)
    private BigDecimal bhytEmp;

    @Column(name = "bhtn_emp", precision = 15, scale = 2)
    private BigDecimal bhtnEmp;

    @Column(name = "bhxh_comp", precision = 15, scale = 2)
    private BigDecimal bhxhComp;

    @Column(name = "bhyt_comp", precision = 15, scale = 2)
    private BigDecimal bhytComp;

    @Column(name = "bhtn_comp", precision = 15, scale = 2)
    private BigDecimal bhtnComp;

    @ColumnDefault("0.00")
    @Column(name = "OtTaxExempt", precision = 15, scale = 2)
    private BigDecimal otTaxExempt;

}