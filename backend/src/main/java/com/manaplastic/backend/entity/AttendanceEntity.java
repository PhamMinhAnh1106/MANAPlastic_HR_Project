package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "attendances")
public class AttendanceEntity {


    @ColumnDefault("0.00")
    @Column(name = "estimated_salary", precision = 15, scale = 4)
    private BigDecimal estimatedSalary;

    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE_AND_EARLY,ON_LEAVE, MISSING_OUTPUT_DATA, MISSING_INPUT_DATA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendanceID", nullable = false)
    private Integer id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "checkin")
    private LocalDateTime checkin;

    @Column(name = "checkout")
    private LocalDateTime checkout;

    @Column(name = "checkinimgurl")
    private String checkinImgUrl;

    @Column(name = "checkoutimgurl")
    private String checkoutImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "checkInLogID")
    private AttendancelogEntity checkInLogID;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "checkOutLogID")
    private AttendancelogEntity checkOutLogID;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'absent'")
    @Lob
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shiftID")
    private com.manaplastic.backend.entity.ShiftEntity shiftID;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID")
    private com.manaplastic.backend.entity.UserEntity userID;

}
