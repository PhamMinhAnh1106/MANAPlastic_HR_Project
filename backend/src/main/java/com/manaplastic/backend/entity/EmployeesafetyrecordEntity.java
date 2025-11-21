package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "employeesafetyrecords")
public class EmployeesafetyrecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecordID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "UserID", nullable = false)
    private UserEntity userID;

    @NotNull
    @Lob
    @Column(name = "RecordType", nullable = false)
    private String recordType;

    @NotNull
    @Column(name = "DateCompleted", nullable = false)
    private LocalDate dateCompleted;

    @ColumnDefault("0.00")
    @Column(name = "Cost", precision = 15, scale = 2)
    private BigDecimal cost;

    @Lob
    @Column(name = "Notes")
    private String notes;

}