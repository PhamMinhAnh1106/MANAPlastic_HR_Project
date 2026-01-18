package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "overtimerequests")
public class OvertimeRequestEntity {
    public enum RequestStatus {
        PENDING_MANAGER,      // Chờ Manager duyệt (Đơn thường)
        PENDING_CONFIRMATION, // Chờ Manager xác nhận (Đơn tự sinh)
        PENDING_HR,           // Chờ HR
        APPROVED,             // Đã xong
        REJECTED              // Từ chối
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requestID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userid", nullable = false)
    private UserEntity userid;

    @ManyToOne
    @JoinColumn(name = "departmentID")
    private DepartmentEntity departmentid;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "shiftID")
    private ShiftEntity shiftID;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "endTime")
    private LocalDateTime endTime;

    @ColumnDefault("0")
    @Column(name = "totalHours")
    private Double totalHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private RequestStatus status;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "isSystemGenerated", nullable = false)
    private Boolean isSystemGenerated = false;

    @Column(name = "actualCheckOut")
    private LocalDateTime actualCheckOut;

    @Column(name = "finalPaidHours")
    private Double finalPaidHours;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "managerApproverID")
    private UserEntity managerApproverID;

    @Column(name = "managerApprovedAt")
    private LocalDateTime managerApprovedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "hrApproverID")
    private UserEntity hrApproverID;

    @Column(name = "hrApprovedAt")
    private LocalDateTime hrApprovedAt;

    @Lob
    @Column(name = "rejectReason")
    private String rejectReason;

    @Lob
    @Column(name = "reason")
    private String reason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "requestID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OvertimeRequestDetailEntity> details = new ArrayList<>();

    // Hàm helper để thêm detail đúng cách (Gán 2 chiều)
    public void addDetail(OvertimeRequestDetailEntity detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);
        detail.setRequestID(this);
    }

}