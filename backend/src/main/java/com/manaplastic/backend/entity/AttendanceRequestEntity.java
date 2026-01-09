package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attendancerequests")
public class AttendanceRequestEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "managerApproverID")
    private UserEntity managerApproverID;

    @Column(name = "managerApprovedAt")
    private LocalDateTime managerApprovedAt;

    @Lob
    @Column(name = "rejectReason")
    private String rejectReason;

    @Column(name = "HRAprprovedAt")
    private LocalDateTime hrApprovedAt;

    public enum RequestType {CHECK_IN, CHECK_OUT, FULL_SHIFT}

    public enum RequestStatus {
        PENDING_MANAGER, // Chờ quản lý duyệt
        PENDING_HR,      // Quản lý đã duyệt, chờ HR chốt
        APPROVED,        // Đã hoàn tất
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requestid", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private UserEntity userid;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "shiftid")
    private ShiftEntity shiftid;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "requesttype", nullable = false)
    private RequestType requesttype;

    @Column(name = "checkintime")
    private LocalDateTime checkintime;

    @Column(name = "checkouttime")
    private LocalDateTime checkouttime;

    @Size(max = 255)
    @Column(name = "imgproof")
    private String imgproof;

    @Lob
    @Column(name = "reason")
    private String reason;

    @ColumnDefault("'PENDING'")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "approverid")
    private UserEntity approverid;

    @Lob
    @Column(name = "comment")
    private String comment;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdat")
    private LocalDateTime createdat;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updatedat")
    private LocalDateTime updatedat;

    @PrePersist
    protected void onCreate() {
        this.createdat = LocalDateTime.now();
        if (this.status == null) this.status = RequestStatus.PENDING_MANAGER;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedat = LocalDateTime.now();
    }
}