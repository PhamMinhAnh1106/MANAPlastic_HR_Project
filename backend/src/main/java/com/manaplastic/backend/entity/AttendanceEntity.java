package com.manaplastic.backend.entity;

import jakarta.persistence.*;
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
@Table(name = "attendances")
public class AttendanceEntity {

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
    private Instant checkin;

    @Column(name = "checkout")
    private Instant checkout;

    @Column(name = "checkin_img_url")
    private String checkinImgUrl;

    @Column(name = "checkout_img_url")
    private String checkoutImgUrl;

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
