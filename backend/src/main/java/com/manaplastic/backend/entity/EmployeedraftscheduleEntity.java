package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "employeedraftschedule")
public class EmployeedraftscheduleEntity {
    @Id
    @Column(name = "draftID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "employeeID", nullable = false)
    private UserEntity employeeID;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "shiftID")
    private ShiftEntity shiftID;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "isdayoff", nullable = false)
    private Boolean isDayOff = false;

    @Size(max = 7)
    @NotNull
    @Column(name = "monthyear", nullable = false, length = 7)
    private String monthYear;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "registrationdate")
    private Instant registrationDate;


}