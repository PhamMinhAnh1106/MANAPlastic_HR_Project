package com.manaplastic.backend.Entity;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leaverequestID", nullable = false)
    private Integer id;

    @Column(name = "leavetype", length = 100)
    private String leavetype;

    @Column(name = "startdate", nullable = false)
    private LocalDate startdate;

    @Column(name = "enddate", nullable = false)
    private LocalDate enddate;

    @Lob
    @Column(name = "reason")
    private String reason;

    @ColumnDefault("'pending'")
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "requestdate")
    private LocalDate requestdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID")
    private com.manaplastic.backend.Entity.UserEntity userID;

}