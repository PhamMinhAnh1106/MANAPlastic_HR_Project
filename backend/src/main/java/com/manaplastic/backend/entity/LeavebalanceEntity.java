package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "leavebalance")
public class LeavebalanceEntity {
    @EmbeddedId
    private LeavebalanceEntityId id;

    @MapsId("userID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID", nullable = false)
    private UserEntity userID;

    @MapsId("leavetypeid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "leavetypeid", nullable = false)
    private ShiftEntity leaveType;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "totalgranted", nullable = false)
    private Integer totalGranted;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "carriedover", nullable = false)
    private Integer carriedOver;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "daysused", nullable = false)
    private Integer daysUsed;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "lastupdated")
    private Instant lastUpdated;


}