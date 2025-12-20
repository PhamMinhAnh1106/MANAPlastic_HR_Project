package com.manaplastic.backend.entity;

import com.manaplastic.backend.constant.LogType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "activitylogs")
public class ActivitylogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logID", nullable = false)
    private Integer id;

    @Column(name = "action", nullable = false)
    private String action;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "actiontime")
    private LocalDateTime actiontime;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "userID")
    private com.manaplastic.backend.entity.UserEntity userID;

    @ColumnDefault("'INFO'")
    @Enumerated(EnumType.STRING)
    @Column(name = "logType")
    private LogType logType;

    @Lob
    @Column(name = "details")
    private String details;

    @Size(max = 50)
    @Column(name = "username", length = 50)
    private String username;

}