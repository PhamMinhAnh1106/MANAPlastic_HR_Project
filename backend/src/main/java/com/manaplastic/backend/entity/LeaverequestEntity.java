package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "leaverequests")
public class LeaverequestEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "shiftID")
    private ShiftEntity shiftID;

    public enum LeaverequestStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum LeaveType {
        ANNUAL, SICK, MATERNITY, PATERNITY, UNPAID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leaverequestID", nullable = false)
    private Integer id;

    @ColumnDefault("'ANNUAL'")
    @Enumerated(EnumType.STRING)
    @Column(name = "leavetype", nullable = false)
    private LeaveType leavetype;

    @Column(name = "startdate", nullable = false)
    private LocalDate startdate;

    @Column(name = "enddate", nullable = false)
    private LocalDate enddate;

    @Lob
    @Column(name = "reason")
    private String reason;

    @ColumnDefault("'PENDING'")
    @Lob
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaverequestStatus status;

    @Column(name = "requestdate")
    private LocalDate requestdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID")
    private com.manaplastic.backend.entity.UserEntity userID;

}