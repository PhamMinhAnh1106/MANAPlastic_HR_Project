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

}