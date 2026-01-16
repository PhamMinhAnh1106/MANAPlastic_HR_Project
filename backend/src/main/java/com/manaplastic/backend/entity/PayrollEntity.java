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


    @ColumnDefault("0")
    @Column(name = "actualworkdays")
    private BigDecimal actualworkdays;

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

    @ColumnDefault("'DRAFT'")
    @Lob
    @Column(name = "status")
    private String status;

    @ColumnDefault("0.00")
    @Column(name = "basesalarysnapshot", precision = 15, scale = 2)
    private BigDecimal basesalarysnapshot;

    @ColumnDefault("0.00")
    @Column(name = "insurancesalarysnapshot", precision = 15, scale = 2)
    private BigDecimal insurancesalarysnapshot;

    @ColumnDefault("26.00")
    @Column(name = "standardworkdays", precision = 4, scale = 2)
    private BigDecimal standardworkdays;

    @ColumnDefault("0.00")
    @Column(name = "othours", precision = 5, scale = 2)
    private BigDecimal othours;

    @ColumnDefault("0")
    @Column(name = "dependentcount")
    private Integer dependentcount;

}