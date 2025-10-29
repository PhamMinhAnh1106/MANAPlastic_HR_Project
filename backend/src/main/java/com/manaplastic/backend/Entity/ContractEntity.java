package com.manaplastic.backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "contracts")
public class ContractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contractID", nullable = false)
    private Integer id;

    @Column(name = "contractname", nullable = false)
    private String contractname;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "basesalary", nullable = false, precision = 15, scale = 2)
    private BigDecimal basesalary;

    @Column(name = "fileurl")
    private String fileurl;

    @Column(name = "signdate", nullable = false)
    private LocalDate signdate;

    @Column(name = "startdate", nullable = false)
    private LocalDate startdate;

    @Column(name = "enddate")
    private LocalDate enddate;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID", nullable = false)
    private com.manaplastic.backend.Entity.UserEntity userID;

}