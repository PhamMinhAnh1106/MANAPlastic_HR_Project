package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "payrolldetails")
public class PayrolldetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paydetailID", nullable = false)
    private Integer id;

    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "payID")
    private com.manaplastic.backend.entity.PayrollEntity payID;

}