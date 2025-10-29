package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

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
    private Instant actiontime;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "userID")
    private com.manaplastic.backend.entity.UserEntity userID;

}